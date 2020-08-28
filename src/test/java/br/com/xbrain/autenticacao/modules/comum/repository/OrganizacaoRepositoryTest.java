package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.predicate.OrganizacaoPredicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql(scripts = {"classpath:/tests_organizacao.sql"})
public class OrganizacaoRepositoryTest {

    @Autowired
    private OrganizacaoRepository organizacaoRepository;

    @Test
    public void findAll_todasOrganizacoes_quandoPesquisar() {
        assertThat(organizacaoRepository.findAll())
                .hasSize(5)
                .extracting("codigo", "nome")
                .contains(tuple("BCC", "Brasil Center"),
                        tuple("CALLINK", "Callink"),
                        tuple("PROPRIO", "Próprio"),
                        tuple("ATENTO", "Atento"),
                        tuple("VGX", "VGX"));
    }

    @Test
    public void findAll_organizacoesFiltradas_quandoPorNivelId() {
        var predicate = new OrganizacaoPredicate().comNivel(1);

        assertThat(organizacaoRepository.findByPredicate(predicate.build()))
            .hasSize(1)
            .extracting("codigo", "nome")
            .contains(tuple("BCC", "Brasil Center"));
    }

    @Test
    public void findAll_organizacoesFiltradas_quandoPorId() {
        var predicate = new OrganizacaoPredicate().comId(2);

        assertThat(organizacaoRepository.findByPredicate(predicate.build()))
            .hasSize(1)
            .extracting("codigo", "nome")
            .contains(tuple("CALLINK", "Callink"));
    }
}
