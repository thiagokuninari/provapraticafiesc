package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.feriado.model.FeriadoHistorico;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoHistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeriadoHistoricoService {

    @Autowired
    private FeriadoHistoricoRepository historicoRepository;

    public void salvarHistorico(Feriado feriado, String observacao, UsuarioAutenticado usuario) {
        historicoRepository.save(FeriadoHistorico.of(feriado, observacao, usuario.getId()));
    }
}
