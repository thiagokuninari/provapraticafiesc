package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.service.FuncionalidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "api/funcionalidades")
@RequiredArgsConstructor
public class FuncionalidadeController {

    private final HttpServletRequest request;
    private final FuncionalidadeService service;

    @GetMapping
    public List<FuncionalidadeResponse> getAll() {
        return service.getAll(request);
    }
}
