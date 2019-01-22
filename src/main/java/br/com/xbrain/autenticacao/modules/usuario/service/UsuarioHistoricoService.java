package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHistoricoDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.autenticacao.modules.usuario.repository.MotivoInativacaoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioHistoricoService {
        
    @Autowired
    private UsuarioHistoricoRepository usuarioHistoricoRepository;
    
    @Autowired
    private MotivoInativacaoRepository motivoInativacaoRepository;
    
    @Async
    public void registrarHistoricoUltimoAcessoAsync(Integer usuarioId) {
        UsuarioHistorico historico = gerarHistoricoUltimoAcessoDoUsuario(usuarioId);
        usuarioHistoricoRepository.save(historico);
    }
    
    public List<UsuarioHistoricoDto> getHistoricoDoUsuario(Integer usuarioId) {
        return usuarioHistoricoRepository.getHistoricoDoUsuario(usuarioId);
    }   

    public UsuarioHistorico gerarHistoricoUltimoAcessoDoUsuario(Integer usuarioId) {
        Optional<UsuarioHistorico> usuarioHistorico = usuarioHistoricoRepository.getUltimoHistoricoPorUsuario(usuarioId);
        if (usuarioHistorico.isPresent()) {
            usuarioHistorico.get().atualizarDataUltimoAcesso();
            return usuarioHistorico.get();
        }
        Optional<MotivoInativacao> motivo = motivoInativacaoRepository.findByCodigo(CodigoMotivoInativacao.ULTIMO_ACESSO);
        return UsuarioHistorico.gerarUltimoAcesso(usuarioId, motivo.get());
    }

}
