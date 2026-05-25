package pt.hotel.animais.e2e;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes E2E com Playwright — abre um browser Chromium headless real contra a app em execução.
 * Requer DB de testes activa: make db-reset-test antes de correr.
 * Instalar browsers uma vez: mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlaywrightE2ETest {

    @LocalServerPort
    private int port;

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeAll
    void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions().setHeadless(true)
        );
    }

    @AfterAll
    void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void newContext() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void paginaLoginDeveCarregarFormularioDeAutenticacao() {
        page.navigate(url("/login"));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        assertThat(page.locator("form").count()).isGreaterThan(0);
        assertThat(page.locator("input[name='username']").count()).isEqualTo(1);
        assertThat(page.locator("input[name='password']").count()).isEqualTo(1);
        assertThat(page.locator("button[type='submit']").count()).isGreaterThan(0);
    }

    @Test
    void loginComCredenciaisValidasRedirecionaParaHome() {
        page.navigate(url("/login"));
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        page.fill("input[name='username']", "diretor");
        page.fill("input[name='password']", "diretor123");
        page.click("button[type='submit']");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        assertThat(page.url()).doesNotContain("/login");
        assertThat(page.locator("body").innerText()).isNotBlank();
    }

    @Test
    void paginaProtegidaSemSessaoRedirecionaParaLogin() {
        page.navigate(url("/dashboard"));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        assertThat(page.url()).contains("login");
    }

    @Test
    void loginComPasswordErradaMostraErro() {
        page.navigate(url("/login"));
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        page.fill("input[name='username']", "diretor");
        page.fill("input[name='password']", "password-errada");
        page.click("button[type='submit']");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        assertThat(page.url()).contains("login");
    }
}
