package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepositoryCustom;
import br.com.xbrain.autenticacao.modules.permissao.service.CargoDepartamentoFuncionalidadeService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.predicate.DepartamentoPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.DepartamentoRepository;
import br.com.xbrain.autenticacao.modules.usuario.helpers.DepartamentoHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.DepartamentoHelper.umDepartamentoAdministrador;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.DepartamentoHelper.umDepartamentoHelpDesk;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DepartamentoServiceTest {

    private static Integer NIVEL_ID = 4;

    @InjectMocks
    private DepartamentoService service;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private CargoDepartamentoFuncionalidadeRepositoryCustom cargoDepartamentoFuncionalidadeRepositoryCustom;
    @Mock
    private CargoDepartamentoFuncionalidadeService cargoDepartamentoFuncionalidadeService;
    @Mock
    private DepartamentoRepository departamentoRepository;

    @Test
    public void getPermitidosPorNivel_deveRetornarTodosOsNiveis_quandoTiverPermissaoVisualizarGeral() {
        var usuarioAutenticado = UsuarioAutenticado.builder()
            .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole())))
            .build();

        var predicate = new DepartamentoPredicate()
            .doNivel(NIVEL_ID)
            .filtrarPermitidos(usuarioAutenticado);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticado);

        when(departamentoRepository.findAll(eq(predicate.build())))
            .thenReturn(List.of(umDepartamentoAdministrador(), umDepartamentoHelpDesk()));

        assertThat(service.getPermitidosPorNivel(NIVEL_ID))
            .extracting("id", "nome")
            .containsExactly(
                tuple(50, "Administrador"),
                tuple(51, "HelpDesk"));
    }

    @Test
    public void getPermitidosPorNivel_deveRetornarOProprioCargo_quandoNaoTiverPermissaoVisualizarGeral() {
        var usuarioAutenticado = UsuarioAutenticado.builder()
            .departamentoCodigo(CodigoDepartamento.HELP_DESK)
            .build();

        var predicate = new DepartamentoPredicate()
            .doNivel(NIVEL_ID)
            .filtrarPermitidos(usuarioAutenticado);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticado);

        when(departamentoRepository.findAll(eq(predicate.build())))
            .thenReturn(List.of(umDepartamentoHelpDesk()));

        assertThat(service.getPermitidosPorNivel(4))
            .extracting("id", "nome")
            .containsExactly(
                tuple(51, "HelpDesk"));
    }

    @Test
    public void getPermitidosPorCargo_deveRetornarUmaListaDeDepartamentos_quandoExistirDepartamentosVinculadosAoCargoId() {
        when(cargoDepartamentoFuncionalidadeService.getDepartamentoByCargo(200))
            .thenReturn(DepartamentoHelper.umaListaDepartamentos());

        assertThat(service.getPermitidosPorCargo(200))
            .extracting("id", "nome", "codigo", "situacao")
            .containsExactly(tuple(1, "Departamento 1", CodigoDepartamento.COMERCIAL, ESituacao.A),
                tuple(2, "Departamento 2", CodigoDepartamento.COMERCIAL, ESituacao.A));
    }

    @Test
    public void getPermitidosPorCargo_deveRetornarVazio_quandoNaoExistirDepartamentosVinculadosAoCargoId() {
        when(cargoDepartamentoFuncionalidadeService.getDepartamentoByCargo(201))
            .thenReturn(Collections.emptyList());

        assertThat(service.getPermitidosPorCargo(201))
            .isEmpty();
    }
}
