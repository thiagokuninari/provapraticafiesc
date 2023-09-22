package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.usuario.dto.NivelResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.NivelTipoVisualizacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.predicate.NivelPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.NivelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;

@Service
public class NivelService {

    @Autowired
    private NivelRepository nivelRepository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    public List<Nivel> getAll() {
        return nivelRepository.getAll(
                new NivelPredicate()
                        .isAtivo()
                        .build());
    }

    public NivelResponse getByCodigo(CodigoNivel codigoNivel) {
        var nivel = nivelRepository.findByCodigo(codigoNivel);

        return NivelResponse.of(Optional.ofNullable(nivel).orElseThrow(
            () -> new NotFoundException("Nível não encontrado.")
        ));
    }

    public List<Nivel> getPermitidos(NivelTipoVisualizacao tipoVisualizacao) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        return nivelRepository.getAll(
            new NivelPredicate()
                .isAtivo()
                .exibeSomenteParaCadastro(tipoVisualizacao == NivelTipoVisualizacao.CADASTRO)
                .exibeXbrainSomenteParaXbrain(usuarioAutenticado.isXbrain())
                .exibeProprioNivelSeNaoVisualizarGeral(
                    usuarioAutenticado.hasPermissao(AUT_VISUALIZAR_GERAL),
                    usuarioAutenticado.getNivelCodigoEnum(), false)
                .build());
    }

    public List<Nivel> getPermitidosParaComunicados() {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        return nivelRepository.getAll(
            new NivelPredicate()
                .isAtivo()
                .exibeXbrainSomenteParaXbrain(usuarioAutenticado.isXbrain())
                .semCodigoNivel(CodigoNivel.BACKOFFICE)
                .exibeProprioNivelSeNaoVisualizarGeral(
                    usuarioAutenticado.hasPermissao(AUT_VISUALIZAR_GERAL),
                    usuarioAutenticado.getNivelCodigoEnum(),
                    usuarioAutenticado.haveCanalAgenteAutorizado())
                .build());
    }

    public List<NivelResponse> getPermitidosParaOrganizacao() {
        return nivelRepository.findByCodigoIn(List.of(CodigoNivel.VAREJO, CodigoNivel.RECEPTIVO))
            .stream()
            .map(NivelResponse::of)
            .collect(Collectors.toList());
    }
}
