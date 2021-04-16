package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.NivelTipoVisualizacao;
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
public class NivelServiceTest {

    @Autowired
    private NivelService service;
    @MockBean
    private AutenticacaoService autenticacaoService;

    @Test
    public void getPermitidosPorNivel_deveRetornarXbrain_quandoOUsuarioForXbrain() {
        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(UsuarioAutenticado
                        .builder()
                        .nivelCodigo("XBRAIN")
                        .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole())))
                        .build());

        assertThat(service.getPermitidos(NivelTipoVisualizacao.CADASTRO))
                .extracting("id", "nome")
                .contains(
                        tuple(4, "X-BRAIN"));
    }

    @Test
    public void getPermitidosPorNivel_deveIgnorarXbrain_quandoOUsuarioNaoForXbrain() {
        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(UsuarioAutenticado
                        .builder()
                        .nivelCodigo("MSO")
                        .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole())))
                        .build());

        assertThat(service.getPermitidos(NivelTipoVisualizacao.CADASTRO))
                .extracting("id", "nome")
                .doesNotContain(
                        tuple(4, "X-BRAIN"));
    }

    @Test
    public void getPermitidosPorNivel_deveVisualizarSomenteProprioNivel_quandoNaoTiverPermissaoVisualizarGeral() {
        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(UsuarioAutenticado
                        .builder()
                        .nivelCodigo("MSO")
                        .build());

        assertThat(service.getPermitidos(NivelTipoVisualizacao.CADASTRO))
                .extracting("id", "nome")
                .contains(
                        tuple(2, "MSO"));
    }

    @Test
    public void getPermitidosPorNivel_deveVisualizarTodosOsNiveis_quandoTiverPermissaoVisualizarGeral() {
        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(UsuarioAutenticado
                        .builder()
                        .nivelCodigo("MSO")
                        .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole())))
                        .build());

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
                        tuple(18, "Backoffice"));
    }
}
