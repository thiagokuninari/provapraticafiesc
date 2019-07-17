package br.com.xbrain.autenticacao.modules.usuarioacesso.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuarioacesso.service.UsuarioAcessoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/usuario-acesso")
public class UsuarioAcessoController {

    @Autowired
    private UsuarioAcessoService usuarioAcessoService;
    @Autowired
    private AutenticacaoService autenticacaoService;

    @GetMapping("/inativar")
    @ResponseStatus(HttpStatus.OK)
    public void inativarUsuariosSemAcesso() {
        if (autenticacaoService.getUsuarioAutenticado()
                .isXbrain()) {
            usuarioAcessoService.inativarUsuariosSemAcesso();
            return;
        }

        throw new ValidacaoException("Endpoint protegido, somente usuários ADMIN podem acessar.");
    }

    @DeleteMapping("historico")
    public void deletarHistoricoUsuarioAcesso() {
        if (autenticacaoService.getUsuarioAutenticado()
                .isXbrain()) {
            usuarioAcessoService.deletarHistoricoUsuarioAcesso();
            return;
        }

        throw new ValidacaoException("Endpoint protegido, somente usuários ADMIN podem acessar.");
    }
}
