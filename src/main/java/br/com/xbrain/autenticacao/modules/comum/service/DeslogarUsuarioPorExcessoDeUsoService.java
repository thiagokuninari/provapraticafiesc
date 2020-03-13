package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.model.UsuarioParaDeslogar;
import br.com.xbrain.autenticacao.modules.comum.repository.UsuarioParaDeslogarRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeslogarUsuarioPorExcessoDeUsoService {

    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private UsuarioParaDeslogarRepository repository;

    public void deslogarUsuariosInativados() {
        var usuarios = repository.findAll();

        if (!usuarios.isEmpty()) {
            usuarios
                .stream()
                .map(UsuarioParaDeslogar::getUsuarioId)
                .forEach(autenticacaoService::logout);

            repository.delete(usuarios);

            log.info("Usuários deslogados por excesso de uso de API: {}", usuarios.size());
        }
    }
}
