package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHistoricoDto;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.INATIVADO_SEM_ACESSO;

@Service
public class UsuarioHistoricoService {

    private static final String INATIVADO_DESCRICAO = "INATIVADO POR FALTA DE ACESSO";
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

    public void gerarHistoricoInativacao(Usuario usuario) {
        usuarioHistoricoRepository.save(UsuarioHistorico.gerarHistorico(
                usuario.getId(), getMotivoInativacao(), INATIVADO_DESCRICAO, ESituacao.I
        ));
    }

    private MotivoInativacao getMotivoInativacao() {
        return motivoInativacaoService.findByCodigoMotivoInativacao(INATIVADO_SEM_ACESSO);
    }
}
