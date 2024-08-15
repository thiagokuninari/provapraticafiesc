package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.usuario.service.SubNivelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/sub-niveis")
public class SubNivelController {

    private final SubNivelService subNivelService;

    @GetMapping("select")
    public List<SelectResponse> getSubNiveisSelect(@RequestParam Integer nivelId) {
        return subNivelService.getSubNiveisSelect(nivelId);
    }
}
