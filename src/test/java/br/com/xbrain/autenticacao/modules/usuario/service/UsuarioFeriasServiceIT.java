package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioInativacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioFeriasRepository;
import org.assertj.core.util.Streams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.Collectors;

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
                usuarioService.findById(101),
                UsuarioInativacaoDto
                        .builder()
                        .motivoInativacao(
                                MotivoInativacao
                                        .builder()
                                        .codigo(CodigoMotivoInativacao.FERIAS)
                                        .build())
                        .dataInicio("01/01/2019")
                        .dataFim("01/02/2019")
                        .build());

        assertThat(Streams.stream(repository.findAll()).collect(Collectors.toList()))
                .extracting("inicio", "fim")
                .contains(
                        tuple(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 2, 1)));
    }

    @Test
    public void save_deveNaoGerarORegistroDeFerias_quandoOMotivoDeInativacaoNaoForFerias() {
        service.save(
                usuarioService.findById(101),
                UsuarioInativacaoDto
                        .builder()
                        .motivoInativacao(
                                MotivoInativacao
                                        .builder()
                                        .codigo(CodigoMotivoInativacao.DEMISSAO)
                                        .build())
                        .build());

        assertThat(Streams.stream(
                repository.findAll())
                .collect(Collectors.toList()))
                .isEmpty();
    }
}
