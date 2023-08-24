package br.com.xbrain.autenticacao.modules.organizacaoempresa.repository;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.predicate.OrganizacaoEmpresaPredicate;
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
@Sql(scripts = {"classpath:/tests_database.sql"})
public class OrganizacaoEmpresaRepositoryTest {

    @Autowired
    private OrganizacaoEmpresaRepository organizacaoEmpresaRepository;

    @Test
    public void findAll_todasOrganizacoes_quandoPesquisar() {
        assertThat(organizacaoEmpresaRepository.findAll())
            .hasSize(6)
            .extracting("codigo", "nome")
            .contains(tuple("BCC", "Brasil Center"),
                tuple("CALLINK", "Callink"),
                tuple("PROPRIO", "Próprio"),
                tuple("ATENTO", "Atento"),
                tuple("VGX", "VGX"),
                tuple("INTERNET", "INTERNET"));
    }

    @Test
    public void findAll_organizacoesFiltradas_quandoPorNivelId() {
        var predicate = new OrganizacaoEmpresaPredicate().comNivel(1);

        assertThat(organizacaoEmpresaRepository.findByPredicate(predicate.build()))
            .hasSize(1)
            .extracting("codigo", "nome")
            .contains(tuple("BCC", "Brasil Center"));
    }

    @Test
    public void findAll_organizacoesFiltradas_quandoPorId() {
        var predicate = new OrganizacaoEmpresaPredicate().comId(2);

        assertThat(organizacaoEmpresaRepository.findByPredicate(predicate.build()))
            .hasSize(1)
            .extracting("codigo", "nome")
            .contains(tuple("CALLINK", "Callink"));
    }

    @Test
    public void findById_organizacao_quandoExistir() {
        var response = organizacaoEmpresaRepository.findById(3).get();
        assertThat(response.getId()).isEqualTo(3);
        assertThat(response.getNome()).isEqualTo("Próprio");
        assertThat(response.getCodigo()).isEqualTo("PROPRIO");
    }
}
