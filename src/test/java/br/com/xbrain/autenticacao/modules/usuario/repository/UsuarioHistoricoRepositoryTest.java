package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioHistoricoPredicate;
import com.querydsl.core.BooleanBuilder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql", "classpath:/tests_usuario_historico.sql"})
public class UsuarioHistoricoRepositoryTest {

    @Autowired
    private UsuarioHistoricoRepository repository;

//    @Test
//    public void getUsuariosSemAcessoAoSistemaAposTrintaEDoisDias_doisUsuarios_usuariosQueNaoAcessaOSistemaATrintaEDoisDias() {
//        final List<Usuario> usuarios = repository.getUsuariosPorTempoDeInatividade(getPredicate());
//        assertEquals(2, usuarios.size());
//        assertThat(usuarios)
//                .extracting(Usuario::getSituacao)
//                .containsOnly(ESituacao.A);
//    }

    private BooleanBuilder getPredicate() {
        return new UsuarioHistoricoPredicate()
                .comDataCadastro()
                .build();
    }
}
