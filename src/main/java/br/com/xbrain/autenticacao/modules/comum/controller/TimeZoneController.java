package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/timezone")
public class TimeZoneController {

    @GetMapping
    public Iterable<SelectResponse> getTimeZones() {
        return Arrays.stream(ETimeZone.values())
                .map(item -> SelectResponse.convertFrom(item.name(), item.toString()))
                .sorted(Comparator.comparing(SelectResponse::getLabel))
                .collect(Collectors.toList());
    }
}
