package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.usuario.service.SubNivelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/sub-niveis")
public class SubNivelController {

    private final SubNivelService subNivelService;

    @GetMapping("{nivelId}/select")
    public List<SelectResponse> getSubNiveisSelect(@PathVariable Integer nivelId) {
        return subNivelService.getSubNiveisSelect(nivelId);
    }
}
