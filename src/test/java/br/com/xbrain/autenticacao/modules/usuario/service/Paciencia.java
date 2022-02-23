package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendasUsuarioService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargoOperacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import com.google.common.collect.Sets;
import com.querydsl.core.types.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class Paciencia {

    @InjectMocks
    private UsuarioService usuarioService;
    @Mock
    private EquipeVendasUsuarioService equipeVendasUsuarioService;
    @Mock
    private UsuarioRepository usuarioRepository;

    @Test(expected = ValidacaoException.class)
    public void validarMudancaCargo_retornaValidacaoException_quandoUsuarioAtivoOutraEquipe() {
        when(usuarioRepository.findOne(any(Predicate.class)))
            .thenReturn(umUsuarioCompleto(CodigoCargoOperacao.ASSISTENTE_OPERACAO.getCodigo(), CodigoNivel.OPERACAO));
        when(equipeVendasUsuarioService.buscarUsuarioPorId(anyInt()))
            .thenReturn(listaComUsuarioResponse());
        usuarioService.validarMudancaCargo(criaNovoUsuario(CodigoCargoOperacao.VENDEDOR_OPERACAO.getCodigo()));
    }

    @Test
    public void validarMudancaCargo_naoRetornaNada_quandoUsuarioNaoPossuiOutraEquipe() {
        when(usuarioRepository.findOne(any(Predicate.class)))
            .thenReturn(umUsuarioCompleto(CodigoCargoOperacao.ASSISTENTE_OPERACAO.getCodigo(), CodigoNivel.OPERACAO));
        when(equipeVendasUsuarioService.buscarUsuarioPorId(anyInt()))
            .thenReturn(listaVazia());
        usuarioService.validarMudancaCargo(criaNovoUsuario(CodigoCargoOperacao.VENDEDOR_OPERACAO.getCodigo()));
    }

    @Test
    public void validarMudancaCargo_naoRetornaNada_quandoUsuarioPossuiCargoForaVerificacao() {
        when(usuarioRepository.findOne(any(Predicate.class)))
            .thenReturn(umUsuarioCompleto(CodigoCargoOperacao.COORDENADOR_OPERACAO.getCodigo(), CodigoNivel.OPERACAO));
        usuarioService.validarMudancaCargo(criaNovoUsuario(CodigoCargoOperacao.COORDENADOR_OPERACAO.getCodigo()));
        verify(equipeVendasUsuarioService, never()).buscarUsuarioPorId(any());
    }

    private List<EquipeVendaUsuarioResponse> listaVazia() {
        var lista = new ArrayList<EquipeVendaUsuarioResponse>();
        return lista;
    }

    private List<EquipeVendaUsuarioResponse> listaComUsuarioResponse() {
        var lista = new ArrayList<EquipeVendaUsuarioResponse>();
        lista.add(criaEquipeVendaUsuarioResponse());
        return lista;
    }

    private Usuario umUsuarioCompleto(int cargoId, CodigoNivel nivel) {
        var usuario = Usuario
            .builder()
            .id(1)
            .nome("NOME UM")
            .email("email@email.com")
            .cpf("111.111.111-11")
            .situacao(ESituacao.A)
            .loginNetSales("login123")
            .cargo(Cargo
                .builder()
                .id(cargoId)
                .nivel(Nivel
                    .builder()
                    .codigo(nivel)
                    .nome(nivel.name())
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .id(3)
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .nome("UNIDADE NEGÓCIO UM")
                .build()))
            .empresas(List.of(Empresa
                .builder()
                .nome("EMPRESA UM")
                .build()))
            .build();

        usuario.setCidades(
            Sets.newHashSet(
                List.of(UsuarioCidade.criar(
                    usuario,
                    3237,
                    100
                ))
            )
        );
        usuario.setUsuariosHierarquia(
            Sets.newHashSet(
                UsuarioHierarquia.criar(
                    usuario,
                    65,
                    100)
            )
        );
        usuario.setCanais(
            Sets.newHashSet(
                List.of(ECanal.ATIVO_PROPRIO)
            )
        );

        return usuario;
    }

    private Usuario criaNovoUsuario(int cargoId) {
        return Usuario.builder().id(1)
            .cargo(new Cargo(cargoId))
            .departamento(new Departamento(3))
            .build();
    }

    private EquipeVendaUsuarioResponse criaEquipeVendaUsuarioResponse() {
        return EquipeVendaUsuarioResponse.builder().id(1)
            .build();
    }

}
