package br.com.xbrain.autenticacao.modules.comum.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
public class UfRepositoryTest {

    @Autowired
    private UfRepository ufRepository;

    @Test
    public void findByOrderByNomeAsc_umaListaDeUfsOrdenadaPeloNome_quandoSolicitado() {
        assertThat(ufRepository.findByOrderByNomeAsc())
                .extracting("nome")
                .containsExactly("ACRE", "ALAGOAS", "AMAPA", "AMAZONAS", "BAHIA", "CEARA", "DISTRITO FEDERAL", "ESPIRITO SANTO",
                        "GOIAS", "MARANHAO", "MATO GROSSO", "MATO GROSSO DO SUL", "MINAS GERAIS", "PARA", "PARAIBA", "PARANA",
                        "PERNAMBUCO", "PIAUI", "RIO DE JANEIRO", "RIO GRANDE DO NORTE", "RIO GRANDE DO SUL", "RONDONIA",
                        "RORAIMA", "SANTA CATARINA", "SAO PAULO", "SERGIPE", "TOCANTINS");
    }
}