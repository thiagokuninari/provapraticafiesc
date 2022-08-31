package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.dto.DiasUteisRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.DiasUteisRequestCidadeUf;
import br.com.xbrain.autenticacao.modules.comum.util.DiasUteisAdjuster;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DiasUteisService {

    @Autowired
    private FeriadoRepository feriadoRepository;

    public LocalDateTime getDataComDiasUteisAdicionado(DiasUteisRequest request) {
        var datasFeriadosDaCidade = feriadoRepository.findAllDataFeriadoByCidadeId(request.getCidadeId());
        return request.getDataOriginal().with(new DiasUteisAdjuster(request.getQtdDiasUteisAdicionar(), datasFeriadosDaCidade));
    }

    public LocalDateTime getDataComDiasUteisAdicionadoECidadeUf(DiasUteisRequestCidadeUf request) {
        var datasFeriadosDaCidade = feriadoRepository.findAllDataFeriadoByCidadeEUf(request.getCidade(), request.getUf());
        return request.getDataOriginal().with(new DiasUteisAdjuster(request.getQtdDiasUteisAdicionar(), datasFeriadosDaCidade));
    }
}
