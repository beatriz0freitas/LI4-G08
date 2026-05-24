package pt.hotel.animais.service;

import pt.hotel.animais.dto.RelatorioFiltroFormDto;
import pt.hotel.animais.dto.RelatorioResumoDto;

public interface IRelatorioService {
    RelatorioResumoDto gerarRelatorio(RelatorioFiltroFormDto filtro);
    String gerarCsv(RelatorioFiltroFormDto filtro);
    byte[] gerarPdf(RelatorioFiltroFormDto filtro);
}
