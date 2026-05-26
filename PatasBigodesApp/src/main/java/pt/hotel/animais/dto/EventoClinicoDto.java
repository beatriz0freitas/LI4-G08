package pt.hotel.animais.dto;

import java.time.LocalDateTime;

public class EventoClinicoDto {
    private final String tipo;
    private final String badgeClass;
    private final String dotClass;
    private final LocalDateTime dataHora;
    private final String descricao;
    private final String detalhe;

    public EventoClinicoDto(String tipo,
                            String badgeClass,
                            String dotClass,
                            LocalDateTime dataHora,
                            String descricao,
                            String detalhe) {
        this.tipo = tipo;
        this.badgeClass = badgeClass;
        this.dotClass = dotClass;
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.detalhe = detalhe;
    }

    public String getTipo() {
        return tipo;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public String getDotClass() {
        return dotClass;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDetalhe() {
        return detalhe;
    }
}
