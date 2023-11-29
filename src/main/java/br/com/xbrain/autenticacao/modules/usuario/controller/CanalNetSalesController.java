package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CanalNetSales;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/usuario-canal-netsales")
public class CanalNetSalesController {

    @GetMapping
    public List<SelectResponse> getAllCanaisNetsales() {
        return Arrays.stream(CanalNetSales.values())
            .map(canalNetSales -> new SelectResponse(canalNetSales, canalNetSales.getDescricao()))
            .collect(Collectors.toList());
    }
}
