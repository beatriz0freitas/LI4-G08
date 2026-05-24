package pt.hotel.animais.service;

import pt.hotel.animais.dto.RelatorioFiltroFormDto;
import pt.hotel.animais.dto.RelatorioResumoDto;

/**
 * Contrato de aplicação para relatórios operacionais e financeiros.
 */
public interface IRelatorioService {
    /**
     * Gera o resumo agregado para apresentação na interface.
     *
     * @param filtro filtros aplicados ao relatório
     * @return métricas agregadas
     */
    RelatorioResumoDto gerarRelatorio(RelatorioFiltroFormDto filtro);

    /**
     * Gera conteúdo CSV para download.
     *
     * @param filtro filtros aplicados ao relatório
     * @return texto CSV
     */
    String gerarCsv(RelatorioFiltroFormDto filtro);

    /**
     * Gera conteúdo PDF para download.
     *
     * @param filtro filtros aplicados ao relatório
     * @return bytes do documento
     */
    byte[] gerarPdf(RelatorioFiltroFormDto filtro);
}
