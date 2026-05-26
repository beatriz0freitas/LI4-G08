package pt.hotel.animais.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Catálogo de tipos de serviços extra disponíveis.
 * Gerido pelo diretor através de operações CRUD.
 * Apenas tipos ativos podem ser selecionados para criação de serviços extra.
 */
@Entity
@Table(name = "tipo_servico_extra", uniqueConstraints = {
    @UniqueConstraint(columnNames = "nome", name = "uk_nome_tipo_servico")
})
@Getter
@Setter
public class TipoServicoExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, unique = true, length = 100)
    private String nome;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    public TipoServicoExtra() {
    }

    public TipoServicoExtra(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
        this.ativo = true;
    }
}
