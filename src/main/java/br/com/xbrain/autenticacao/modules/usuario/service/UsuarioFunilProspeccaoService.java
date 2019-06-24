package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public OptionalInt findUsuarioDirecionadoByCidade(String cidadeNome) {
        return getUsuarioRedirecionado(
            usuarioRepository.findByIdInAndCargoIn(getIdsDosUsuariosDaCidade(cidadeNome),
            cargoRepository.findByCodigoIn(of(EXECUTIVO_HUNTER, EXECUTIVO, COORDENADOR_OPERACAO, GERENTE_OPERACAO))));
    }

    private List<Integer> getIdsDosUsuariosDaCidade(String cidadeNome) {
        return usuarioCidadeRepository
            .findUsuarioByCidadeIn(cidadeRepository
                .findCidadeByNomeLike(cidadeNome))
            .stream()
            .filter(usuarioCidade -> usuarioCidade.getUsuario().isAtivo())
            .map(usuarioCidade -> usuarioCidade.getUsuario().getId())
            .collect(Collectors.toList());
    }

    private OptionalInt getUsuarioRedirecionado(List<Usuario> usuarios) {
        var executivoHunter = getUsuarioComCargo(usuarios, EXECUTIVO_HUNTER);
        if (executivoHunter.size() == 1) {
            return OptionalInt.of(executivoHunter.get(0));
        }
        var executivo = getUsuarioComCargo(usuarios,EXECUTIVO);
        if (executivo.size() == 1) {
            return OptionalInt.of(executivo.get(0));
        }
        var coordenador = getUsuarioComCargo(usuarios, COORDENADOR_OPERACAO);
        if (coordenador.size() == 1) {
            return OptionalInt.of(coordenador.get(0));
        }
        var gerente = getUsuarioComCargo(usuarios, GERENTE_OPERACAO);
        return OptionalInt.of(gerente.get(0));
    }

    private List<Integer> getUsuarioComCargo(List<Usuario> usuarios, CodigoCargo codigoCargo) {
        return usuarios
            .stream()
            .filter(usuario -> usuario.isCargo(codigoCargo))
            .map(Usuario::getId)
            .collect(Collectors.toList());
    }


}
