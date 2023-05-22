package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoImportacao;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.predicate.FeriadoPredicate;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import br.com.xbrain.autenticacao.modules.mailing.service.MailingService;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/feriado-repository-test.sql"})
public class FeriadoServiceIT {

    @Autowired
    private FeriadoRepository feriadoRepository;
    @Autowired
    private FeriadoService feriadoService;
    @MockBean
    private FeriadoHistoricoService feriadoHistoricoService;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private CallService callService;
    @Autowired
    private EntityManager entityManager;
    @MockBean
    private MailingService mailingService;

    @Test
    public void obterFeriadosByFiltros_deveRetornarTodosFeriadosExcetoExcluidosEFeriadoFilhos_quandoNaoTemFiltro() {
        assertThat(feriadoService.obterFeriadosByFiltros(umPageRequest(0), new FeriadoFiltros()))
            .hasSize(10)
            .extracting("id", "nome", "dataFeriado", "tipoFeriado", "estadoId", "estadoNome",
                "cidadeId", "cidadeNome")
            .contains(tuple(100, "Feriado Nacional do Luis", LocalDate.of(2019, 7, 30),
                ETipoFeriado.NACIONAL, null, null, null, null));

        assertThat(feriadoService.obterFeriadosByFiltros(umPageRequest(1), new FeriadoFiltros()))
            .hasSize(7)
            .extracting("id", "nome", "dataFeriado", "tipoFeriado", "estadoId", "estadoNome",
                "cidadeId", "cidadeNome")
            .contains(
                tuple(101, "Feriado de Londrina do Luis", LocalDate.of(2019, 7, 28),
                    ETipoFeriado.MUNICIPAL, 1, "PARANA", 5578, "LONDRINA"),
                tuple(102, "Feriado de Maringá do Luis", LocalDate.of(2019, 7, 29),
                    ETipoFeriado.MUNICIPAL, 1, "PARANA", 3426, "MARINGA"),
                tuple(104, "Feriado de Santa Catarina", LocalDate.of(2019, 9, 23),
                    ETipoFeriado.ESTADUAL, 22, "SANTA CATARINA", null, null),
                tuple(107, "Feriado de Chapeco", LocalDate.of(2019, 9, 22),
                    ETipoFeriado.MUNICIPAL, 22, "SANTA CATARINA", 4498, "CHAPECO"));
    }

    @Test
    public void obterFeriadosByFiltro_deveRetornarTodosFeriadosNoEstado_quandoFiltroEstadoId() {
        var filtrosComEstado = FeriadoFiltros.builder()
            .estadoId(22)
            .build();

        assertThat(feriadoService.obterFeriadosByFiltros(umPageRequest(0), filtrosComEstado))
            .hasSize(10);
        assertThat(feriadoService.obterFeriadosByFiltros(umPageRequest(1), filtrosComEstado))
            .hasSize(2)
            .extracting("id", "nome", "dataFeriado", "tipoFeriado", "estadoId", "estadoNome",
                "cidadeId", "cidadeNome")
            .contains(
                tuple(104, "Feriado de Santa Catarina", LocalDate.of(2019, 9, 23),
                    ETipoFeriado.ESTADUAL, 22, "SANTA CATARINA", null, null),
                tuple(107, "Feriado de Chapeco", LocalDate.of(2019, 9, 22),
                    ETipoFeriado.MUNICIPAL, 22, "SANTA CATARINA", 4498, "CHAPECO"));
    }

    @Test
    public void obterFeriadosByFiltro_deveFiltrarFeriadosPelaData_quandoFiltroDataDoFeriado() {
        var filtrosComData = FeriadoFiltros.builder()
            .dataInicio(LocalDate.of(2019, 7, 29))
            .dataFim(LocalDate.of(2019, 9, 23))
            .build();

        assertThat(feriadoService.obterFeriadosByFiltros(umPageRequest(0), filtrosComData))
            .hasSize(7)
            .extracting("id", "nome", "dataFeriado", "tipoFeriado", "estadoId", "estadoNome",
                "cidadeId", "cidadeNome")
            .contains(
                tuple(100, "Feriado Nacional do Luis", LocalDate.of(2019, 7, 30),
                    ETipoFeriado.NACIONAL, null, null, null, null),
                tuple(102, "Feriado de Maringá do Luis", LocalDate.of(2019, 7, 29),
                    ETipoFeriado.MUNICIPAL, 1, "PARANA", 3426, "MARINGA"),
                tuple(104, "Feriado de Santa Catarina", LocalDate.of(2019, 9, 23),
                    ETipoFeriado.ESTADUAL, 22, "SANTA CATARINA", null, null),
                tuple(107, "Feriado de Chapeco", LocalDate.of(2019, 9, 22),
                    ETipoFeriado.MUNICIPAL, 22, "SANTA CATARINA", 4498, "CHAPECO"));
    }

    @Test
    public void obterFeriadosByFiltro_deveRetornarTodosFeriadosNaCidade_quandoFiltroCidadeId() {
        var filtrosMaringa = FeriadoFiltros.builder()
            .cidadeId(3426)
            .estadoId(1)
            .build();

        assertThat(feriadoService.obterFeriadosByFiltros(umPageRequest(0), filtrosMaringa))
            .hasSize(10);
        assertThat(feriadoService.obterFeriadosByFiltros(umPageRequest(1), filtrosMaringa))
            .hasSize(3)
            .extracting("id", "nome", "dataFeriado", "tipoFeriado", "estadoId", "estadoNome",
                "cidadeId", "cidadeNome")
            .contains(tuple(102, "Feriado de Maringá do Luis", LocalDate.of(2019, 7, 29),
                ETipoFeriado.MUNICIPAL, 1, "PARANA", 3426, "MARINGA"));
    }

    @Test
    public void obterFeriadosByFiltro_deveRetornarFeriadosFiltradosPelaDataECidade_quandoFiltroCidadeIdEData() {
        var filtrosChapeco = FeriadoFiltros.builder()
            .cidadeId(4498)
            .estadoId(22)
            .dataInicio(LocalDate.of(2019, 7, 29))
            .dataFim(LocalDate.of(2019, 9, 23))
            .build();

        assertThat(feriadoService.obterFeriadosByFiltros(umPageRequest(0), filtrosChapeco))
            .hasSize(3)
            .extracting("id", "nome", "dataFeriado", "tipoFeriado", "estadoId", "estadoNome",
                "cidadeId", "cidadeNome")
            .contains(
                tuple(100, "Feriado Nacional do Luis", LocalDate.of(2019, 7, 30),
                    ETipoFeriado.NACIONAL, null, null, null, null),
                tuple(104, "Feriado de Santa Catarina", LocalDate.of(2019, 9, 23),
                    ETipoFeriado.ESTADUAL, 22, "SANTA CATARINA", null, null),
                tuple(107, "Feriado de Chapeco", LocalDate.of(2019, 9, 22),
                    ETipoFeriado.MUNICIPAL, 22, "SANTA CATARINA", 4498, "CHAPECO"));
    }

    @Test
    public void obterFeriadosByFiltro_deveFiltrarFeriadosPeloTipo_quandoFiltroTipoDoFeriado() {
        var filtrosMunicipal = FeriadoFiltros.builder()
            .tipoFeriado(ETipoFeriado.MUNICIPAL)
            .build();

        assertThat(feriadoService.obterFeriadosByFiltros(umPageRequest(0), filtrosMunicipal))
            .hasSize(5)
            .extracting("id", "nome", "dataFeriado", "tipoFeriado", "estadoId", "estadoNome",
                "cidadeId", "cidadeNome")
            .contains(
                tuple(101, "Feriado de Londrina do Luis", LocalDate.of(2019, 7, 28),
                    ETipoFeriado.MUNICIPAL, 1, "PARANA", 5578, "LONDRINA"),
                tuple(102, "Feriado de Maringá do Luis", LocalDate.of(2019, 7, 29),
                    ETipoFeriado.MUNICIPAL, 1, "PARANA", 3426, "MARINGA"),
                tuple(107, "Feriado de Chapeco", LocalDate.of(2019, 9, 22),
                    ETipoFeriado.MUNICIPAL, 22, "SANTA CATARINA", 4498, "CHAPECO"));

        var filtrosNacional = FeriadoFiltros.builder()
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .estadoId(1)
            .build();
        assertThat(feriadoService.obterFeriadosByFiltros(umPageRequest(0), filtrosNacional))
            .hasSize(10);
    }

    @Test
    public void getFeriadoById_deveDarErro_quandoNaoExiste() {
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> feriadoService.getFeriadoById(99))
            .withMessage("Feriado não encontrado.");
    }

    @Test
    public void getFeriadoById_deveRetornarFeriado_quandoExiste() {
        assertThat(feriadoService.getFeriadoById(107))
            .extracting("id", "nome", "dataFeriado", "tipoFeriado", "estadoId", "estadoNome",
                "cidadeId", "cidadeNome")
            .containsExactlyInAnyOrder(107, "Feriado de Chapeco", LocalDate.of(2019, 9, 22),
                ETipoFeriado.MUNICIPAL, 22, "SANTA CATARINA", 4498, "CHAPECO");
    }

    @Test
    public void salvarFeriado_deveDarErro_quandoRequestNaoTiverOsDadosObrigatorios() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> feriadoService.salvarFeriado(umFeriadoEstadualRequest(null, null)))
            .withMessage("Para este Tipo de Feriado o campo ESTADO é obrigatório.");
    }

    @Test
    public void salvarFeriado_deveDarErro_quandoJaExisteFeriadoComOsMesmosDados() {
        var feriado = FeriadoRequest.builder()
            .dataFeriado("23/09/2019")
            .tipoFeriado(ETipoFeriado.ESTADUAL)
            .estadoId(22)
            .nome("Feriado de Santa Catarina")
            .build();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> feriadoService.salvarFeriado(feriado))
            .withMessage("Já existe feriado com os mesmos dados.");
    }

    @Test
    public void salvarFeriado_deveSalvarFeriadoNacional_quandoRequestCorreto() {
        assertThat(feriadoRepository.findAll())
            .hasSize(20);
        when(autenticacaoService.getUsuarioId()).thenReturn(1111);

        assertThat(feriadoService.salvarFeriado(umFeriadoNacionalRequest(null)))
            .extracting("nome", "dataFeriado", "tipoFeriado", "estadoId",
                "cidadeId", "feriadoNacional")
            .containsExactlyInAnyOrder("FERIADO NACIONAL", LocalDate.of(2019, 11, 12), ETipoFeriado.NACIONAL,
                null, null, Eboolean.V);

        assertThat(feriadoRepository.findAll())
            .hasSize(21);
        verify(feriadoHistoricoService, times(1)).salvarHistorico(any(), eq("CADASTRADO MANUAL"), any());
    }

    @Test
    public void salvarFeriado_deveSalvarFeriadoMunicipal_quandoRequestCorreto() {
        assertThat(feriadoRepository.findAll())
            .hasSize(20);
        when(autenticacaoService.getUsuarioId()).thenReturn(1111);

        assertThat(feriadoService.salvarFeriado(umFeriadoMunicipalRequest()))
            .extracting("nome", "dataFeriado", "tipoFeriado", "estadoId",
                "cidadeId", "feriadoNacional")
            .containsExactlyInAnyOrder("FERIADO MUNICIPAL", LocalDate.of(2019, 12, 12), ETipoFeriado.MUNICIPAL,
                8, 1765, Eboolean.F);

        assertThat(feriadoRepository.findAll())
            .hasSize(21);
        verify(feriadoHistoricoService, times(1)).salvarHistorico(any(), eq("CADASTRADO MANUAL"), any());
    }

    @Test
    public void salvarFeriado_deveSalvarFeriadoEstadualEOsFeriadosFilhosDele_quandoRequestCorreto() {
        assertThat(feriadoRepository.findAll())
            .hasSize(20);
        when(autenticacaoService.getUsuarioId()).thenReturn(1111);

        var feriadoEstadual = feriadoService.salvarFeriado(umFeriadoEstadualRequest(1, null));

        assertThat(feriadoEstadual)
            .extracting("nome", "dataFeriado", "tipoFeriado", "estadoId",
                "cidadeId", "feriadoNacional")
            .containsExactlyInAnyOrder("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                1, null, Eboolean.F);

        assertThat(feriadoRepository.findAll())
            .hasSize(40);

        assertThat(feriadoRepository.findAll(
            new FeriadoPredicate()
                .comFeriadoPaiId(feriadoEstadual.getId())
                .build()))
            .hasSize(19)
            .extracting("nome", "dataFeriado", "tipoFeriado", "uf.id",
                "cidade.id", "feriadoNacional", "feriadoPai.id")
            .containsExactlyInAnyOrder(
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 5578, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 3426, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 3237, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 3306, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 3423, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 3425, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 3407, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 3408, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 30817, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 30858, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 30891, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 30813, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 30732, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 30757, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 30553, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 30676, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 30848, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 30850, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    1, 30910, Eboolean.F, feriadoEstadual.getId()));

        verify(feriadoHistoricoService, times(1)).salvarHistorico(any(), eq("CADASTRADO MANUAL"), any());
    }

    @Test
    public void salvarFeriadoImportado_deveSalvarFeriado_quandoFeriadoNacional() {
        assertThat(feriadoRepository.findAll())
            .hasSize(20);
        when(autenticacaoService.getUsuarioId()).thenReturn(1111);

        assertThat(feriadoService.salvarFeriadoImportado(umaFeriadoImportacao(ETipoFeriado.NACIONAL, null, null)))
            .extracting("nome", "dataFeriado", "tipoFeriado", "uf.id",
                "cidade.id", "feriadoNacional")
            .containsExactlyInAnyOrder("FERIADO IMPORTADO", LocalDate.of(2019, 3, 22), ETipoFeriado.NACIONAL,
                null, null, Eboolean.V);

        assertThat(feriadoRepository.findAll())
            .hasSize(21);
        verify(feriadoHistoricoService, times(1)).salvarHistorico(any(), eq("IMPORTADO"), any());
    }

    @Test
    public void salvarFeriadoImportado_deveSalvarFeriado_quandoFeriadoMunicipal() {
        assertThat(feriadoRepository.findAll())
            .hasSize(20);
        when(autenticacaoService.getUsuarioId()).thenReturn(1111);

        assertThat(feriadoService.salvarFeriadoImportado(umaFeriadoImportacao(ETipoFeriado.MUNICIPAL, 8, 1765)))
            .extracting("nome", "dataFeriado", "tipoFeriado", "uf.id",
                "cidade.id", "feriadoNacional")
            .containsExactlyInAnyOrder("FERIADO IMPORTADO", LocalDate.of(2019, 3, 22), ETipoFeriado.MUNICIPAL,
                8, 1765, Eboolean.F);

        assertThat(feriadoRepository.findAll())
            .hasSize(21);
        verify(feriadoHistoricoService, times(1)).salvarHistorico(any(), eq("IMPORTADO"), any());
    }

    @Test
    public void salvarFeriadoImportado_deveSalvarFeriadoEFeriadoFilhos_quandoFeriadoEstadual() {
        assertThat(feriadoRepository.findAll())
            .hasSize(20);
        when(autenticacaoService.getUsuarioId()).thenReturn(1111);

        var feriadoEstadual = feriadoService.salvarFeriadoImportado(umaFeriadoImportacao(ETipoFeriado.ESTADUAL, 22, null));

        assertThat(feriadoEstadual)
            .extracting("nome", "dataFeriado", "tipoFeriado", "uf.id",
                "cidade.id", "feriadoNacional")
            .containsExactlyInAnyOrder("FERIADO IMPORTADO", LocalDate.of(2019, 3, 22), ETipoFeriado.ESTADUAL,
                22, null, Eboolean.F);

        assertThat(feriadoRepository.findAll())
            .hasSize(27);

        assertThat(feriadoRepository.findAll(
            new FeriadoPredicate()
                .comFeriadoPaiId(feriadoEstadual.getId())
                .build()))
            .hasSize(6)
            .extracting("nome", "dataFeriado", "tipoFeriado", "uf.id",
                "cidade.id", "feriadoNacional", "feriadoPai.id")
            .containsExactlyInAnyOrder(
                tuple("FERIADO IMPORTADO", LocalDate.of(2019, 3, 22), ETipoFeriado.ESTADUAL,
                    22, 4505, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO IMPORTADO", LocalDate.of(2019, 3, 22), ETipoFeriado.ESTADUAL,
                    22, 4498, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO IMPORTADO", LocalDate.of(2019, 3, 22), ETipoFeriado.ESTADUAL,
                    22, 34093, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO IMPORTADO", LocalDate.of(2019, 3, 22), ETipoFeriado.ESTADUAL,
                    22, 34116, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO IMPORTADO", LocalDate.of(2019, 3, 22), ETipoFeriado.ESTADUAL,
                    22, 34164, Eboolean.F, feriadoEstadual.getId()),
                tuple("FERIADO IMPORTADO", LocalDate.of(2019, 3, 22), ETipoFeriado.ESTADUAL,
                    22, 34178, Eboolean.F, feriadoEstadual.getId()));

        verify(feriadoHistoricoService, times(1)).salvarHistorico(any(), eq("IMPORTADO"), any());
    }

    @Test
    public void editarFeriado_deveDarErro_quandoRequestNaoTiverOsDadosObrigatorios() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> feriadoService.editarFeriado(umFeriadoEstadualRequest(null, 104)))
            .withMessage("Para este Tipo de Feriado o campo ESTADO é obrigatório.");
    }

    @Test
    public void editarFeriado_deveDarErro_quandoJaExisteFeriadoComOsMesmosDados() {
        var feriado = FeriadoRequest.builder()
            .id(101)
            .dataFeriado("29/07/2019")
            .tipoFeriado(ETipoFeriado.MUNICIPAL)
            .estadoId(1)
            .cidadeId(3426)
            .nome("Feriado de Maringá")
            .build();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> feriadoService.editarFeriado(feriado))
            .withMessage("Já existe feriado com os mesmos dados.");
    }

    @Test
    public void editarFeriado_deveDarErro_quandoFeriadoIdNaoExiste() {
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> feriadoService.editarFeriado(umFeriadoEstadualRequest(1, 99)))
            .withMessage("Feriado não encontrado.");
    }

    @Test
    public void editarFeriado_deveDarErro_quandoTipoFeriadoAlterado() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> feriadoService.editarFeriado(umFeriadoEstadualRequest(1, 100)))
            .withMessage("Não é permitido editar o Tipo do Feriado.");
    }

    @Test
    public void editarFeriado_deveEditarFeriadoNacional_quandoRequestCorreto() {
        assertThat(feriadoRepository.findAll())
            .hasSize(20);
        when(autenticacaoService.getUsuarioId()).thenReturn(1111);

        assertThat(feriadoService.editarFeriado(umFeriadoNacionalRequest(100)))
            .extracting("id", "nome", "dataFeriado", "tipoFeriado", "estadoId",
                "cidadeId", "feriadoNacional")
            .containsExactlyInAnyOrder(100, "FERIADO NACIONAL", LocalDate.of(2019, 11, 12), ETipoFeriado.NACIONAL,
                null, null, Eboolean.V);

        verify(feriadoHistoricoService, times(1)).salvarHistorico(any(), eq("EDITADO"), any());
        assertThat(feriadoRepository.findAll())
            .hasSize(20);
    }

    @Test
    public void editarFeriado_deveEditarFeriadoMunicipal_quandoRequestCorreto() {
        var editacaoRequest = FeriadoRequest.builder()
            .dataFeriado("29/06/2019")
            .id(102)
            .nome("Feriado de Maringá")
            .cidadeId(3426)
            .estadoId(1)
            .tipoFeriado(ETipoFeriado.MUNICIPAL)
            .build();
        assertThat(feriadoRepository.findAll())
            .hasSize(20);
        when(autenticacaoService.getUsuarioId()).thenReturn(1111);

        assertThat(feriadoService.editarFeriado(editacaoRequest))
            .extracting("id", "nome", "dataFeriado", "tipoFeriado", "estadoId",
                "cidadeId", "feriadoNacional")
            .containsExactlyInAnyOrder(102, "Feriado de Maringá", LocalDate.of(2019, 6, 29), ETipoFeriado.MUNICIPAL,
                1, 3426, Eboolean.F);

        verify(feriadoHistoricoService, times(1)).salvarHistorico(any(), eq("EDITADO"), any());
        assertThat(feriadoRepository.findAll())
            .hasSize(20);
    }

    @Test
    public void editarFeriado_deveEditarFeriadoEstadualEFeriadoFilhos_quandoEstadoIdNaoForAlterado() {
        assertThat(feriadoRepository.findAll())
            .hasSize(20);
        when(autenticacaoService.getUsuarioId()).thenReturn(1111);

        assertThat(feriadoService.editarFeriado(umFeriadoEstadualRequest(22, 104)))
            .extracting("id", "nome", "dataFeriado", "tipoFeriado", "estadoId",
                "cidadeId", "feriadoNacional")
            .containsExactlyInAnyOrder(104, "FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                22, null, Eboolean.F);
        refresh();

        verify(feriadoHistoricoService, times(1)).salvarHistorico(any(), eq("EDITADO"), any());
        assertThat(feriadoRepository.findAll())
            .hasSize(20);
        assertThat(feriadoRepository.findAll(new FeriadoPredicate().comFeriadoPaiId(104).build()))
            .hasSize(2)
            .extracting("id", "nome", "dataFeriado", "tipoFeriado", "uf.id",
                "cidade.id", "feriadoNacional", "feriadoPai.id")
            .containsExactlyInAnyOrder(
                tuple(105, "FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    22, 4505, Eboolean.F, 104),
                tuple(106, "FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    22, 4498, Eboolean.F, 104));
    }

    @Test
    public void editarFeriado_deveEditarFeriadoEstadualExluirFeriadoFilhosESalvarNovos_quandoEstadoIdForAlterado() {
        assertThat(feriadoRepository.findAll())
            .hasSize(20);
        when(autenticacaoService.getUsuarioId()).thenReturn(1111);

        var feriadoEditado = feriadoService.editarFeriado(umFeriadoEstadualRequest(19, 104));
        refresh();

        assertThat(feriadoEditado)
            .extracting("id", "nome", "dataFeriado", "tipoFeriado", "estadoId",
                "cidadeId", "feriadoNacional")
            .containsExactlyInAnyOrder(104, "FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                19, null, Eboolean.F);

        assertThat(feriadoRepository.findAll())
            .hasSize(24);

        assertThat(feriadoRepository.findAll(new FeriadoPredicate().comFeriadoPaiId(104).build()))
            .hasSize(6)
            .extracting("nome", "dataFeriado", "tipoFeriado", "uf.id",
                "cidade.id", "feriadoNacional", "feriadoPai.id", "situacao")
            .containsExactlyInAnyOrder(
                tuple("Feriado de Santa Catarina", LocalDate.of(2019, 9, 23), ETipoFeriado.ESTADUAL,
                    22, 4505, Eboolean.F, 104, ESituacaoFeriado.EXCLUIDO),
                tuple("Feriado de Santa Catarina", LocalDate.of(2019, 9, 23), ETipoFeriado.ESTADUAL,
                    22, 4498, Eboolean.F, 104, ESituacaoFeriado.EXCLUIDO),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    19, 3652, Eboolean.F, 104, ESituacaoFeriado.ATIVO),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    19, 30987, Eboolean.F, 104, ESituacaoFeriado.ATIVO),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    19, 31070, Eboolean.F, 104, ESituacaoFeriado.ATIVO),
                tuple("FERIADO ESTADUAL", LocalDate.of(2019, 7, 12), ETipoFeriado.ESTADUAL,
                    19, 31085, Eboolean.F, 104, ESituacaoFeriado.ATIVO));

        verify(feriadoHistoricoService, times(1)).salvarHistorico(any(), eq("EDITADO"), any());
    }

    @Test
    public void excluirFeriado_deveDarErro_quandoFeriadoIdNaoExiste() {
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> feriadoService.excluirFeriado(99))
            .withMessage("Feriado não encontrado.");
    }

    @Test
    public void excluirFeriado_deveExcluirFeriadoEGerarHistorico_quandoFeriadoNacionalOuMunicipal() {
        feriadoService.excluirFeriado(107);
        assertThat(feriadoService.findById(107))
            .extracting("nome", "situacao", "tipoFeriado", "cidade.id")
            .containsExactlyInAnyOrder("Feriado de Chapeco", ESituacaoFeriado.EXCLUIDO, ETipoFeriado.MUNICIPAL, 4498);

        feriadoService.excluirFeriado(100);
        assertThat(feriadoService.findById(100))
            .extracting("nome", "situacao", "tipoFeriado")
            .containsExactlyInAnyOrder("Feriado Nacional do Luis", ESituacaoFeriado.EXCLUIDO, ETipoFeriado.NACIONAL);

        verify(feriadoHistoricoService, times(2)).salvarHistorico(any(), eq("EXCLUIDO"), any());
    }

    @Test
    public void excluirFeriado_deveExcluirFeriadoEFeriadoFilhos_quandoFeriadoEstadual() {
        feriadoService.excluirFeriado(104);
        assertThat(feriadoService.findById(104))
            .extracting("nome", "situacao", "tipoFeriado", "uf.id")
            .containsExactlyInAnyOrder("Feriado de Santa Catarina", ESituacaoFeriado.EXCLUIDO, ETipoFeriado.ESTADUAL, 22);
        refresh();

        assertThat(feriadoRepository.findAll(new FeriadoPredicate().comFeriadoPaiId(104).build()))
            .hasSize(2)
            .extracting("nome", "dataFeriado", "tipoFeriado", "uf.id",
                "cidade.id", "feriadoNacional", "feriadoPai.id", "situacao")
            .containsExactlyInAnyOrder(
                tuple("Feriado de Santa Catarina", LocalDate.of(2019, 9, 23), ETipoFeriado.ESTADUAL,
                    22, 4505, Eboolean.F, 104, ESituacaoFeriado.EXCLUIDO),
                tuple("Feriado de Santa Catarina", LocalDate.of(2019, 9, 23), ETipoFeriado.ESTADUAL,
                    22, 4498, Eboolean.F, 104, ESituacaoFeriado.EXCLUIDO));

        verify(feriadoHistoricoService, times(1)).salvarHistorico(any(), eq("EXCLUIDO"), any());
    }

    private PageRequest umPageRequest(int pageNumber) {
        var pageRequest = new PageRequest();
        pageRequest.setPage(pageNumber);
        return pageRequest;
    }

    private FeriadoRequest umFeriadoEstadualRequest(Integer estadoId, Integer id) {
        return FeriadoRequest.builder()
            .id(id)
            .nome("FERIADO ESTADUAL")
            .tipoFeriado(ETipoFeriado.ESTADUAL)
            .estadoId(estadoId)
            .dataFeriado("12/07/2019")
            .build();
    }

    private FeriadoRequest umFeriadoNacionalRequest(Integer id) {
        return FeriadoRequest.builder()
            .id(id)
            .nome("FERIADO NACIONAL")
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .dataFeriado("12/11/2019")
            .build();
    }

    private FeriadoRequest umFeriadoMunicipalRequest() {
        return FeriadoRequest.builder()
            .nome("FERIADO MUNICIPAL")
            .tipoFeriado(ETipoFeriado.MUNICIPAL)
            .estadoId(8)
            .cidadeId(1765)
            .dataFeriado("12/12/2019")
            .build();
    }

    private FeriadoImportacao umaFeriadoImportacao(ETipoFeriado tipoFeriado, Integer ufId, Integer cidadeId) {
        return FeriadoImportacao.builder()
            .tipoFeriado(tipoFeriado)
            .dataFeriado(LocalDate.of(2019, 3, 22))
            .nome("FERIADO IMPORTADO")
            .motivoNaoImportacao(List.of())
            .uf(!isEmpty(ufId) ? new Uf(ufId) : null)
            .cidade(!isEmpty(cidadeId) ? new Cidade(cidadeId) : null)
            .build();
    }

    private void refresh() {
        entityManager.flush();
        entityManager.clear();
    }
}
