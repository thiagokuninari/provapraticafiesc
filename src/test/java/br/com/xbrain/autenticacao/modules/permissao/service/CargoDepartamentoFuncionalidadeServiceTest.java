package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadeFiltros;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadePredicate;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.helpers.DepartamentoHelper;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.permissao.helper.CargoDepartamentoFuncionalidadeHelper.*;
import static br.com.xbrain.autenticacao.modules.permissao.helper.FuncionalidadeHelper.funcionalidadeGerenciarAas;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioAutenticadoAdmin;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umaListaDeUsuariosAdminSimples;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CargoDepartamentoFuncionalidadeServiceTest {

    @InjectMocks
    private CargoDepartamentoFuncionalidadeService service;
    @Mock
    private CargoDepartamentoFuncionalidadeRepository repository;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Captor
    private ArgumentCaptor<List<CargoDepartamentoFuncionalidade>> argumentCaptorListaCargoDeptoFuncionalidade;

    @Test
    public void getAll_deveRetornarPage_seExistiremCargoDepartamentoFuncionalidades() {
        var filtros = new CargoDepartamentoFuncionalidadeFiltros();

        when(repository.findAll(filtros.toPredicate(), new PageRequest()))
            .thenReturn(new PageImpl<>(umaListaDeCargoDepartamentoFuncionalidadeDeAdministrador()));

        assertThat(service.getAll(new PageRequest(), filtros))
            .hasSize(10)
            .extracting("departamento.nome", "funcionalidade.nome")
            .containsExactly(
                tuple("Administrador", "Visualizar Geral"),
                tuple("Administrador", "Gerenciar Equipe Venda"),
                tuple("Administrador", "Gerenciar Equipe Técnica"),
                tuple("Administrador", "Visualizar Notícias"),
                tuple("Administrador", "Agente Autorizado Aprovação Operação"),
                tuple("Administrador", "Agente Autorizado Aprovação MSO"),
                tuple("Administrador", "Gerenciar Agentes Autorizados"),
                tuple("Administrador", "Descredenciamento de Agente Autorizado"),
                tuple("Administrador", "Visualizar Comissionamento"),
                tuple("Administrador", "Captação de AA Extração")
            );
    }

    @Test
    public void getAll_deveRetornarPageVazia_seNaoExistirCargoDepartamentoFuncionalidade() {
        var filtros = new CargoDepartamentoFuncionalidadeFiltros();

        when(repository.findAll(filtros.toPredicate(), new PageRequest()))
            .thenReturn(new PageImpl<>(Collections.emptyList()));

        assertThat(service.getAll(new PageRequest(), filtros)).isEmpty();
    }

    @Test
    public void getCargoDepartamentoFuncionalidadeByFiltro_deveRetornarLista_quandoPassarFiltro() {
        var filtros = new CargoDepartamentoFuncionalidadeFiltros();
        filtros.setFuncionalidadeNome("Gerenciar Agentes Autorizados");

        when(repository.findFuncionalidadesPorCargoEDepartamento(filtros.toPredicate()))
            .thenReturn(umaPageCargoDeptoFuncionalidadesGerenciarAas());

        assertThat(service.getCargoDepartamentoFuncionalidadeByFiltro(filtros))
            .hasSize(5)
            .extracting("cargo.nome", "departamento.nome", "funcionalidade.nome")
            .containsExactly(
                tuple("Sócio Principal", "Agente Autorizado", "Gerenciar Agentes Autorizados"),
                tuple("Consultor", "Comercial", "Gerenciar Agentes Autorizados"),
                tuple("Vendedor Operação", "Comercial", "Gerenciar Agentes Autorizados"),
                tuple("Analista", "Administrativo", "Gerenciar Agentes Autorizados"),
                tuple("Administrador", "Administrador", "Gerenciar Agentes Autorizados")
            );
    }

    @Test
    public void getCargoDepartamentoFuncionalidadeByFiltro_deveRetornarListaVazia_seNaoExistirCargoDepartamentoFuncionalidade() {
        var filtros = new CargoDepartamentoFuncionalidadeFiltros();
        filtros.setFuncionalidadeNome("Captação de AA Extração");

        assertThat(service.getCargoDepartamentoFuncionalidadeByFiltro(filtros)).isEmpty();
    }

    @Test
    public void getDepartamentoByCargo_deveRetornarUmaListaDeDepartamentos_quandoExistirDepartamentosVinculadosAoCargo() {
        var cargoId = 200;
        var filtros = new CargoDepartamentoFuncionalidadeFiltros();
        filtros.setCargoId(cargoId);
        when(repository.findAllDepartamentos(filtros.toPredicate()))
            .thenReturn(DepartamentoHelper.umaListaDepartamentos());

        assertThat(service.getDepartamentoByCargo(cargoId))
            .extracting("id", "nome", "codigo", "situacao")
            .containsExactly(tuple(1, "Departamento 1", CodigoDepartamento.COMERCIAL, ESituacao.A),
                tuple(2, "Departamento 2", CodigoDepartamento.COMERCIAL, ESituacao.A));
    }

    @Test
    public void getDepartamentoByCargo_deveRetornarVazio_quandoNaoExistirDepartamentosVinculadosAoCargoId() {
        assertThat(service.getDepartamentoByCargo(201))
            .isEmpty();
    }

    @Test
    public void save_deveSalvarIdsDeCargoDepartamentoFuncionalidade_seSolicitado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoAdmin());

        service.save(novasFuncionalidades());

        verify(repository, times(1)).save(argumentCaptorListaCargoDeptoFuncionalidade.capture());

        assertThat(argumentCaptorListaCargoDeptoFuncionalidade.getValue())
            .extracting("cargo.id", "departamento.id", "funcionalidade.id")
            .containsExactly(
                Tuple.tuple(1, 1, 1),
                Tuple.tuple(1, 1, 2),
                Tuple.tuple(1, 1, 3),
                Tuple.tuple(1, 1, 4)
            );
    }

    @Test
    public void save_naoDeveSalvarIdsDeCargoDepartamentoFuncionalidade_sePermissoesRepetidas() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoAdmin());

        var predicate = getPredicate();

        when(repository.findFuncionalidadesPorCargoEDepartamento(predicate.build()))
            .thenReturn(umaListaDeFuncionalidadesRepetidas());

        service.save(funcionalidadesRepetidas());

        verify(repository, times(1)).save(argumentCaptorListaCargoDeptoFuncionalidade.capture());

        assertThat(argumentCaptorListaCargoDeptoFuncionalidade.getValue()).isEmpty();
    }

    @Test
    public void remover_deveRemoverUmaFuncionalidade_seSolicitado() {
        service.remover(100);

        verify(repository, times(1)).delete(100);
    }

    @Test
    public void deslogar_deveDeslogarUsuarios_seSolicitado() {
        when(usuarioRepository.findAllByCargoAndDepartamento(new Cargo(50), new Departamento(50)))
            .thenReturn(umaListaDeUsuariosAdminSimples());

        doNothing().when(autenticacaoService).logout(anyString());

        service.deslogar(50, 50);

        verify(autenticacaoService, times(4)).logout(anyString());
    }

    private List<CargoDepartamentoFuncionalidade> umaPageCargoDeptoFuncionalidadesGerenciarAas() {
        return List.of(
            umCargoDeptoFuncionalidadeDeSocio(1, funcionalidadeGerenciarAas()),
            umCargoDeptoFuncionalidadeDeConsultor(2, funcionalidadeGerenciarAas()),
            umCargoDeptoFuncionalidadeDeVendedor(3, funcionalidadeGerenciarAas()),
            umCargoDeptoFuncionalidadeDeAnalista(4, funcionalidadeGerenciarAas()),
            umCargoDeptoFuncionalidadeDeAdministrador(5, funcionalidadeGerenciarAas())
        );
    }

    private CargoDepartamentoFuncionalidadePredicate getPredicate() {
        var predicate = new CargoDepartamentoFuncionalidadePredicate();
        predicate.comCargo(1);
        predicate.comDepartamento(1);
        return predicate;
    }
}
