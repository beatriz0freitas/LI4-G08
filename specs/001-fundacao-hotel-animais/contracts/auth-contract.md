# API Contract: Authentication (Phase 1)

**Version**: 1.0 | **Status**: Specification | **Conformance**: [UC-01](../../../../docs/Etapa1/03-use-cases/UC-01.md)

---

## Overview

The Authentication contract defines the login/logout workflow and session management for Phase 1. All endpoints return HTML (Thymeleaf form submissions) except where explicitly noted.

---

## Endpoints

### 1. GET /login

**Purpose**: Display login form

**Request**:
```http
GET /login HTTP/1.1
Host: localhost:8080
```

**Response** (200 OK):
```http
HTTP/1.1 200 OK
Content-Type: text/html;charset=UTF-8

<!DOCTYPE html>
<html lang="pt">
<head>
    <title>Login — Hotel Animais</title>
</head>
<body>
    <form method="post" action="/login">
        <input type="text" name="username" placeholder="Utilizador" required>
        <input type="password" name="password" placeholder="Senha" required>
        <button type="submit">Entrar</button>
    </form>
</body>
</html>
```

**Error Responses**:
- **302 Redirect**: If already authenticated, redirect to `/dashboard`

---

### 2. POST /login

**Purpose**: Authenticate user with username and password

**Request**:
```http
POST /login HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded

username=diretor&password=password
```

**Response — Success** (302 Found):
```http
HTTP/1.1 302 Found
Location: /dashboard
Set-Cookie: JSESSIONID=ABC123DEF456; Path=/; HttpOnly; Secure
```

**Response — Failure (Invalid Credentials)** (302 Found):
```http
HTTP/1.1 302 Found
Location: /login?error=invalid_credentials

<!-- OR render login.html with error message -->
HTTP/1.1 200 OK
Content-Type: text/html
<!-- Form with error: "Utilizador ou senha incorretos" -->
```

**Response — Locked Account** (403 Forbidden):
```http
HTTP/1.1 403 Forbidden
Content-Type: text/html

<!-- Error: Account locked (not in Phase 1, but placeholder for future) -->
```

**Credentials (In-Memory, Phase 1)**:
```
| Username | Password | Tipo Colaborador | Status |
|----------|----------|------------------|--------|
| diretor | password | DIRETOR | ✅ ACTIVE |
| recepcao | password | FUNCIONARIO_RECEPCAO | ✅ ACTIVE |
| cuidador | password | CUIDADOR | ✅ ACTIVE |
| veterinario | password | MEDICO_VETERINARIO | ✅ ACTIVE |
| limpeza | password | RESPONSAVEL_LIMPEZA | ✅ ACTIVE |
```

**Notes**:
- Passwords are hashed with BCrypt (strength 12) in `SecurityConfig`
- Session is stored in HTTP cookie: `JSESSIONID`
- Valid duration: 30 minutes (configurable in `application.properties`)
- CSRF token is included in form (Spring Security default)

---

### 3. POST /logout

**Purpose**: Invalidate session and logout user

**Request**:
```http
POST /logout HTTP/1.1
Host: localhost:8080
Cookie: JSESSIONID=ABC123DEF456
```

**Response** (302 Found):
```http
HTTP/1.1 302 Found
Location: /login
Set-Cookie: JSESSIONID=; MaxAge=0; Path=/
```

**Post-Logout**:
- Session is invalidated server-side
- Cookie is deleted client-side
- User redirected to login page
- Any subsequent request without valid session → 302 to `/login`

---

## Session Management

### Session Lifecycle

```
1. User visits /login
   → GET /login (200 OK, login form)

2. User submits credentials
   → POST /login (username=X, password=Y)

3. Server validates credentials against in-memory UserDetailsService
   → BCrypt.matches(password, hashedPassword)

4. If valid:
   a. Create session (JSESSIONID)
   b. Store user principal in session
   c. 302 redirect to /dashboard

5. User issued JSESSIONID cookie
   → Subsequent requests include cookie automatically

6. User clicks Logout
   → POST /logout
   → Session invalidated, cookie deleted
   → 302 redirect to /login
```

### Cookie Details

```
Name: JSESSIONID
Path: /
HttpOnly: true (prevents XSS access)
Secure: true (HTTPS only in production)
Max-Age: 1800 (30 minutes)
SameSite: Strict (CSRF protection)
```

### CSRF Protection

- All POST requests include CSRF token
- Token is embedded in login form (Thymeleaf)
- Spring Security validates token on each POST

**Example Form**:
```html
<form method="post" action="/login">
    <input type="hidden" name="_csrf" value="...token...">
    <input type="text" name="username">
    <input type="password" name="password">
    <button type="submit">Entrar</button>
</form>
```

---

## Error Handling

| Scenario | HTTP Status | Response | Notes |
|----------|------------|----------|-------|
| Invalid username/password | 302 → /login?error | Redirect to login with error param | Form displays "Credenciais inválidas" |
| Missing username | 302 → /login?error | Redirect to login | Form displays "Utilizador é obrigatório" |
| Missing password | 302 → /login?error | Redirect to login | Form displays "Senha é obrigatória" |
| Session expired | 302 → /login | Redirect to login | Transparent to user |
| Unauthorized access (403) | 403 | "Access Denied" page | Attempt to access /dashboard without auth |

---

## Security Constraints

**Implemented in Phase 1**:
- ✅ BCrypt hashing (12 rounds)
- ✅ Spring Security filters
- ✅ HTTP Session (stateful)
- ✅ Role-based access control (5 tipos)
- ✅ CSRF token validation

**Not in Phase 1** (Future Phases):
- ❌ Two-factor authentication
- ❌ OAuth2 / OpenID Connect
- ❌ Account lockout after failed attempts
- ❌ Password renewal / reset
- ❌ Audit logging of login attempts

---

## Testing

### Unit Test Example

```java
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class AuthControllerTest {
    @Autowired MockMvc mockMvc;
    
    @Test
    void testLoginWithValidCredentials() throws Exception {
        mockMvc.perform(post("/login")
            .param("username", "diretor")
            .param("password", "password")
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/dashboard"));
    }
    
    @Test
    void testLoginWithInvalidPassword() throws Exception {
        mockMvc.perform(post("/login")
            .param("username", "diretor")
            .param("password", "wrong")
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login?error"));
    }
}
```

### Integration Test Example

```java
@SpringBootTest
class AuthIntegrationTest {
    @Autowired TestRestTemplate restTemplate;
    
    @Test
    void testLoginLogoutCycle() {
        // Step 1: GET /login
        ResponseEntity<String> loginPage = restTemplate.getForEntity("/login", String.class);
        assertTrue(loginPage.getBody().contains("Entrar"));
        
        // Step 2: POST /login
        // ... (omitted for brevity)
        
        // Step 3: POST /logout
        // ... (omitted for brevity)
    }
}
```

---

## Related Artifacts

- **Use Case**: [UC-01 — Autenticar no Sistema](../../../../docs/Etapa1/03-use-cases/UC-01.md)
- **Data Model**: [data-model.md](../data-model.md) (TipoColaborador enum)
- **Architecture**: [ADR-04 — Spring Security + Sessão HTTP](../../../../docs/Etapa2/04-architecture-decisions/ADR-04-spring-security-sessao-http.md)
- **Template**: `templates/auth/login.html`

---

**Contract Status**: ✅ **Approved for implementation**
