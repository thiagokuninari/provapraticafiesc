package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioInativacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioFeriasRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_database_oracle.sql"})
public class UsuarioFeriasServiceIT {

    @Autowired
    private UsuarioFeriasService service;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private UsuarioFeriasRepository repository;

    @Test
    public void save_deveGerarORegistroDeFerias_quandoOMotivoDeInativacaoForFerias() {
        service.save(
                usuarioService.findByIdCompleto(101),
                UsuarioInativacaoDto
                        .builder()
                        .codigoMotivoInativacao(CodigoMotivoInativacao.FERIAS)
                        .dataInicio(LocalDate.of(2019, 1, 1))
                        .dataFim(LocalDate.of(2019, 2, 1))
                        .build());

        assertThat(repository.findAll())
                .extracting("inicio", "fim")
                .contains(
                        tuple(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 2, 1)));
    }

    @Test
    public void save_deveNaoGerarORegistroDeFerias_quandoOMotivoDeInativacaoNaoForFerias() {
        service.save(
                usuarioService.findByIdCompleto(101),
                UsuarioInativacaoDto
                        .builder()
                        .codigoMotivoInativacao(CodigoMotivoInativacao.FERIAS)
                        .build());

        assertThat(repository.findAll()).isEmpty();
    }
}
