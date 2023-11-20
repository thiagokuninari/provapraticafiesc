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
            .extracting("value", "label")
            .containsExactlyInAnyOrder(
                tuple(40, "Aceite - Agente Autorizado"),
                tuple(1, "Analista - Operação"),
                tuple(43, "Aprendiz - Agente Autorizado"),
                tuple(44, "Assistente - Agente Autorizado"),
                tuple(2, "Assistente - Operação"),
                tuple(94, "Assistente Hunter - Operação"),
                tuple(78, "Back Office D2D - Agente Autorizado"),
                tuple(56, "Back Office Televendas - Agente Autorizado"),
                tuple(84, "BackOffice* - Agente Autorizado"),
                tuple(3, "Consultor - Operação"),
                tuple(45, "Coordenador - Agente Autorizado"),
                tuple(4, "Coordenador - Operação"),
                tuple(6, "Diretor - Operação"),
                tuple(46, "Empresário - Agente Autorizado"),
                tuple(5, "Executivo - Operação"),
                tuple(95, "Executivo Hunter - Operação"),
                tuple(47, "Gerente - Agente Autorizado"),
                tuple(7, "Gerente - Operação"),
                tuple(82, "Gerente* - Agente Autorizado"),
                tuple(120, "Operador Televendas - Operação"),
                tuple(48, "Supervisor - Agente Autorizado"),
                tuple(10, "Supervisor - Operação"),
                tuple(81, "Supervisor* - Agente Autorizado"),
                tuple(41, "Sócio Principal - Agente Autorizado"),
                tuple(42, "Sócio Secundário - Agente Autorizado"),
                tuple(9, "Técnico - Operação"),
                tuple(121, "Técnico Vendedor - Agente Autorizado"),
                tuple(1253, "Supervisor Técnico - Agente Autorizado"),
                tuple(1254, "Coordenador Técnico - Agente Autorizado"),
                tuple(1255, "Gerente da Equipe Técnico - Agente Autorizado"),
                tuple(1256, "Técnico Segmentado - Agente Autorizado"),
                tuple(8, "Vendedor - Operação"),
                tuple(79, "Vendedor Back Office D2D - Agente Autorizado"),
                tuple(80, "Vendedor Back Office Televendas  - Agente Autorizado"),
                tuple(85, "Vendedor BackOffice* - Agente Autorizado"),
                tuple(57, "Vendedor D2D - Agente Autorizado"),
                tuple(49, "Vendedor Híbrido - Agente Autorizado"),
                tuple(58, "Vendedor Televendas - Agente Autorizado"),
                tuple(83, "Vendedor* - Agente Autorizado"),
                tuple(98, "Executivo de Vendas - Operação"),
                tuple(202, "Assistente Relacionamento - Agente Autorizado"),
                tuple(201, "Cliente Loja Futuro - Agente Autorizado"),
                tuple(503, "Backoffice Internet - Operação"),
                tuple(502, "Coordenador Internet - Operação"),
                tuple(500, "Gerente Internet - Operação"),
                tuple(501, "Supervisor Internet - Operação"),
                tuple(504, "Vendedor Internet - Operação")
            );
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
