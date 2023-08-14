package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.dto.RamalResponse;
import br.com.xbrain.autenticacao.modules.call.dto.TelefoniaResponse;
import br.com.xbrain.autenticacao.modules.call.service.CallClient;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalDadosAdicionaisResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ETipoImplantacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.SubCanalService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.querydsl.core.types.Predicate;
import feign.RetryableException;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
public class SolicitacaoRamalServiceD2dTest {

    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private SolicitacaoRamalRepository repository;
    @MockBean
    private UsuarioService usuarioService;
    @Autowired
    private SolicitacaoRamalServiceD2d service;
    @MockBean
    private SubCanalService subCanalService;
    @MockBean
    private CallClient client;

    @Test
    public void save_deveSalvarUmaSolicitacaoRamal_seUsuarioNivelOperacao() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoOperacao());
        when(repository.save(any(SolicitacaoRamal.class)))
            .thenReturn(umaSolicitacaoRamalCanalD2d(1));

        service.save(criaSolicitacaoRamal(null));

        verify(autenticacaoService, times(1)).getUsuarioId();
        verify(repository, times(1)).save(any(SolicitacaoRamal.class));
    }

    @Test
    public void save_deveLancarException_seCanalD2dESubCanalNaoSelecionado() {
        var solicitacaoRamal = criaSolicitacaoRamal(null);
        solicitacaoRamal.setCanal(ECanal.D2D_PROPRIO);
        solicitacaoRamal.setSubCanalId(null);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoOperacao());

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() -> service.save(solicitacaoRamal))
            .withMessage("Tipo de canal obrigatório para o canal D2D");

        verify(repository, never()).save(any(SolicitacaoRamal.class));
    }

    @Test
    public void save_deveLancarException_seJaHouverSolicitacaoPorEquipeId() {
        var solicitacaoRamal = criaSolicitacaoRamal(1);
        solicitacaoRamal.setEquipeId(1);

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoOperacao());
        when(repository.findAllByPredicate(any())).thenReturn(List.of(umaSolicitacaoRamalCanalD2d(1)));

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() -> service.save(solicitacaoRamal))
            .withMessage("Não é possível salvar a solicitação de ramal, pois já existe uma pendente ou em andamento.");

        verify(repository, never()).save(any(SolicitacaoRamal.class));
    }

    @Test
    public void save_deveLancarException_seUsuarioAutenticadoNaoTiverPermissaoCTR_20015() {
        var solicitacaoRamal = criaSolicitacaoRamal(null);
        solicitacaoRamal.setCanal(ECanal.D2D_PROPRIO);
        solicitacaoRamal.setSubCanalId(1);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder().id(1)
                .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.CTR_20014.getRole()))).build());

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() -> service.save(solicitacaoRamal))
            .withMessage("Sem autorização para fazer uma solicitação para este canal.");

    }

    @Test
    public void update_deveAtualizarSolicitacaoD2dSeTodosOsDadosPreenchidosCorretamente() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoOperacao());
        when(repository.findById(1))
            .thenReturn(Optional.of(umaSolicitacaoRamalCanalD2d(1)));
        when(repository.save(any(SolicitacaoRamal.class)))
            .thenReturn(umaSolicitacaoRamalCanalD2d(1));

        assertThat(service.update(criaSolicitacaoRamal(1)))
            .extracting("id")
            .contains(1);

        verify(repository, times(1)).save(any(SolicitacaoRamal.class));
        verify(repository, atLeastOnce()).findById(eq(1));
        verify(autenticacaoService, times(1)).getUsuarioId();
    }

    @Test
    public void getDadosAdicionais_deveChamarClientPeloSubCanalId() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoOperacao());
        when(subCanalService.getSubCanalById(1)).thenReturn(umSubCanal(1));
        when(client.obterNomeTelefoniaPorId(1)).thenReturn(umaTelefonia());
        when(client.obterRamaisParaCanal(ECanal.D2D_PROPRIO, 1)).thenReturn(List.of());

        service.getDadosAdicionais(umFiltrosSolicitacao(ECanal.D2D_PROPRIO, 1, null));

        verify(subCanalService, times(1)).getSubCanalById(eq(1));
        verify(client, times(1)).obterNomeTelefoniaPorId(eq(1));
        verify(client, times(1)).obterRamaisParaCanal(ECanal.D2D_PROPRIO, 1);
    }

    @Test
    public void getDadosAdicionais_deveLancarException_quandoOcorrerAlgumErro() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoOperacao());
        when(subCanalService.getSubCanalById(1)).thenReturn(umSubCanal(1));
        when(client.obterNomeTelefoniaPorId(1)).thenThrow(RetryableException.class);
        when(client.obterRamaisParaCanal(ECanal.D2D_PROPRIO, 1)).thenReturn(List.of());

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service
                .getDadosAdicionais(umFiltrosSolicitacao(ECanal.D2D_PROPRIO, 1, null)))
            .withMessage("#008 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(subCanalService, times(1)).getSubCanalById(eq(1));
        verify(client, times(1)).obterNomeTelefoniaPorId(eq(1));
    }

    @Test
    public void getAllGerencia_deveListarSolicitacoes_seTodosOsParametrosPreenchidos() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(repository.findAllGerenciaD2d(any(PageRequest.class), any(Predicate.class))).thenReturn((umaPageSolicitacaoRamal()));
        when(usuarioService.findComplete(1)).thenReturn(Usuario.builder().id(1).nome("teste").build());

        var filtros = new SolicitacaoRamalFiltros();
        filtros.setAgenteAutorizadoId(1);
        filtros.setSituacao(ESituacaoSolicitacao.PENDENTE);
        filtros.setCanal(ECanal.D2D_PROPRIO);

        var response = service.getAllGerencia(new PageRequest(), filtros);

        assertThat(response)
            .extracting("id", "canal", "dataCadastro",
                "situacao").containsExactly(

                Tuple.tuple(1, ECanal.D2D_PROPRIO,
                    LocalDateTime.of(2022, 02, 10, 10, 00, 00),
                    ESituacaoSolicitacao.PENDENTE),

                Tuple.tuple(2, ECanal.D2D_PROPRIO,
                    LocalDateTime.of(2022, 02, 10, 10, 00, 00),
                    ESituacaoSolicitacao.PENDENTE)
            );
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        return UsuarioAutenticado.builder()
            .id(1)
            .nome("teste")
            .usuario(Usuario.builder().id(1).build())
            .cargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_SOCIO)
            .build();
    }

    private SolicitacaoRamalRequest criaSolicitacaoRamal(Integer id) {
        return SolicitacaoRamalRequest.builder()
            .id(id)
            .quantidadeRamais(38)
            .canal(ECanal.D2D_PROPRIO)
            .subCanalId(3)
            .melhorHorarioImplantacao(LocalTime.of(10, 00))
            .melhorDataImplantacao(LocalDate.of(2019, 01, 25))
            .tipoImplantacao(ETipoImplantacao.ESCRITORIO.getCodigo())
            .emailTi("reanto@ti.com.br")
            .telefoneTi("(18) 3322-2388")
            .usuariosSolicitadosIds(Arrays.asList(100, 101))
            .build();
    }

    private SubCanalDto umSubCanal(Integer id) {
        return SubCanalDto.builder()
            .id(id)
            .nome("teste")
            .situacao(ESituacao.A)
            .codigo(ETipoCanal.PAP)
            .build();
    }

    private SolicitacaoRamalDadosAdicionaisResponse umDadosAdicionaisResponse() {
        var adicionalResponse = SolicitacaoRamalDadosAdicionaisResponse
            .convertFrom("nome discadora", 2);
        adicionalResponse.setDiscadora("discadora teste");
        adicionalResponse.setQuantidadeRamais(2);
        return adicionalResponse;
    }

    private TelefoniaResponse umaTelefonia() {
        var telefoniaResponse = new TelefoniaResponse();
        telefoniaResponse.setId(1);
        telefoniaResponse.setNome("teste");
        return telefoniaResponse;
    }

    private RamalResponse umRamal() {
        var ramalResponse = new RamalResponse();
        ramalResponse.setId(1);
        ramalResponse.setRamal("2000");
        return ramalResponse;
    }

    private UsuarioAutenticado umUsuarioAutenticadoOperacao() {
        return UsuarioAutenticado.builder()
            .id(1)
            .usuario(Usuario.builder().id(1).build())
            .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
            .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.CTR_20015.getRole())))
            .build();
    }

    private SolicitacaoRamal umaSolicitacaoRamalCanalD2d(Integer id) {
        var solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.setId(id);
        solicitacaoRamal.setCanal(ECanal.D2D_PROPRIO);
        solicitacaoRamal.setSubCanal(SubCanal.builder().id(1).build());
        solicitacaoRamal.setDataCadastro(LocalDateTime.of(2022, 02, 10, 10, 00, 00));
        solicitacaoRamal.setMelhorDataImplantacao(LocalDate.of(2022, 12, 01));
        solicitacaoRamal.setUsuariosSolicitados(List.of(Usuario.builder().id(1).build()));
        solicitacaoRamal.setSituacao(ESituacaoSolicitacao.PENDENTE);
        solicitacaoRamal.setUsuario(Usuario.builder().id(1).nome("teste").build());
        solicitacaoRamal.setTipoImplantacao(ETipoImplantacao.ESCRITORIO);

        return solicitacaoRamal;
    }

    private Page<SolicitacaoRamal> umaPageSolicitacaoRamal() {
        return new PageImpl<>(
            List.of(umaSolicitacaoRamalCanalD2d(1),
                umaSolicitacaoRamalCanalD2d(2))
        );
    }

    private SolicitacaoRamalFiltros umFiltrosSolicitacao(ECanal canal, Integer subCanalId, Integer aaId) {
        var filtro = new SolicitacaoRamalFiltros();
        filtro.setCanal(canal);
        filtro.setSubCanalId(subCanalId);
        filtro.setAgenteAutorizadoId(aaId);
        return filtro;
    }
}
