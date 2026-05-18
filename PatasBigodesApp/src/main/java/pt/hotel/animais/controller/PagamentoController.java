package pt.hotel.animais.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.hotel.animais.dto.PagamentoDto;
import pt.hotel.animais.model.Pagamento;
import pt.hotel.animais.service.PagamentoService;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    public ResponseEntity<Pagamento> registrar(@RequestBody PagamentoDto dto) {
        Pagamento p = pagamentoService.registrarPagamento(dto);
        return ResponseEntity.ok(p);
    }
}
