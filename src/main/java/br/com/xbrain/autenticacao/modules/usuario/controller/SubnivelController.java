package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.usuario.service.SubnivelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/sub-niveis")
public class SubnivelController {

    private final SubnivelService subnivelService;

    @GetMapping("select")
    public List<SelectResponse> getSubniveisSelect(@RequestParam Integer nivelId) {
        return subnivelService.getSubniveisSelect(nivelId);
    }
}
