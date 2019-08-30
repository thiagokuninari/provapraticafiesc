package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UsuarioAcessoService {

    private static final int TRINTA_E_DOIS_DIAS = 32;
    private static final String MSG_ERRO_AO_INATIVAR_USUARIO = "ocorreu um erro desconhecido ao inativar "
            + "usuários que estão a mais de 32 dias sem efetuar login.";
    private static final String MSG_ERRO_AO_DELETAR_REGISTROS = "Ocorreu um erro desconhecido ao tentar deletar "
            + " os registros antigos da tabela usuário acesso.";
    @Autowired
    private UsuarioAcessoRepository usuarioAcessoRepository;
    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;
    @Autowired
    private InativarColaboradorMqSender inativarColaboradorMqSender;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    @Transactional
    public void registrarAcesso(Integer usuarioId) {
        usuarioAcessoRepository.save(UsuarioAcesso.builder()
                .build()
                .criaRegistroAcesso(usuarioId));
    }

    @Transactional
    public void inativarUsuariosSemAcesso() {
        usuarioIsXbrain();
        try {
            List<UsuarioAcesso> usuarios = buscarUsuariosParaInativar();
            usuarios.forEach(usuarioAcesso -> {
                Usuario usuario = usuarioAcesso.getUsuario();
                usuarioRepository.atualizarParaSituacaoInativo(usuario.getId());
                usuarioHistoricoService.gerarHistoricoInativacao(usuario);
                inativarColaboradorPol(usuario);
            });
            log.info("Total de usuários inativados: " + usuarios.size());
        } catch (Exception ex) {
            log.warn(MSG_ERRO_AO_INATIVAR_USUARIO, ex);
        }
    }

    @Transactional
    public long deletarHistoricoUsuarioAcesso() {
        usuarioIsXbrain();
        try {
            return usuarioAcessoRepository.deletarHistoricoUsuarioAcesso();
        } catch (Exception ex) {
            log.warn(MSG_ERRO_AO_DELETAR_REGISTROS, ex);
        }
        return 0;
    }

    private boolean ultrapassouTrintaEDoisDiasDesdeUltimoAcesso(UsuarioAcesso usuarioAcesso) {
        return usuarioAcesso.getDataCadastro()
            .isBefore(LocalDateTime.now().minusDays(TRINTA_E_DOIS_DIAS));
    }

    private List<UsuarioAcesso> buscarUsuariosParaInativar() {
        List<UsuarioAcesso> usuariosAcesso = usuarioAcessoRepository.findAllUltimoAcessoUsuarios()
            .stream()
            .filter(this::ultrapassouTrintaEDoisDiasDesdeUltimoAcesso)
            .collect(Collectors.toList());

        List<UsuarioAcesso> usuarios = usuarioRepository.findAllUsuariosSemDataUltimoAcesso()
            .stream()
            .map(UsuarioAcesso::of)
            .collect(Collectors.toList());
        return retornarListaUsuariosParaInativar(usuariosAcesso, usuarios);
    }

    private List<UsuarioAcesso> retornarListaUsuariosParaInativar(List<UsuarioAcesso> usuariosAcesso,
                                                                  List<UsuarioAcesso> usuarios) {
        if (!usuariosAcesso.isEmpty() && !usuarios.isEmpty()) {
            usuariosAcesso.addAll(usuarios);
            return usuariosAcesso;
        } else if (!usuariosAcesso.isEmpty()) {
            return usuariosAcesso;
        }
        return usuarios;
    }

    private void inativarColaboradorPol(Usuario usuario) {
        if (Objects.nonNull(usuario.getEmail())) {
            inativarColaboradorMqSender.sendSuccess(usuario.getEmail());
        } else {
            log.warn("Usuário " + usuario.getId() + " não possui um email cadastrado.");
        }
    }

    private void usuarioIsXbrain() {
        if (!autenticacaoService.getUsuarioAutenticado().isXbrain()) {
            throw new PermissaoException();
        }
    }
}