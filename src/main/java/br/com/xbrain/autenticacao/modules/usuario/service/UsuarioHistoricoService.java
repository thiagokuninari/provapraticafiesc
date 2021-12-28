package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHistoricoDto;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.DEMISSAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.INATIVADO_SEM_ACESSO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.EObservacaoHistorico.INATIVACAO_AA;

@Service
public class UsuarioHistoricoService {

    @Autowired
    private UsuarioHistoricoRepository usuarioHistoricoRepository;
    @Autowired
    private MotivoInativacaoService motivoInativacaoService;

    public List<UsuarioHistoricoDto> getHistoricoDoUsuario(Integer usuarioId) {
        return usuarioHistoricoRepository
                .getHistoricoDoUsuario(usuarioId)
                .stream()
                .map(UsuarioHistoricoDto::of)
                .collect(Collectors.toList());
    }

    public void gerarHistoricoInativacao(Usuario usuario, String origem) {
        usuarioHistoricoRepository.save(UsuarioHistorico.gerarHistorico(
                usuario.getId(), getMotivoInativacao(), origem, ESituacao.I
        ));
    }

    public void gerarHistoricoDeInativacaoPorAgenteAutorizado(Integer usuarioId) {
        usuarioHistoricoRepository.save(UsuarioHistorico
            .gerarHistorico(usuarioId, motivoInativacaoService
                .findByCodigoMotivoInativacao(DEMISSAO), INATIVACAO_AA.getObservacao(), ESituacao.I));
    }

    private MotivoInativacao getMotivoInativacao() {
        return motivoInativacaoService.findByCodigoMotivoInativacao(INATIVADO_SEM_ACESSO);
    }

    @Transactional
    public void save(List<UsuarioHistorico> historicos) {
        usuarioHistoricoRepository.save(historicos);
    }

    @Transactional
    public void save(UsuarioHistorico historico) {
        usuarioHistoricoRepository.save(historico);
    }
}
