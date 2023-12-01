package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CargoPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoSuperiorRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.umaListaDeCargosAtaReuniao;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CargoServiceTest {

    @InjectMocks
    private CargoService service;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private CargoSuperiorRepository cargoSuperiorRepository;
    @Mock
    private CargoRepository cargoRepository;

    @Test
    public void getPermitidosPorNivelECanaisPermitidos_todosOsCargos_quandoBuscarPorListaDeCanaisVazia() {
        mockUmUsuarioGerenteVisualizarGeral();

        var predicate = new CargoPredicate()
            .comNivel(31)
            .build();
        when(cargoRepository.findAll(eq(predicate))).thenReturn(umaListadeCargosComCanais());

        assertThat(service.getPermitidosPorNivelECanaisPermitidos(31, Set.of(), true))
            .extracting(Cargo::getId)
            .containsExactlyInAnyOrder(1000, 1001, 1002, 1003, 1004);
    }

    @Test
    public void getPermitidosPorNivelECanaisPermitidos_todosOsCargos_quandoBuscarPorListaDeCanaisNull() {
        mockUmUsuarioGerenteVisualizarGeral();

        var predicate = new CargoPredicate()
            .comNivel(31)
            .build();
        when(cargoRepository.findAll(eq(predicate))).thenReturn(umaListadeCargosComCanais());

        assertThat(service.getPermitidosPorNivelECanaisPermitidos(31, null, true))
            .extracting(Cargo::getId)
            .containsExactlyInAnyOrder(1000, 1001, 1002, 1003, 1004);
    }

    @Test
    public void getPermitidosPorNivelECanaisPermitidos_cargosPermitidosParaAquelesCanais_quandoBuscarPorMaisDeUmCanal() {
        mockUmUsuarioGerenteVisualizarGeral();

        var predicate = new CargoPredicate()
            .comNivel(31)
            .build();
        when(cargoRepository.findAll(eq(predicate))).thenReturn(umaListadeCargosComCanais());

        assertThat(service
            .getPermitidosPorNivelECanaisPermitidos(31, Set.of(ECanal.ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO), true))
            .extracting(Cargo::getId)
            .containsExactlyInAnyOrder(1000, 1002, 1003, 1004);
    }

    @Test
    public void getPermitidosPorNivelECanaisPermitidos_cargosPermitidosParaAqueleCanal_quandoBuscarPorApenasUmCanal() {
        mockUmUsuarioGerenteVisualizarGeral();

        var predicate = new CargoPredicate()
            .comNivel(31)
            .build();
        when(cargoRepository.findAll(eq(predicate))).thenReturn(umaListadeCargosComCanais());

        assertThat(service.getPermitidosPorNivelECanaisPermitidos(31, Set.of(ECanal.D2D_PROPRIO), true))
            .extracting(Cargo::getId)
            .containsExactlyInAnyOrder(1001, 1002, 1003, 1004);

        assertThat(service.getPermitidosPorNivelECanaisPermitidos(31, Set.of(ECanal.AGENTE_AUTORIZADO), true))
            .extracting(Cargo::getId)
            .containsExactlyInAnyOrder(1000, 1002, 1003);
    }

    @Test
    public void getPermitidosPorNivelECanaisPermitidos_deveRetornarCargosPermitidosParaAqueleCanal_quandoCanalForInternet() {
        mockUmUsuarioGerenteVisualizarGeral();

        var predicate = new CargoPredicate()
            .comNivel(1)
            .comCanal(ECanal.INTERNET)
            .build();
        when(cargoRepository.findAll(eq(predicate))).thenReturn(umaListadeCargosComCanaisInternet());

        assertThat(service.getPermitidosPorNivelECanaisPermitidos(1, Set.of(ECanal.INTERNET), true))
            .extracting(Cargo::getId)
            .containsExactlyInAnyOrder(1000, 1001);

        verify(cargoRepository).findAll(predicate);
    }

    @Test
    public void getPermitidosPorNivel_deveRetornarTodosOsNiveis_quandoTiverPermissaoVisualizarGeral() {

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado
                .builder()
                .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole())))
                .build());

        when(cargoRepository.findAll(any(BooleanBuilder.class)))
            .thenReturn(umaListaDeCargos());

        assertThat(service.getPermitidosPorNivel(new CargoPredicate().comNivel(1)))
            .extracting("id", "nome")
            .containsExactlyInAnyOrder(
                tuple(1, "Analista"),
                tuple(2, "Assistente"),
                tuple(3, "Consultor"),
                tuple(4, "Coordenador"),
                tuple(6, "Diretor"),
                tuple(5, "Executivo"),
                tuple(7, "Gerente"),
                tuple(10, "Supervisor"),
                tuple(9, "Técnico"),
                tuple(8, "Vendedor"),
                tuple(94, "Assistente Hunter"),
                tuple(95, "Executivo Hunter"));
    }

    @Test
    public void getPermitidosPorNivel_deveNaoRetornarOsCargos_somenteAbaixoDaHierarquiaDoCargo() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado
                .builder()
                .cargoId(10)
                .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
                .build());

        when(cargoRepository.findAll(any(BooleanBuilder.class)))
            .thenReturn(List.of(umCargo(8, "Vendedor", ESituacao.A)));

        assertThat(service.getPermitidosPorNivel(new CargoPredicate().comNivel(1)))
            .extracting("id", "nome")
            .containsExactly(
                tuple(8, "Vendedor"));
    }

    @Test
    public void save_deveRetornarCargo_quandoForSalvo() throws Exception {
        when(cargoRepository.save(any(Cargo.class)))
            .thenReturn(umCargo(1, "Vendedor", ESituacao.A));

        assertThat(service.save(umCargo(1, "Vendedor", ESituacao.A)))
            .extracting("nome").contains("Vendedor");
    }

    @Test
    public void update_deveRetonarCargo_quandoForAtualizado() throws Exception {
        when(cargoRepository.findById(any()))
            .thenReturn(Optional.of(umCargo(1, "Assistente", ESituacao.A)));

        when(cargoRepository.save(any(Cargo.class)))
            .thenReturn(umCargo(1, "Vendedor", ESituacao.A));

        assertThat(service.update(umCargo(1, "Vendedor", ESituacao.A)))
            .extracting("nome").contains("Vendedor");
    }

    @Test
    public void situacao_deveRetonarCargo_quandoSituacaoForAlterado() throws Exception {
        when(cargoRepository.findById(any()))
            .thenReturn(Optional.of(umCargo(1, "Vendedor", ESituacao.A)));

        when(cargoRepository.save(any(Cargo.class)))
            .thenReturn(umCargo(1, "Vendedor", ESituacao.I));

        assertThat(service.situacao(umCargoRequest(1, "Vendedor", ESituacao.I)))
            .extracting("situacao").contains(ESituacao.I);
    }

    @Test
    public void isAgenteAutorizado_deveRetornarTrue_quandoForNivelAgenteAutorizado() {
        var response = service.isAgenteAutorizado(CodigoCargo.AGENTE_AUTORIZADO_ACEITE);
        assertThat(response).isTrue();
    }

    @Test
    public void isAgenteAutorizado_deveRetornarFalse_quandoForNivelXbrain() {
        var response = service.isAgenteAutorizado(CodigoCargo.AGENTE_AUTORIZADO_SUPERVISOR_XBRAIN);
        assertThat(response).isFalse();
    }

    @Test
    public void isAgenteAutorizado_deveRetornarFalse_quandoNaoForNivelAgenteAutorizado() {
        var response = service.isAgenteAutorizado(CodigoCargo.ADMINISTRADOR);
        assertThat(response).isFalse();
    }

    @Test
    public void canaisComCargo_deveRetornarCargosComCanalAtivoESemCanal_quandoParametroCanalForAtivo() {
        when(cargoRepository.findAll((Predicate) any())).thenReturn(umaListadeCargosComCanais());

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado
                .builder()
                .cargoId(10)
                .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
                .build());

        assertThat(service.getPermitidosPorNivelECanaisPermitidos(31, Set.of(ECanal.ATIVO_PROPRIO), true))
            .extracting("id", "Canais")
            .contains(
                tuple(1002, Set.of()),
                tuple(1004, Set.of(ECanal.D2D_PROPRIO, ECanal.ATIVO_PROPRIO))
            );
    }

    @Test
    public void cargoEditar_deveRetornarOProprioCargo_quandoUsuarioEditarNaoTerPermissaoParaEditarCargoProprio() {
        when(cargoRepository.findAll((Predicate) any())).thenReturn(List.of(umCargo(7, "Gerente", ESituacao.A)));

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado
                .builder()
                .cargoId(10)
                .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
                .build());
        assertThat(service
            .getPermitidosPorNivelECanaisPermitidos(31, Set.of(ECanal.ATIVO_PROPRIO), false))
            .extracting("id", "Nome")
            .contains(tuple(7, "Gerente"));
    }

    @Test
    public void cargoEditarComPermissaoPermissao_deveRetornarTodosOsCargos_quandoParametroEditarForFalso() {
        when(cargoRepository.findAll((Predicate) any())).thenReturn(umaListaDeCargos());
        mockUmUsuarioGerenteVisualizarGeral();
        assertThat(service.getPermitidosPorNivelECanaisPermitidos(31, null, true))
            .hasSize(12);
    }

    @Test
    public void findCargosForAtaReuniao_deveRetornarCargosParaAtaReuniao_quandoSolicitado() {
        doReturn(umaListaDeCargosAtaReuniao())
            .when(cargoRepository)
            .findByCodigoIn(anyList());

        assertThat(service.findCargosForAtaReuniao())
            .extracting(SelectResponse::getValue, SelectResponse::getLabel)
            .containsExactlyInAnyOrder(
                tuple(CodigoCargo.ASSISTENTE_OPERACAO, "Assistente"),
                tuple(CodigoCargo.ASSISTENTE_HUNTER, "Assistente Hunter"),
                tuple(CodigoCargo.OPERACAO_CONSULTOR, "Consultor"),
                tuple(CodigoCargo.COORDENADOR_OPERACAO, "Coordenador"),
                tuple(CodigoCargo.DIRETOR_OPERACAO, "Diretor"),
                tuple(CodigoCargo.EXECUTIVO, "Executivo"),
                tuple(CodigoCargo.EXECUTIVO_HUNTER, "Executivo Hunter"),
                tuple(CodigoCargo.GERENTE_OPERACAO, "Gerente")
            );

        verify(cargoRepository).findByCodigoIn(anyList());
    }

    private void mockUmUsuarioGerenteVisualizarGeral() {
        var usuarioAutenticado = UsuarioAutenticado.builder()
            .cargoId(10)
            .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
            .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.AUT_VISUALIZAR_GERAL.getRole())))
            .build();
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioAutenticado);
    }

    private Cargo umCargo(Integer id, ECanal... canais) {
        return Cargo.builder()
            .id(id)
            .canais(Set.of(canais))
            .build();
    }

    private Cargo umCargo(Integer id, String nome, ESituacao situacao) {
        return Cargo.builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .nivel(Nivel.builder()
                .id(1)
                .nome("Vendedor")
                .build())
            .build();
    }

    private CargoRequest umCargoRequest(Integer id, String nome, ESituacao situacao) {
        return CargoRequest.builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .nivel(Nivel
                .builder()
                .id(1)
                .build())
            .build();
    }

    private List<Cargo> umaListaDeCargos() {
        return List.of(umCargo(1, "Analista", ESituacao.A),
            umCargo(2, "Assistente", ESituacao.A),
            umCargo(3, "Consultor", ESituacao.A),
            umCargo(4, "Coordenador", ESituacao.A),
            umCargo(6, "Diretor", ESituacao.A),
            umCargo(5, "Executivo", ESituacao.A),
            umCargo(7, "Gerente", ESituacao.A),
            umCargo(10, "Supervisor", ESituacao.A),
            umCargo(9, "Técnico", ESituacao.A),
            umCargo(8, "Vendedor", ESituacao.A),
            umCargo(94, "Assistente Hunter", ESituacao.A),
            umCargo(95, "Executivo Hunter", ESituacao.A));
    }

    @NotNull
    private List<Cargo> umaListadeCargosComCanais() {
        return List.of(
            umCargo(1000, ECanal.AGENTE_AUTORIZADO),
            umCargo(1001, ECanal.D2D_PROPRIO),
            umCargo(1002),
            umCargo(1003, ECanal.AGENTE_AUTORIZADO, ECanal.D2D_PROPRIO),
            umCargo(1004, ECanal.ATIVO_PROPRIO, ECanal.D2D_PROPRIO)
        );
    }

    private List<Cargo> umaListadeCargosComCanaisInternet() {
        return List.of(
            umCargo(1000, ECanal.INTERNET),
            umCargo(1001, ECanal.INTERNET),
            umCargo(1003, ECanal.AGENTE_AUTORIZADO, ECanal.D2D_PROPRIO),
            umCargo(1004, ECanal.ATIVO_PROPRIO, ECanal.D2D_PROPRIO)
        );
    }
}
