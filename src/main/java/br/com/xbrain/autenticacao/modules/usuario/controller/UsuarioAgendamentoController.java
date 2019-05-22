package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.AgendamentoDistribuicaoListagemResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.AgendamentoDistribuicaoRequest;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioAgendamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/usuarios/agendamentos")
public class UsuarioAgendamentoController {

    private final UsuarioAgendamentoService usuarioAgendamentoService;

    @GetMapping("{usuarioId}")
    public List<AgendamentoDistribuicaoListagemResponse> getAgendamentoDistribuicaoDoUsuario(@PathVariable Integer usuarioId) {
        return usuarioAgendamentoService.getAgendamentoDistribuicaoDoUsuario(usuarioId);
    }

    @PostMapping
    public void distribuirAgendamentosDoUsuario(@Validated @RequestBody AgendamentoDistribuicaoRequest request) {
        usuarioAgendamentoService.distribuirAgendamentosDoUsuario(request);
    }
}
