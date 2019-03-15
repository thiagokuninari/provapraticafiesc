package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
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
import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Sql(scripts = { "classpath:/tests_database.sql"})
public class FuncionalidadeServiceTest {

    private static Integer USUARIO_SOCIO_ID = 226;
    private static Integer CARGO_SOCIO_ID = 41;
    private static Integer DEPARTAMENTO_SOCIO_ID = 40;

    @Autowired
    private FuncionalidadeService service;

    @Test
    public void getPermissoes_permissosDoUsuario_somentePermitidasAoUsuario() {
        List<SimpleGrantedAuthority> permissoes = service.getPermissoes(umUsuarioSocio());

        assertEquals(27, permissoes.size());
        assertEquals("ROLE_AUT_2031", permissoes.get(0).getAuthority());
    }

    @Test
    public void getFuncionalidadesPermitidasAoUsuarioComCanal_deveAgruparAsFuncionalidadesDoCargoEspeciaisComDistinct() {
        List<Funcionalidade> funcionalidades =
                service.getFuncionalidadesPermitidasAoUsuarioComCanal(umUsuarioSocio());

        assertEquals(5, funcionalidades.size());
        assertEquals("Relatório - Resumo de Mailing", funcionalidades.get(0).getNome());
        assertEquals("Relatório - Ticket Médio Analítico", funcionalidades.get(1).getNome());
        assertEquals("Relatório - Ticket Médio por Vendedor", funcionalidades.get(2).getNome());
        assertEquals("Relatório - Gerenciamento Operacional", funcionalidades.get(3).getNome());
        assertEquals("Cadastrar venda para o vendedor D2D", funcionalidades.get(4).getNome());
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
