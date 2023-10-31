package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ENivel;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalHistoricoRepository;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.querydsl.core.types.Predicate;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.SolicitacaoRamalHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SolicitacaoRamalServiceTest {

    @InjectMocks
    private SolicitacaoRamalService service;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private SolicitacaoRamalRepository repository;
    @Mock
    private SolicitacaoRamalHistoricoRepository historicoRepository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private SolicitacaoRamalServiceAa serviceAa;
    @Mock
    private SolicitacaoRamalServiceD2d serviceD2d;
    @Mock
    private ApplicationContext context;

    @Test
    public void calcularDataFinalizacao_deveSetarADataFinalizacao_seHouverRegistros() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder()
                .nivelCodigo(ENivel.XBRAIN.name()).build());

        when(repository.findAllByPredicate(any())).thenReturn(umaListaSolicitacaoRamal());

        service.calcularDataFinalizacao(new SolicitacaoRamalFiltros());

        verify(repository, times(1)).save(umaListaSolicitacaoRamal());
    }

    @Test
    public void calcularDataFinalizacao_naoDeveSetarADataFinalizacao_seNaoHouverRegistros() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder()
                .nivelCodigo(ENivel.XBRAIN.name()).build());

        when(repository.findAllByPredicate(any())).thenReturn(umaListaSolicitacaoRamalEmpty());

        service.calcularDataFinalizacao(new SolicitacaoRamalFiltros());

        verify(repository, never()).save(umaListaSolicitacaoRamalEmpty());
    }

    @Test
    public void findById_deveRetornarSolicitacao_quandoSolicitacaoForEncontrada() {
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
        when(repository.findAll(any(PageRequest.class), any(Predicate.class)))
            .thenReturn((umaPageSolicitacaoRamal()));

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

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() ->
                service.getAll(new PageRequest(), new SolicitacaoRamalFiltros()))
            .withMessage("Campo agente autorizado é obrigatório");

        verify(repository, never()).findAll(any(PageRequest.class), any(Predicate.class));
        verify(usuarioService, never()).findComplete(1);
    }

    @Test
    public void getAll_deveLancarException_seParametroEquipeIdNaoInformado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioGerenteOperacao());
        var filtro = new SolicitacaoRamalFiltros();
        filtro.setCanal(ECanal.D2D_PROPRIO);

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() ->
                service.getAll(new PageRequest(), filtro))
            .withMessage("Campo equipe é obrigatório");

        verify(repository, never()).findAll(any(PageRequest.class), any(Predicate.class));
    }

    @Test
    public void getAll_deveRetornarListaVazia_seNaoExistirSolicitacaoDeRamalDaEquipe() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(repository.findAll(any(PageRequest.class), any(Predicate.class))).thenReturn(new PageImpl<>(List.of()));

        assertThat(service.getAll(new PageRequest(), umaSolicitacaoFiltros())).isEmpty();
    }

    @Test
    public void save_deveMandarParaServiceAa_seCanalForAgenteAutorizado() {
        var request = criaSolicitacaoRamal(1, null);
        request.setCanal(ECanal.AGENTE_AUTORIZADO);
        when(serviceAa.save(request)).thenReturn(umaSolicitacaoRamalResponseAa(1));

        when(context.getBean(SolicitacaoRamalServiceAa.class)).thenReturn(serviceAa);

        service.save(request);

        verify(serviceAa, times(1)).save(request);
    }

    @Test
    public void save_deveMandarParaServiceD2d_seCanalForD2d() {
        var request = criaSolicitacaoRamal(1, null);
        request.setCanal(ECanal.D2D_PROPRIO);
        request.setSubCanalId(1);
        when(serviceD2d.save(request)).thenReturn(umaSolicitacaoRamalResponseD2d(1));

        when(context.getBean(SolicitacaoRamalServiceD2d.class)).thenReturn(serviceD2d);

        service.save(request);

        verify(serviceD2d, times(1)).save(request);
    }

    @Test
    public void getDadosAdicionais_deveMandarParaServiceAa_seCanalForAgenteAutorizado() {
        when(context.getBean(SolicitacaoRamalServiceAa.class)).thenReturn(serviceAa);

        when(serviceAa.getDadosAdicionais(umFiltrosSolicitacao(ECanal.AGENTE_AUTORIZADO, null, 1)))
            .thenReturn(dadosAdicionaisResponse());

        service.getDadosAdicionais(umFiltrosSolicitacao(ECanal.AGENTE_AUTORIZADO, null, 1));

        verify(serviceAa, times(1))
            .getDadosAdicionais(umFiltrosSolicitacao(ECanal.AGENTE_AUTORIZADO, null, 1));
    }

    @Test
    public void getDadosAdicionais_deveMandarParaServiceD2d_seCanalForD2d() {
        when(context.getBean(SolicitacaoRamalServiceD2d.class)).thenReturn(serviceD2d);

        when(serviceD2d.getDadosAdicionais(umFiltrosSolicitacao(ECanal.D2D_PROPRIO, 1, null)))
            .thenReturn(dadosAdicionaisResponse());

        service.getDadosAdicionais(umFiltrosSolicitacao(ECanal.D2D_PROPRIO, 1, null));

        verify(serviceD2d, times(1))
            .getDadosAdicionais(umFiltrosSolicitacao(ECanal.D2D_PROPRIO, 1, null));
    }

    @Test
    public void update_deveMandarParaServiceAa_seCanalForAgenteAutorizado() {
        var request = criaSolicitacaoRamal(1, 2);
        request.setCanal(ECanal.AGENTE_AUTORIZADO);
        when(serviceAa.update(request)).thenReturn(umaSolicitacaoRamalResponseAa(1));
        when(context.getBean(SolicitacaoRamalServiceAa.class)).thenReturn(serviceAa);

        service.update(request);

        verify(serviceAa, times(1)).update(request);
    }

    @Test
    public void update_deveMandarParaServiceD2d_seCanalForD2d() {
        var request = criaSolicitacaoRamal(1, 2);
        request.setCanal(ECanal.D2D_PROPRIO);
        when(serviceD2d.update(request)).thenReturn(umaSolicitacaoRamalResponseD2d(1));
        when(context.getBean(SolicitacaoRamalServiceD2d.class)).thenReturn(serviceD2d);

        service.update(request);

        verify(serviceD2d, times(1)).update(request);
    }

    @Test
    public void getAllGerenciaDeveMandarParaServiceAa_seCanalforAgenteAutorizado() {
        var filtros = umaSolicitacaoFiltros();
        filtros.setCanal(ECanal.AGENTE_AUTORIZADO);
        when(serviceAa.getAllGerencia(new PageRequest(), filtros)).thenReturn(umaPageResponseAa());
        when(context.getBean(SolicitacaoRamalServiceAa.class)).thenReturn(serviceAa);

        service.getAllGerencia(new PageRequest(), filtros);

        verify(serviceAa, times(1)).getAllGerencia(any(PageRequest.class), eq(filtros));
    }

    @Test
    public void getAllGerenciaDeveMandarParaServiceD2d_seCanalforD2d() {
        var filtros = umaSolicitacaoFiltros();
        filtros.setCanal(ECanal.D2D_PROPRIO);
        when(serviceD2d.getAllGerencia(new PageRequest(), filtros)).thenReturn(umaPageResponseD2d());
        when(context.getBean(SolicitacaoRamalServiceD2d.class)).thenReturn(serviceD2d);

        service.getAllGerencia(new PageRequest(), filtros);

        verify(serviceD2d, times(1)).getAllGerencia(any(PageRequest.class), eq(filtros));
    }

    @Test
    public void remover_deveDeletarSolicitacaoRamal_seSolicitacaoRamalComStatusPendente() {
        when(repository.findById(1)).thenReturn(Optional.of(umaSolicitacaoRamal(1)));

        service.remover(1);

        verify(historicoRepository, times(1)).deleteAll(1);
        verify(repository, times(1)).delete(umaSolicitacaoRamal(1));
    }

    @Test(expected = NotFoundException.class)
    public void remover_deveRetornarNotFoundException_seSolicitacaoRamalNaoExistir() {
        service.remover(1000);
    }

    @Test(expected = ValidacaoException.class)
    public void remover_deveRetornarValidacaoException_seSolicitacaoRamalComStatusDiferenteDePendente() {
        var solicitacaoRamalEmAndamento = umaSolicitacaoRamal(1);
        solicitacaoRamalEmAndamento.setSituacao(ESituacaoSolicitacao.EM_ANDAMENTO);

        when(repository.findById(1)).thenReturn(Optional.of(solicitacaoRamalEmAndamento));

        service.remover(1);

        verify(historicoRepository, never()).deleteAll(1);
        verify(repository, never()).delete(solicitacaoRamalEmAndamento);
    }

    private UsuarioAutenticado umUsuarioGerenteOperacao() {
        return UsuarioAutenticado.builder()
            .id(1)
            .usuario(Usuario.builder().id(1).build())
            .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
            .build();
    }
}
