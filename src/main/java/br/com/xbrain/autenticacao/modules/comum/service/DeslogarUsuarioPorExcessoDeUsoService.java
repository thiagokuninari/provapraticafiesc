package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.UsuarioExcessoUsoResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.UsuarioParaDeslogar;
import br.com.xbrain.autenticacao.modules.comum.repository.UsuarioParaDeslogarRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.MotivoInativacaoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico.gerarHistoricoDeBloqueioPorExcessoDeUso;
import static java.lang.String.format;
import static org.thymeleaf.util.StringUtils.concat;

@Slf4j
@Service
public class DeslogarUsuarioPorExcessoDeUsoService {

    private static Integer qtdUsuariosDeslogados = 0;
    private static Integer qtdUsuariosInativados = 0;

    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private UsuarioParaDeslogarRepository repository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private MotivoInativacaoRepository motivoInativacaoRepository;

    @Transactional
    public void deslogarUsuariosInativados() {
        log.info("\nIniciando timer de busca por usuários logados e bloqueados por excesso de uso da API.");
        var usuarios = repository.findAllByDeslogado(Eboolean.F);
        if (!usuarios.isEmpty()) {
            deslogarUsuarios(usuarios);
            atualizarUsuariosParaDeslogados(usuarios);
            gerarRelatorioDeLogsDeBloqueio(usuarios.size());
            log.info("Usuarios deslogados: {}", usuarios);
        } else {
            log.info("\nNão foram encontrados usuários bloqueados.");
        }
        zerarVariaveisGlobais();
    }

    private void gerarRelatorioDeLogsDeBloqueio(Integer totalRegistrosBloqueados) {
        var totalRegistros = format("\nTotal de registros de usuários bloqueados: %d", totalRegistrosBloqueados);
        var totalDeslogados = format("\nUsuários deslogados por excesso de uso de API: %d", qtdUsuariosDeslogados);
        var totalInativados = format("\nUsuários inativados: %d", qtdUsuariosInativados);
        log.info(concat(totalRegistros, totalDeslogados, totalInativados));
    }

    private void zerarVariaveisGlobais() {
        qtdUsuariosInativados = 0;
        qtdUsuariosDeslogados = 0;
    }

    private void deslogarUsuarios(List<UsuarioParaDeslogar> usuarios) {
        usuarios
            .stream()
            .map(UsuarioParaDeslogar::getUsuarioId)
            .forEach(this::deslogarEInativarUsuario);
    }

    private void deslogarEInativarUsuario(Integer usuarioId) {
        var mensagemInicial = concat("\nO usuário ", usuarioId);
        usuarioRepository.findById(usuarioId)
            .ifPresentOrElse(usuario -> {
                autenticacaoService.logout(usuario.getId());
                var mensagem = concat(mensagemInicial, " | ", usuario.getEmail());
                log.info(concat(mensagem, " foi deslogado."));
                qtdUsuariosDeslogados++;
                inativarUsuario(usuario, mensagem);
            }, () -> log.info(concat(mensagemInicial, " não foi encontrado.")));
    }

    private void inativarUsuario(Usuario usuario, String mensagem) {
        if (usuario.isAtivo()) {
            usuario.setSituacao(ESituacao.I);
            inserirHistoricoDeBloqueioPorExcessoDeUsoDaApi(usuario);
            usuarioRepository.save(usuario);
            log.info(concat(mensagem, " foi inativado."));
            qtdUsuariosInativados++;
        }
    }

    private void inserirHistoricoDeBloqueioPorExcessoDeUsoDaApi(Usuario usuario) {
        var motivoInativacao = motivoInativacaoRepository
            .findByCodigo(CodigoMotivoInativacao.INATIVADO_SIMULACOES)
            .orElseThrow(() -> new ValidacaoException("O motivo de inativação não foi encontrado."));
        usuario.adicionarHistorico(gerarHistoricoDeBloqueioPorExcessoDeUso(usuario, motivoInativacao));
    }

    private void atualizarUsuariosParaDeslogados(List<UsuarioParaDeslogar> usuarios) {
        usuarios
            .stream()
            .map(UsuarioParaDeslogar::atualizarParaDeslogado)
            .forEach(repository::save);
    }

    public UsuarioExcessoUsoResponse validarUsuarioBloqueadoPorExcessoDeUso(Integer usuarioId) {
        var usuarioParaDeslogar = repository
            .findFirstByUsuarioIdOrderByDataCadastroDesc(usuarioId)
            .orElse(UsuarioParaDeslogar.of(usuarioId, Eboolean.F));

        return UsuarioExcessoUsoResponse.of(usuarioId, Eboolean.V.equals(usuarioParaDeslogar.getBloqueado()));
    }

    public void atualizarSituacaoUsuarioBloqueado(Integer usuarioId) {
        repository.findFirstByUsuarioIdOrderByDataCadastroDesc(usuarioId)
            .map(UsuarioParaDeslogar::atualizarSituacaoBloqueado)
            .ifPresent(repository::save);
    }
}
