package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class DepartamentoServiceTest {

    @Autowired
    private DepartamentoService service;
    @MockBean
    private AutenticacaoService autenticacaoService;

    @Test
    public void getPermitidosPorNivel_deveRetornarTodosOsNiveis_quandoTiverPermissaoVisualizarGeral() {

        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(UsuarioAutenticado
                        .builder()
                        .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole())))
                        .build());

        assertThat(service.getPermitidosPorNivel(4))
                .extracting("id", "nome")
                .containsExactly(
                        tuple(50, "Administrador"),
                        tuple(51, "HelpDesk"));
    }

    @Test
    public void getPermitidosPorNivel_deveRetornarOProprioCargo_quandoNaoTiverPermissaoVisualizarGeral() {

        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(UsuarioAutenticado
                        .builder()
                        .departamentoCodigo(CodigoDepartamento.HELP_DESK)
                        .build());

        assertThat(service.getPermitidosPorNivel(4))
                .extracting("id", "nome")
                .containsExactly(
                        tuple(51, "HelpDesk"));
    }
}
