package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHistoricoDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.INATIVADO_SEM_ACESSO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.ULTIMO_ACESSO;

@Service
public class UsuarioHistoricoService {
        
    @Autowired
    private UsuarioHistoricoRepository usuarioHistoricoRepository;
    
    @Autowired
    private MotivoInativacaoService motivoInativacaoService;

    public List<UsuarioHistoricoDto> getHistoricoDoUsuario(Integer usuarioId) {
        return usuarioHistoricoRepository.getHistoricoDoUsuario(usuarioId);
    }   

    @Transactional
    public void gerarHistoricoUltimoAcessoDoUsuario(Integer usuarioId) {
        final MotivoInativacao motivo = findMotivoInativacaoByCodigo(ULTIMO_ACESSO);

        Optional<UsuarioHistorico> usuarioHistorico = usuarioHistoricoRepository.getUltimoHistoricoPorUsuario(usuarioId);
        if (usuarioHistorico.isPresent()) {
            usuarioHistoricoRepository.updateUsuarioHistorico(LocalDateTime.now(), null, ESituacao.A, motivo, usuarioId);
        } else {
            usuarioHistoricoRepository.save(UsuarioHistorico.gerarUltimoAcesso(
                    usuarioId, motivo, null, ESituacao.A));
        }
    }

    @Transactional
    public void gerarHistoricoUsuarioInativado(Usuario usuario) {
        usuarioHistoricoRepository.updateUsuarioHistorico(LocalDateTime.now(), "Inativado por falta de acesso",
                ESituacao.I, findMotivoInativacaoByCodigo(INATIVADO_SEM_ACESSO), usuario.getId());
    }

    private MotivoInativacao findMotivoInativacaoByCodigo(CodigoMotivoInativacao codigo) {
        return motivoInativacaoService.findByCodigoMotivoInativacao(codigo);
    }

}
