package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FeriadoService {

    @Autowired
    private FeriadoRepository repository;
    @Autowired
    private DataHoraAtual dataHoraAtual;

    public boolean consulta(String data) {
        return repository.findByDataFeriadoAndFeriadoNacional(DateUtil.parseStringToLocalDate(data), Eboolean.V).isPresent();
    }

    public boolean consulta(String data, Integer cidadeId) {
        return repository.findByDataFeriadoAndCidadeId(DateUtil.parseStringToLocalDate(data), cidadeId).isPresent();
    }

    public Feriado save(FeriadoRequest request) {
        Feriado feriado = FeriadoRequest.convertFrom(request);
        feriado.setDataCadastro(LocalDateTime.now());
        return  repository.save(feriado);
    }

    public Iterable<Feriado> findAllByAnoAtual() {
        return repository.findAllByAnoAtual(dataHoraAtual.getData());
    }
}
