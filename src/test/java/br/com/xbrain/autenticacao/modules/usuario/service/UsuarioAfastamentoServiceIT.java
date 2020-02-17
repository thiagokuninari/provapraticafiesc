package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioInativacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioAfastamentoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.AFASTAMENTO;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_database_oracle.sql"})
public class UsuarioAfastamentoServiceIT {

    @Autowired
    private UsuarioAfastamentoService service;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private UsuarioAfastamentoRepository repository;

    @Test
    public void save_deveGerarORegistroDeAfastamemto_quandoOMotivoDeInativacaoForAfastamento() {
        service.save(
                usuarioService.findByIdCompleto(101),
                UsuarioInativacaoDto
                        .builder()
                        .codigoMotivoInativacao(AFASTAMENTO)
                        .dataInicio(LocalDate.of(2019, 1, 1))
                        .dataFim(LocalDate.of(2019, 2, 1))
                        .build());

        assertThat(repository.findAll())
                .extracting("inicio", "fim")
                .contains(
                        tuple(LocalDate.of(2019, 1, 1),
                                LocalDate.of(2019, 2, 1)));
    }

    @Test
    public void save_deveNaoGerarORegistroDeAfastamento_quandoNaoConterDataInicialEDataFim() {
        service.save(
                usuarioService.findByIdCompleto(101),
                UsuarioInativacaoDto
                        .builder()
                        .codigoMotivoInativacao(AFASTAMENTO)
                        .build());

        assertThat(repository.findAll()).isEmpty();
    }
}
