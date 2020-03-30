package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.UsuarioExcessoUsoResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.UsuarioParaDeslogar;
import br.com.xbrain.autenticacao.modules.comum.repository.UsuarioParaDeslogarRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DeslogarUsuarioPorExcessoDeUsoService {

    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private UsuarioParaDeslogarRepository repository;

    public void deslogarUsuariosInativados() {
        var usuarios = repository.findAllByDeslogado(Eboolean.F);
        if (!usuarios.isEmpty()) {
            var quantidadeUsuariosLogados = usuarios.size();
            deslogarUsuarios(usuarios);
            atualizarUsuariosParaDeslogados(usuarios);
            log.info("Usuários deslogados por excesso de uso de API: {}", quantidadeUsuariosLogados);
        }
    }

    private void deslogarUsuarios(List<UsuarioParaDeslogar> usuarios) {
        usuarios
            .stream()
            .map(UsuarioParaDeslogar::getUsuarioId)
            .forEach(autenticacaoService::logout);
    }

    private void atualizarUsuariosParaDeslogados(List<UsuarioParaDeslogar> usuarios) {
        usuarios
            .stream()
            .map(UsuarioParaDeslogar::atualizarParaDeslogado)
            .forEach(repository::save);
    }

    public UsuarioExcessoUsoResponse validarUsuarioBloqueadoPorExcessoDeUso(Integer usuarioId) {
        return UsuarioExcessoUsoResponse.of(repository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new ValidacaoException("Não há bloqueios para este usuário.")));
    }
}