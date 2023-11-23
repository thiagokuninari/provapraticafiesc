package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.FunilProspeccaoUsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioCidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static java.util.List.of;
import static org.springframework.util.ObjectUtils.isEmpty;

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

    public FunilProspeccaoUsuarioDto findUsuarioDirecionadoByCidade(String cidadeNome) {
        var cidades = cidadeRepository.findCidadeByNomeLike(cidadeNome);
        var usuarios = usuarioRepository.findByIdInAndCargoIn(
            getIdsDosUsuariosDaCidade(cidades),
            getCargos());
        var usuarioRedirecionado =  getUsuarioRedirecionado(usuarios);
        if (isEmpty(usuarioRedirecionado.getUsuarioId())) {
            usuarioRedirecionado = buscarGerentePelaUf(cidades);
        }
        return usuarioRedirecionado;
    }

    private List<Integer> getIdsDosUsuariosDaCidade(List<Cidade> cidades) {
        return usuarioCidadeRepository
            .findUsuarioByCidadeIn(cidades)
            .stream()
            .filter(usuarioCidade -> usuarioCidade.getUsuario().isAtivo())
            .map(usuarioCidade -> usuarioCidade.getUsuario().getId())
            .collect(Collectors.toList());
    }

    private List<Cargo> getCargos() {
        return cargoRepository.findByCodigoIn(of(EXECUTIVO_HUNTER, EXECUTIVO, COORDENADOR_OPERACAO, GERENTE_OPERACAO));
    }

    private FunilProspeccaoUsuarioDto getUsuarioRedirecionado(List<Usuario> usuarios) {
        var executivoHunter = getUsuarioComCargo(usuarios, EXECUTIVO_HUNTER);
        if (executivoHunter.size() == 1) {
            return new FunilProspeccaoUsuarioDto(executivoHunter.get(0));
        }
        var executivo = getUsuarioComCargo(usuarios,EXECUTIVO);
        if (executivo.size() == 1) {
            return new FunilProspeccaoUsuarioDto(executivo.get(0));
        }
        var coordenador = getUsuarioComCargo(usuarios, COORDENADOR_OPERACAO);
        if (coordenador.size() == 1) {
            return new FunilProspeccaoUsuarioDto(coordenador.get(0));
        }
        var gerente = getUsuarioComCargo(usuarios, GERENTE_OPERACAO);
        return new FunilProspeccaoUsuarioDto(gerente.isEmpty() ? null : gerente.get(0));
    }

    private List<Integer> getUsuarioComCargo(List<Usuario> usuarios, CodigoCargo codigoCargo) {
        return usuarios
            .stream()
            .filter(usuario -> usuario.isCargo(codigoCargo))
            .map(Usuario::getId)
            .collect(Collectors.toList());
    }

    private FunilProspeccaoUsuarioDto buscarGerentePelaUf(List<Cidade> cidades) {
        var funilProspeccaoUsuario = new FunilProspeccaoUsuarioDto();
        if (!cidades.isEmpty() && !isEmpty(cidades.get(0).getUf().getId())) {
            funilProspeccaoUsuario = usuarioRepository.findUsuarioGerenteByUf(cidades.get(0).getUf().getId());
        }
        return funilProspeccaoUsuario;
    }

}
