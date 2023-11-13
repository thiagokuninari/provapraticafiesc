package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHistoricoDto;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.DEMISSAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.INATIVADO_SEM_ACESSO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.ORGANIZACAO_EMPRESA_INATIVA;
import static br.com.xbrain.autenticacao.modules.usuario.enums.EObservacaoHistorico.INATIVACAO_AA;
import static br.com.xbrain.autenticacao.modules.usuario.enums.EObservacaoHistorico.INATIVACAO_ORGANIZACAO;

@Service
@SuppressWarnings("PMD.TooManyStaticImports")
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

    public void gerarHistoricoInativacao(Integer usuarioId, String origem) {
        usuarioHistoricoRepository.save(UsuarioHistorico.gerarHistorico(
            usuarioId, getMotivoInativacao(), origem, ESituacao.I
        ));
    }

    public void gerarHistoricoDeInativacaoPorAgenteAutorizado(Integer usuarioId) {
        usuarioHistoricoRepository.save(UsuarioHistorico
            .gerarHistorico(usuarioId, motivoInativacaoService
                .findByCodigoMotivoInativacao(DEMISSAO), INATIVACAO_AA.getObservacao(), ESituacao.I));
    }

    public void gerarHistoricoDeInativacaoPorOrganizacaoEmpresa(Integer usuarioId) {
        usuarioHistoricoRepository.save(UsuarioHistorico
            .gerarHistorico(usuarioId, motivoInativacaoService
                .findByCodigoMotivoInativacao(ORGANIZACAO_EMPRESA_INATIVA),
                INATIVACAO_ORGANIZACAO.getObservacao(), ESituacao.I));
    }

    public Optional<String> findMotivoInativacaoByUsuarioId(Integer usuarioId) {
        return usuarioHistoricoRepository.findMotivoInativacaoByUsuarioId(usuarioId);
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
