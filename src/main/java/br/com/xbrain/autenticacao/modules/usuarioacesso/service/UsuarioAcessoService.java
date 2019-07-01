package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.InativarColaboradorMqSender;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import br.com.xbrain.autenticacao.modules.usuarioacesso.repository.UsuarioAcessoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
public class UsuarioAcessoService {

    private static final String MSG_ERRO_AO_INATIVAR_USUARIO = "ocorreu um erro desconhecido ao inativar "
            + "usuários que estão a mais de 32 dias sem efetuar login.";
    @Autowired
    private UsuarioAcessoRepository usuarioAcessoRepository;
    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;
    @Autowired
    private InativarColaboradorMqSender inativarColaboradorMqSender;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public void registrarAcesso(Integer usuarioId) {
        usuarioAcessoRepository.save(UsuarioAcesso.builder()
                .build()
                .criaRegistroAcesso(usuarioId));
    }

    @Transactional
    public void inativarUsuariosSemAcesso() {
        try {
            usuarioAcessoRepository.findAllUltimoAcessoUsuarios()
                    .forEach(usuarioAcesso -> {
                        Usuario usuario = usuarioAcesso.getUsuario();
                        usuarioRepository.atualizarParaSituacaoInativo(usuario.getId());
                        usuarioHistoricoService.gerarHistoricoInativacao(usuario);
                        inativarColaboradorPol(usuario);
                    });
        } catch (Exception e) {
            log.warn(MSG_ERRO_AO_INATIVAR_USUARIO, e);
        }
    }

    private void inativarColaboradorPol(Usuario usuario) {
        if (Objects.nonNull(usuario.getEmail())) {
            inativarColaboradorMqSender.sendSuccess(usuario.getEmail());
        } else {
            log.warn("Usuário " + usuario.getId() + " não possui um email cadastrado.");
        }
    }
}
