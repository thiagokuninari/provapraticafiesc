package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ENivel;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.EquipeVendasService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ETipoImplantacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.querydsl.core.types.Predicate;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
public class SolicitacaoRamalServiceTest {

    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private SolicitacaoRamalRepository repository;
    @Autowired
    private SolicitacaoRamalService service;
    @MockBean
    private UsuarioService usuarioService;
    @MockBean
    private AgenteAutorizadoService agenteAutorizadoService;
    @MockBean
    private AgenteAutorizadoNovoService agenteAutorizadoNovoService;
    @MockBean
    private EquipeVendasService equipeVendasService;

    @Test
    public void calcularDataFinalizacao_quandoHouverRegistros() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder()
                .nivelCodigo(ENivel.XBRAIN.name()).build());

        when(repository.findAllByPredicate(any())).thenReturn(umaListaSolicitacaoRamal());

        service.calcularDataFinalizacao(new SolicitacaoRamalFiltros());

        verify(repository, times(1)).save(umaListaSolicitacaoRamal());
    }

    @Test
    public void calcularDataFinalizacao_quandoNaoHouverRegistros() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder()
                .nivelCodigo(ENivel.XBRAIN.name()).build());

        when(repository.findAllByPredicate(any())).thenReturn(umaListaSolicitacaoRamalEmpty());

        service.calcularDataFinalizacao(new SolicitacaoRamalFiltros());

        verify(repository, never()).save(umaListaSolicitacaoRamalEmpty());
    }

    @Test
    public void findById_deveRetornarSolicitacao_quandoSolicitacaoForEncontrada() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado());
        when(repository.findById(1))
            .thenReturn(Optional.of(umaSolicitacaoRamalCanalD2d(1)));

        assertThat(service.findById(1)).isEqualTo(umaSolicitacaoRamalCanalD2d(1));

        verify(repository, atLeastOnce()).findById(eq(1));
    }

    @Test
    public void findById_deveLancarException_seSolicitacaoNaoForEncontrada() {
        when(repository.findById(5)).thenReturn(Optional.of(umaSolicitacaoRamalCanalD2d(5)));
        assertThat(service.findById(5)).isEqualTo(umaSolicitacaoRamalCanalD2d(5));
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.findById(1))
            .withMessage("Solicitação não encontrada.");

        verify(repository, atLeastOnce()).findById(eq(1));
    }

    @Test
    public void getAll_deveListarSolicitacoes_seTodosOsParametrosPreenchidos() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(repository.findAll(any(PageRequest.class), any(Predicate.class))).thenReturn((umaPageSolicitacaoRamal()));
        when(usuarioService.findComplete(1)).thenReturn(Usuario.builder().id(1).nome("teste").build());

        var filtros = new SolicitacaoRamalFiltros();
        filtros.setAgenteAutorizadoId(1);
        filtros.setSituacao(ESituacaoSolicitacao.PENDENTE);
        filtros.setCanal(ECanal.AGENTE_AUTORIZADO);

        var response = service.getAll(new PageRequest(), filtros);

        assertThat(response)
            .extracting("id", "canal", "dataCadastro",
                "situacao", "agenteAutorizadoId").containsExactly(

                Tuple.tuple(1, ECanal.AGENTE_AUTORIZADO,
                    LocalDateTime.of(2022, 02, 10, 10, 00, 00),
                    ESituacaoSolicitacao.PENDENTE, 1),

                Tuple.tuple(2, ECanal.AGENTE_AUTORIZADO,
                    LocalDateTime.of(2022, 02, 10, 10, 00, 00),
                    ESituacaoSolicitacao.PENDENTE, 1)
            );
    }

    @Test
    public void getAll_deveLancarException_seParametroAgenteAutorizadoIdNaoInformado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(repository.findAll(any(PageRequest.class), any(Predicate.class))).thenReturn((umaPageSolicitacaoRamal()));
        when(usuarioService.findComplete(1)).thenReturn(Usuario.builder().id(1).nome("teste").build());

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() ->
                service.getAll(new PageRequest(), new SolicitacaoRamalFiltros()))
            .withMessage("É necessário enviar o parâmetro agente autorizado id.");

        verify(repository, never()).findAll(any(PageRequest.class), any(Predicate.class));
        verify(usuarioService, never()).findComplete(1);
    }

    private SolicitacaoRamalRequest criaSolicitacaoRamal(Integer id, Integer aaId) {
        return SolicitacaoRamalRequest.builder()
            .id(id)
            .quantidadeRamais(38)
            .canal(ECanal.D2D_PROPRIO)
            .subCanalId(3)
            .agenteAutorizadoId(aaId)
            .melhorHorarioImplantacao(LocalTime.of(10, 00))
            .melhorDataImplantacao(LocalDate.of(2019, 01, 25))
            .tipoImplantacao(ETipoImplantacao.ESCRITORIO.getCodigo())
            .emailTi("reanto@ti.com.br")
            .telefoneTi("(18) 3322-2388")
            .usuariosSolicitadosIds(Arrays.asList(100, 101))
            .build();
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        return UsuarioAutenticado.builder()
            .id(1)
            .usuario(Usuario.builder().id(1).build())
            .cargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_SOCIO)
            .build();
    }

    private UsuarioAutenticado umUsuarioAutenticadoOperacao() {
        return UsuarioAutenticado.builder()
            .id(1)
            .usuario(Usuario.builder().id(1).build())
            .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
            .build();
    }

    private AgenteAutorizadoResponse criaAa() {
        return AgenteAutorizadoResponse.builder()
            .id("303030")
            .cnpj("81733187000134")
            .nomeFantasia("Fulano")
            .discadoraId(1)
            .razaoSocial("RAZAO SOCIAL AA")
            .build();
    }

    private List<SolicitacaoRamal> umaListaSolicitacaoRamal() {
        return List.of(
            umaSolicitacaoRamal(1),
            umaSolicitacaoRamal(2)
        );
    }

    private PageImpl<SolicitacaoRamal> umaPageSolicitacaoRamal() {
        return new PageImpl<>(
            List.of(umaSolicitacaoRamal(1),
                umaSolicitacaoRamal(2))
        );
    }

    private List<SolicitacaoRamal> umaListaSolicitacaoRamalEmpty() {
        List<SolicitacaoRamal> lista = new ArrayList<>();
        return lista;
    }

    private SolicitacaoRamal umaSolicitacaoRamalCanalD2d(Integer id) {
        var solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.setId(id);
        solicitacaoRamal.setCanal(ECanal.D2D_PROPRIO);
        solicitacaoRamal.setSubCanalId(1);
        solicitacaoRamal.setDataCadastro(LocalDateTime.of(2022, 02, 10, 10, 00, 00));
        solicitacaoRamal.setMelhorDataImplantacao(LocalDate.of(2022, 12, 01));
        solicitacaoRamal.setUsuariosSolicitados(List.of(Usuario.builder().id(1).build()));
        solicitacaoRamal.setSituacao(ESituacaoSolicitacao.PENDENTE);
        solicitacaoRamal.setUsuario(Usuario.builder().id(1).nome("teste").build());
        solicitacaoRamal.setTipoImplantacao(ETipoImplantacao.ESCRITORIO);

        return solicitacaoRamal;
    }

    private SolicitacaoRamal umaSolicitacaoRamal(Integer id) {
        var solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.setId(id);
        solicitacaoRamal.setCanal(ECanal.AGENTE_AUTORIZADO);
        solicitacaoRamal.setAgenteAutorizadoId(1);
        solicitacaoRamal.setDataCadastro(LocalDateTime.of(2022, 02, 10, 10, 00, 00));
        solicitacaoRamal.setMelhorDataImplantacao(LocalDate.of(2022, 12, 01));
        solicitacaoRamal.setUsuariosSolicitados(List.of(Usuario.builder().id(1).build()));
        solicitacaoRamal.setSituacao(ESituacaoSolicitacao.PENDENTE);
        solicitacaoRamal.setUsuario(Usuario.builder().id(1).nome("teste").build());
        solicitacaoRamal.setTipoImplantacao(ETipoImplantacao.ESCRITORIO);

        return solicitacaoRamal;
    }
}
