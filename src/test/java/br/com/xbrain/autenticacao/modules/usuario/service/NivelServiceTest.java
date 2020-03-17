package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.NivelTipoVisualizacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
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
import java.util.Set;

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
                    .usuario(Usuario
                        .builder()
                        .canais(Set.of(ECanal.D2D_PROPRIO))
                        .build())
                    .cargoCodigo(CodigoCargo.EXECUTIVO)
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
                    .usuario(Usuario
                        .builder()
                        .canais(Set.of(ECanal.D2D_PROPRIO))
                        .build())
                    .cargoCodigo(CodigoCargo.EXECUTIVO)
                    .cargoCodigo(CodigoCargo.EXECUTIVO)
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
                .usuario(Usuario
                    .builder()
                    .canais(Set.of(ECanal.D2D_PROPRIO))
                    .build())
                .cargoCodigo(CodigoCargo.EXECUTIVO)
                .nivelCodigo("MSO")
                .build());

        assertThat(service.getPermitidos(NivelTipoVisualizacao.CADASTRO))
            .extracting("id", "nome")
            .contains(
                tuple(2, "MSO"));
    }

    @Test
    public void getPermitidosPorNivel_deveVisualizarSeuNivelENivelAgente_quandoSemPermisaoeSendoGerenciaOperacao() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado
                .builder()
                .usuario(Usuario
                    .builder()
                    .canais(Set.of(ECanal.D2D_PROPRIO))
                    .build())
                .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
                .nivelCodigo(CodigoNivel.OPERACAO.name())
                .build());

        assertThat(service.getPermitidos(NivelTipoVisualizacao.CADASTRO))
            .extracting("id", "nome")
            .contains(
                tuple(1, "Operação")
            );
    }

    @Test
    public void getPermitidosPorNivel_deveVisualizarSeuNivel_quandoSemPermisaoeSendoGerenciaOperacao() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado
                .builder()
                .usuario(Usuario
                    .builder()
                    .canais(Set.of(ECanal.AGENTE_AUTORIZADO))
                    .build())
                .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
                .nivelCodigo(CodigoNivel.OPERACAO.name())
                .build());

        assertThat(service.getPermitidosParaComunicados())
            .extracting("id", "nome")
            .contains(
                tuple(3, "Agente Autorizado"),
                tuple(1, "Operação")
            );
    }

    @Test
    public void getPermitidosPorNivel_deveVisualizarTodosOsNiveis_quandoTiverPermissaoVisualizarGeral() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado
                .builder()
                .usuario(Usuario
                    .builder()
                    .canais(Set.of(ECanal.D2D_PROPRIO))
                    .build())
                .cargoCodigo(CodigoCargo.EXECUTIVO)
                        .nivelCodigo("MSO")
                        .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole())))
                        .build());

        assertThat(service.getPermitidos(NivelTipoVisualizacao.CADASTRO))
                .extracting("id", "nome")
                .containsExactly(
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
                        tuple(8, "Receptivo"));
    }
}
