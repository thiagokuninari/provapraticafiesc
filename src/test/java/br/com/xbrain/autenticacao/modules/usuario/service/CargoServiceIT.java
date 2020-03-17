package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoSuperiorRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_database_oracle.sql", "classpath:/tests_hierarquia.sql"})
public class CargoServiceIT {

    @Autowired
    private CargoService service;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private CargoSuperiorRepository cargoSuperiorRepository;

    @Test
    public void getPermitidosAosComunicados_deveRetornarUsuariosDoAa_quandoGerenciaDeOperacao() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());

        assertThat(service.getPermitidosAosComunicados(List.of(1, 3)))
            .extracting("nome", "codigo", "situacao")
            .containsExactlyInAnyOrder(
                tuple("Aceite", "AGENTE_AUTORIZADO_ACEITE", A),
                tuple("Analista", null, A),
                tuple("Analista de Suporte", "AGENTE_AUTORIZADO_SUPERVISOR_XBRAIN", A),
                tuple("Aprendiz", null, A),
                tuple("Assistente", "ASSISTENTE_OPERACAO", A),
                tuple("Assistente", null, A),
                tuple("Assistente Hunter", "ASSISTENTE_HUNTER", A),
                tuple("Back Office D2D", "AGENTE_AUTORIZADO_BACKOFFICE_D2D", A),
                tuple("Back Office Televendas", "AGENTE_AUTORIZADO_BACKOFFICE_TELEVENDAS", A),
                tuple("BackOffice*", "AGENTE_AUTORIZADO_BACKOFFICE_TEMP", A),
                tuple("Consultor", null, A),
                tuple("Coordenador", "COORDENADOR_OPERACAO", A),
                tuple("Coordenador", null, A),
                tuple("Diretor", null, A),
                tuple("Empresário", null, A),
                tuple("Executivo", "EXECUTIVO", A),
                tuple("Executivo Hunter", "EXECUTIVO_HUNTER", A),
                tuple("Gerente", "AGENTE_AUTORIZADO_GERENTE", A),
                tuple("Gerente", "GERENTE_OPERACAO", A),
                tuple("Gerente*", "AGENTE_AUTORIZADO_GERENTE_TEMP", A),
                tuple("Supervisor", "SUPERVISOR_OPERACAO", A),
                tuple("Supervisor", "AGENTE_AUTORIZADO_SUPERVISOR", A),
                tuple("Supervisor*", "AGENTE_AUTORIZADO_SUPERVISOR_TEMP", A),
                tuple("Sócio Principal", "AGENTE_AUTORIZADO_SOCIO", A),
                tuple("Sócio Secundário", "AGENTE_AUTORIZADO_SOCIO_SECUNDARIO", A),
                tuple("Técnico", null, A),
                tuple("Vendedor", "VENDEDOR_OPERACAO", A),
                tuple("Vendedor Back Office D2D", "AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_D2D", A),
                tuple("Vendedor Back Office Televendas ", "AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_TELEVENDAS", A),
                tuple("Vendedor BackOffice*", "AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_TEMP", A),
                tuple("Vendedor D2D", "AGENTE_AUTORIZADO_VENDEDOR_D2D", A),
                tuple("Vendedor Híbrido", "AGENTE_AUTORIZADO_VENDEDOR_HIBRIDO", A),
                tuple("Vendedor Televendas", "AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS", A),
                tuple("Vendedor*", "AGENTE_AUTORIZADO_VENDEDOR_TEMP", A));
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        return UsuarioAutenticado
            .builder()
            .id(1)
            .nome("USUARIO")
            .email("USUARIO@TESTE.COM")
            .cargoId(6)
            .usuario(Usuario.builder()
                .canais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
                .build())
            .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
            .nivelCodigo(CodigoNivel.OPERACAO.name())
            .permissoes(List.of(new SimpleGrantedAuthority(AUT_VISUALIZAR_GERAL.getRole()),
                new SimpleGrantedAuthority(AUT_VISUALIZAR_USUARIO.getRole()),
                new SimpleGrantedAuthority(AUT_VISUALIZAR_USUARIOS_AA.getRole())))
            .build();
    }
}