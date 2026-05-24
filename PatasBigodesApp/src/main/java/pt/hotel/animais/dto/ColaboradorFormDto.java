package pt.hotel.animais.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pt.hotel.animais.model.enums.TipoColaborador;

/**
 * DTO de formulário para criação e edição de colaboradores.
 *
 * A password é obrigatória apenas na criação; na edição, um valor vazio mantém
 * a password existente. O tipo de colaborador é uma enum fechada para impedir
 * perfis arbitrários.
 */
public class ColaboradorFormDto {

    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 80, message = "Username deve ter entre 3 e 80 caracteres")
    private String username;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    private String password;

    @NotNull(message = "Tipo de colaborador é obrigatório")
    private TipoColaborador tipoColaborador;

    private boolean ativo = true;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public TipoColaborador getTipoColaborador() { return tipoColaborador; }
    public void setTipoColaborador(TipoColaborador tipoColaborador) { this.tipoColaborador = tipoColaborador; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
