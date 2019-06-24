package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.repository.MotivoInativacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MotivoInativacaoService {

    private static final NotFoundException MOTIVO_INATIVACAO_NOT_FOUND = new NotFoundException(
            "Motivo de inativação não encontrado.");

    @Autowired
    private MotivoInativacaoRepository motivoInativacaoRepository;

    public MotivoInativacao findById(Integer id) {
        return motivoInativacaoRepository.findOne(id);
    }

    public MotivoInativacao findByCodigoMotivoInativacao(CodigoMotivoInativacao motivo) {
        return motivoInativacaoRepository.findByCodigo(motivo)
                .orElseThrow(() -> MOTIVO_INATIVACAO_NOT_FOUND);
    }
}
