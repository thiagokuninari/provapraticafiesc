package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static helpers.Usuarios.SOCIO_AA;
import static org.assertj.core.api.Java6Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class FuncionalidadeServiceTest {

    private static Integer USUARIO_SOCIO_ID = 226;
    private static Integer CARGO_SOCIO_ID = 41;
    private static Integer DEPARTAMENTO_SOCIO_ID = 40;

    @Autowired
    private FuncionalidadeService service;

    @Test
    public void getPermissoes_permissosDoUsuario_somentePermitidasAoUsuario() {
        List<SimpleGrantedAuthority> permissoes = service.getPermissoes(umUsuarioSocio());

        assertThat(permissoes)
                .isNotEmpty()
                .extracting("authority")
                .contains("ROLE_AUT_2031");
    }

    @Test
    public void getFuncionalidadesPermitidasAoUsuarioComCanal_deveAgruparAsFuncionalidadesDoCargoEspeciaisComDistinct() {
        List<Funcionalidade> funcionalidades =
                service.getFuncionalidadesPermitidasAoUsuarioComCanal(umUsuarioSocio());

        assertThat(funcionalidades)
            .extracting("nome")
            .containsExactly(
                "Visualizar Tabulação Manual",
                "Visualizar Agendamento",
                "Relatório - Resumo de Mailing",
                "Relatório - Ticket Médio Analítico",
                "Relatório - Ticket Médio por Vendedor",
                "Relatório - Gerenciamento Operacional",
                "Cadastrar venda para o vendedor D2D");
    }

    @Test
    public void getFuncionalidadesPermitidasAoUsuario_listaDeFuncionalidades_quandoUsuarioCargoMsoConsultor() {
        var usuario = Usuario.builder()
                .id(100)
                .cargo(umCargoMsoConsultor())
                .departamento(new Departamento(21))
                .nome("RENATO")
                .build();

        assertThat(service.getFuncionalidadesPermitidasAoUsuario(usuario))
                .hasSize(39);
    }

    private Cargo umCargoMsoConsultor() {
        return Cargo.builder()
                .id(22)
                .codigo(CodigoCargo.MSO_CONSULTOR)
                .nivel(umNivelMso())
                .build();
    }

    private Nivel umNivelMso() {
        return Nivel.builder()
                .id(2)
                .codigo(CodigoNivel.MSO)
                .build();
    }

    private Usuario umUsuarioSocio() {
        return Usuario
                .builder()
                .id(USUARIO_SOCIO_ID)
                .email(SOCIO_AA)
                .cargo(Cargo.builder().id(CARGO_SOCIO_ID).build())
                .departamento(Departamento.builder().id(DEPARTAMENTO_SOCIO_ID).build())
                .build();
    }
}
