package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidade;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioCidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static java.util.List.of;

@Service
public class UsuarioFunilProspeccaoService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CidadeRepository cidadeRepository;
    @Autowired
    private UsuarioCidadeRepository usuarioCidadeRepository;
    @Autowired
    private CargoRepository cargoRepository;

    public Integer findUsuarioDirecionadoByCidade(String cidadeNome) {
        List<Cargo> cargos = cargoRepository.findByCodigoIn(of(EXECUTIVO_HUNTER, EXECUTIVO, COORDENADOR_OPERACAO,
            GERENTE_OPERACAO));
        List<Usuario> usuariosFiltro = usuarioRepository.findByIdInAndCargoIn(getIdsDosUsuariosDaCidade(cidadeNome), cargos);
        return getUsuarioRedirecionado(usuariosFiltro);
    }

    private List<Integer> getIdsDosUsuariosDaCidade(String cidadeNome) {
        List<Cidade> cidades = cidadeRepository.findCidadeByNomeIn(cidadeNome);
        List<UsuarioCidade> usuarios = usuarioCidadeRepository.findUsuarioByCidadeIn(cidades);
        List<Integer> usuariosIds = new ArrayList<>();
        usuarios
            .forEach(usuario -> {
                if (usuario.getUsuario().isAtivo()) {
                    usuariosIds.add(usuario.getUsuario().getId());
                }
            });
        return usuariosIds;
    }

    private Integer getUsuarioRedirecionado(List<Usuario> usuarios) {
        var executivoHunter = getUsuarioExecutivoHunter(usuarios);
        if (executivoHunter.size() == 1) {
            return executivoHunter.get(0);
        } else {
            var executivo = getUsuarioExecutivo(usuarios);
            if (executivo.size() == 1) {
                return executivo.get(0);
            } else {
                var coordenador = getUsuarioCoordenador(usuarios);
                if (coordenador.size() == 1) {
                    return coordenador.get(0);
                } else {
                    var gerente = getUsuarioGerente(usuarios);
                    return !gerente.isEmpty() ? gerente.get(0) : null;
                }
            }
        }
    }

    private List<Integer> getUsuarioExecutivo(List<Usuario> usuarios) {
        List<Integer> executivo = new ArrayList<>();
        usuarios
            .forEach(
                usuario -> {
                    if (usuario.isCargo(EXECUTIVO)) {
                        executivo.add(usuario.getId());
                    }
                }
        );
        return executivo;
    }

    private List<Integer> getUsuarioExecutivoHunter(List<Usuario> usuarios) {
        List<Integer> executivoHunter = new ArrayList<>();
        usuarios
            .forEach(
                usuario -> {
                    if (usuario.isCargo(EXECUTIVO_HUNTER)) {
                        executivoHunter.add(usuario.getId());
                    }
                }
        );
        return executivoHunter;
    }

    private List<Integer> getUsuarioCoordenador(List<Usuario> usuarios) {
        List<Integer> coordenador = new ArrayList<>();
        usuarios
            .forEach(
                usuario -> {
                    if (usuario.isCargo(COORDENADOR_OPERACAO)) {
                        coordenador.add(usuario.getId());
                    }
                }
        );
        return coordenador;
    }

    private List<Integer> getUsuarioGerente(List<Usuario> usuarios) {
        List<Integer> gerente = new ArrayList<>();
        usuarios
            .forEach(
                usuario -> {
                    if (usuario.isCargo(GERENTE_OPERACAO)) {
                        gerente.add(usuario.getId());
                    }
                }
        );
        return gerente;
    }

}
