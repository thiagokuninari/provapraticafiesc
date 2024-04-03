package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.NivelTipoVisualizacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.NivelPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.NivelRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.NivelHelper.*;
import static org.assertj.core.api.Java6Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NivelServiceTest {

    @InjectMocks
    private NivelService service;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private NivelRepository nivelRepository;

    @Test
    public void getPermitidosPorNivel_deveRetornarXbrain_quandoOUsuarioForXbrain() {
        var usuarioAutenticado = UsuarioAutenticado.builder()
            .usuario(Usuario
                .builder()
                .canais(Set.of(ECanal.D2D_PROPRIO))
                .build())
            .cargoCodigo(CodigoCargo.EXECUTIVO)
            .nivelCodigo("XBRAIN")
            .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole())))
            .build();

        var predicate = getPredicate(usuarioAutenticado);

        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(usuarioAutenticado);

        when(nivelRepository.getAll(predicate.build()))
            .thenReturn(List.of(umNivelReceptivo(), umNivelXbrain()));

        assertThat(service.getPermitidos(NivelTipoVisualizacao.CADASTRO))
            .extracting("id", "nome")
            .contains(
                tuple(4, "X-BRAIN"));
    }

    @Test
    public void getPermitidosPorNivel_deveIgnorarXbrain_quandoOUsuarioNaoForXbrain() {
        var usuarioAutenticado = UsuarioAutenticado.builder()
            .usuario(Usuario
                .builder()
                .canais(Set.of(ECanal.D2D_PROPRIO))
                .build())
            .cargoCodigo(CodigoCargo.EXECUTIVO)
            .nivelCodigo("MSO")
            .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole())))
            .build();

        var predicate = getPredicate(usuarioAutenticado);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticado);

        when(nivelRepository.getAll(predicate.build()))
            .thenReturn(List.of(umNivelReceptivo(), umNivelBko(), umNivelMso()));

        assertThat(service.getPermitidos(NivelTipoVisualizacao.CADASTRO))
            .extracting("id", "nome")
            .doesNotContain(
                tuple(4, "X-BRAIN"));
    }

    @Test
    public void getPermitidosPorNivel_deveVisualizarSomenteProprioNivel_quandoNaoTiverPermissaoVisualizarGeral() {
        var usuarioAutenticado = UsuarioAutenticado.builder()
            .usuario(Usuario
                .builder()
                .canais(Set.of(ECanal.D2D_PROPRIO))
                .build())
            .cargoCodigo(CodigoCargo.EXECUTIVO)
            .nivelCodigo("MSO")
            .build();

        var predicate = getPredicate(usuarioAutenticado);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticado);

        when(nivelRepository.getAll(predicate.build()))
            .thenReturn(List.of(umNivelMso()));

        assertThat(service.getPermitidos(NivelTipoVisualizacao.CADASTRO))
            .extracting("id", "nome")
            .contains(
                tuple(2, "MSO"));
    }

    @Test
    public void getPermitidosPorNivel_deveVisualizarSeuNivelENivelAgente_quandoSemPermisaoeSendoGerenciaOperacao() {
        var usuarioAutenticado = UsuarioAutenticado.builder()
            .usuario(Usuario
                .builder()
                .canais(Set.of(ECanal.D2D_PROPRIO))
                .build())
            .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
            .nivelCodigo(CodigoNivel.OPERACAO.name())
            .build();

        var predicate = getPredicate(usuarioAutenticado);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticado);

        when(nivelRepository.getAll(predicate.build()))
            .thenReturn(List.of(umNivelOperacao()));

        assertThat(service.getPermitidos(NivelTipoVisualizacao.CADASTRO))
            .extracting("id", "nome")
            .contains(
                tuple(1, "Operação")
            );
    }

    @Test
    public void getPermitidosParaComunicados_deveVisualizarSeuNivel_quandoSemPermisaoeSendoGerenciaOperacao() {
        var usuarioAutenticado = UsuarioAutenticado.builder()
            .usuario(Usuario
                .builder()
                .canais(Set.of(ECanal.AGENTE_AUTORIZADO))
                .build())
            .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
            .nivelCodigo(CodigoNivel.OPERACAO.name())
            .build();

        var predicate = getPredicateParaComunicados(usuarioAutenticado);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticado);

        when(nivelRepository.getAll(predicate.build()))
            .thenReturn(List.of(umNivelAa(), umNivelOperacao()));

        assertThat(service.getPermitidosParaComunicados())
            .extracting("id", "nome")
            .contains(
                tuple(3, "Agente Autorizado"),
                tuple(1, "Operação")
            );
    }

    @Test
    public void getPermitidosPorNivel_deveVisualizarTodosOsNiveis_quandoTiverPermissaoVisualizarGeral() {
        var usuarioAutenticado = UsuarioAutenticado.builder()
            .usuario(Usuario
                .builder()
                .canais(Set.of(ECanal.D2D_PROPRIO))
                .build())
            .cargoCodigo(CodigoCargo.EXECUTIVO)
            .nivelCodigo("MSO")
            .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole())))
            .build();

        var predicate = getPredicate(usuarioAutenticado);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticado);

        when(nivelRepository.getAll(predicate.build()))
            .thenReturn(umaListaDeNiveis());

        assertThat(service.getPermitidos(NivelTipoVisualizacao.CADASTRO))
            .extracting("id", "nome")
            .containsExactlyInAnyOrder(
                tuple(6, "Atendimento Pessoal"),
                tuple(16, "Ativo Local Colaborador"),
                tuple(9, "Ativo Local Proprio"),
                tuple(10, "Ativo Local Terceiro"),
                tuple(11, "Ativo Nacional Terceiro"),
                tuple(12, "Ativo Nacional Terceiro Segmentado"),
                tuple(13, "Ativo Rentabilização"),
                tuple(7, "Lojas"),
                tuple(2, "MSO"),
                tuple(1, "Operação"),
                tuple(15, "Ouvidoria"),
                tuple(8, "Receptivo"),
                tuple(18, "Backoffice"),
                tuple(19, "Backoffice Centralizado"));
    }

    @Test
    public void getPermitidosParaOrganizacao_deveRetornarNiveisPermitidos_quandoSolicitado() {
        when(nivelRepository.findByCodigoIn(List.of(CodigoNivel.RECEPTIVO, CodigoNivel.BACKOFFICE, CodigoNivel.OPERACAO,
            CodigoNivel.BACKOFFICE_CENTRALIZADO, CodigoNivel.BACKOFFICE_SUPORTE_VENDAS)))
            .thenReturn(umaListaComNiveisReceptivoBkoEOperacao());

        assertThat(service.getPermitidosParaOrganizacao())
            .extracting("codigo")
            .contains(CodigoNivel.RECEPTIVO.name(), CodigoNivel.BACKOFFICE.name());
    }

    @Test
    public void getByCodigo_deveRetornarNivelResponse_quandoSolicitado() {
        when(nivelRepository.findByCodigo(CodigoNivel.MSO)).thenReturn(umNivelMso());

        assertThat(service.getByCodigo(CodigoNivel.MSO))
            .extracting("id", "nome", "codigo")
            .containsExactly(2, "MSO", CodigoNivel.MSO.name());
    }

    @Test
    public void getByCodigo_deveLancarExcecao_quandoNaoEncontrar() {
        assertThatThrownBy(() -> service.getByCodigo(CodigoNivel.MSO))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Nível não encontrado.");
    }

    private NivelPredicate getPredicate(UsuarioAutenticado usuarioAutenticado) {
        return new NivelPredicate()
            .isAtivo()
            .exibeSomenteParaCadastro(NivelTipoVisualizacao.CADASTRO == NivelTipoVisualizacao.CADASTRO)
            .exibeXbrainSomenteParaXbrain(usuarioAutenticado.isXbrain())
            .exibeProprioNivelSeNaoVisualizarGeral(
                usuarioAutenticado.hasPermissao(AUT_VISUALIZAR_GERAL),
                usuarioAutenticado.getNivelCodigoEnum(), false);
    }

    private NivelPredicate getPredicateParaComunicados(UsuarioAutenticado usuarioAutenticado) {
        return new NivelPredicate()
            .isAtivo()
            .exibeXbrainSomenteParaXbrain(usuarioAutenticado.isXbrain())
            .semCodigoNivel(CodigoNivel.BACKOFFICE)
            .exibeProprioNivelSeNaoVisualizarGeral(
                usuarioAutenticado.hasPermissao(AUT_VISUALIZAR_GERAL),
                usuarioAutenticado.getNivelCodigoEnum(),
                usuarioAutenticado.haveCanalAgenteAutorizado());
    }
}
