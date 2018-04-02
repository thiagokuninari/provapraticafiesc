package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.predicate.NivelPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.NivelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_USUARIOS_AA;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_USUARIOS_VAREJO;

@Service
public class NivelService {

    @Autowired
    private NivelRepository nivelRepository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    public List<Nivel> getAll() {
        NivelPredicate predicate = new NivelPredicate();
        predicate.ativo();
        return nivelRepository.getAll(predicate.build());
    }

    public List<Nivel> getAllByPermitidosCadastroUsuario() {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        NivelPredicate predicate = new NivelPredicate();
        predicate.deveExibirCadastro();
        predicate.filtrarPermitidos(usuarioAutenticado);
        if (!usuarioAutenticado.isXbrain()) {
            predicate.withoutXbrain();
        }
        if (!usuarioAutenticado.hasPermissao(AUT_VISUALIZAR_USUARIOS_VAREJO)) {
            predicate.withoutVarejo();
        }
        return nivelRepository.getAllByPermitidos(predicate.build());
    }

    public List<Nivel> getAllByPermitidosListaUsuarios() {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        NivelPredicate predicate = new NivelPredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        if (!usuarioAutenticado.isXbrain()) {
            predicate.withoutXbrain();
        }
        if (!usuarioAutenticado.hasPermissao(AUT_VISUALIZAR_USUARIOS_AA)) {
            predicate.withoutAgenteAutoriazado();
        }
        if (!usuarioAutenticado.hasPermissao(AUT_VISUALIZAR_USUARIOS_VAREJO)) {
            predicate.withoutVarejo();
        }
        return nivelRepository.getAllByPermitidos(predicate.build());
    }
}
