package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.dto.TelefoniaResponse;
import br.com.xbrain.autenticacao.modules.call.service.CallClient;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.SocioResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.SocioClient;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ETipoImplantacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
public class SolicitacaoRamalServiceAaTest {

    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private SolicitacaoRamalRepository repository;
    @MockBean
    private CallClient client;
    @MockBean
    private SocioClient socioClient;
    @MockBean
    private UsuarioService usuarioService;
    @Autowired
    private SolicitacaoRamalServiceAa service;
    @MockBean
    private AgenteAutorizadoService agenteAutorizadoService;

    @Test
    public void save_deveSalvarUmaSolicitacaoRamal_seUsuarioForAgenteAutorizado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado());
        when(agenteAutorizadoService.getAgentesAutorizadosPermitidos(eq(umUsuarioAutenticado()
            .getUsuario()))).thenReturn(Arrays.asList(1, 2));
        when(agenteAutorizadoService.getAaById(eq(7129))).thenReturn(criaAa());
        when(repository.save(any(SolicitacaoRamal.class))).thenReturn(umaSolicitacaoRamal(1));

        service.save(criaSolicitacaoRamal(null, 7129));

        verify(autenticacaoService, times(1)).getUsuarioId();
        verify(agenteAutorizadoService, times(1)).getAaById(eq(7129));
        verify(repository, times(1)).save(any(SolicitacaoRamal.class));
    }

    @Test
    public void save_deveLancarException_seCanalForAgenteAutorizadoEAgenteAutorizadoIdNaoForInformado() {
        var solicitacaoRamal = criaSolicitacaoRamal(null, null);
        solicitacaoRamal.setCanal(ECanal.AGENTE_AUTORIZADO);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado());

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() -> service.save(solicitacaoRamal))
            .withMessage("agenteAutorizadoId obrigatório para o cargo agente autorizado");

        verify(repository, never()).save(any(SolicitacaoRamal.class));
    }

    @Test
    public void save_deveLancarException_seUsuarioAutenticadoNaoTiverPermissaoCTR_20014() {
        var solicitacaoRamal = criaSolicitacaoRamal(null, null);
        solicitacaoRamal.setCanal(ECanal.AGENTE_AUTORIZADO);
        solicitacaoRamal.setAgenteAutorizadoId(1);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticado.builder().id(1)
                .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.CTR_20015.getRole()))).build());

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() -> service.save(solicitacaoRamal))
            .withMessage("Sem autorização para fazer uma solicitação para este canal.");

    }

    @Test
    public void update_deveAtualizarSolicitacaoAaSeTodosOsDadosPreenchidosCorretamente() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado());
        when(repository.findById(1))
            .thenReturn(Optional.of(umaSolicitacaoRamal(1)));
        when(agenteAutorizadoService.getAgentesAutorizadosPermitidos(eq(umUsuarioAutenticado()
            .getUsuario()))).thenReturn(Arrays.asList(1, 2));
        when(agenteAutorizadoService.getAaById(eq(7129))).thenReturn(criaAa());
        when(repository.save(any(SolicitacaoRamal.class)))
            .thenReturn(umaSolicitacaoRamal(1));

        assertThat(service.update(criaSolicitacaoRamal(1, 7129)))
            .extracting("id")
            .contains(1);

        verify(repository, times(1)).save(any(SolicitacaoRamal.class));
        verify(repository, atLeastOnce()).findById(eq(1));
    }

    @Test
    public void getDadosAdicionais_deveChamarClientPeloAgenteAutorizadoId() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado());
        when(agenteAutorizadoService.getAaById(1)).thenReturn(umAgenteAutorizado());
        when(client.obterNomeTelefoniaPorId(1)).thenReturn(umaTelefonia());
        when(agenteAutorizadoService.getUsuariosAaAtivoComVendedoresD2D(1))
            .thenReturn(List.of());
        when(socioClient.findSocioPrincipalByAaId(1)).thenReturn(umSocioPrincipal());
        when(client.obterRamaisParaCanal(ECanal.D2D_PROPRIO, 1)).thenReturn(List.of());

        service.getDadosAdicionais(umFiltrosSolicitacao(ECanal.AGENTE_AUTORIZADO, null, 1));

        verify(agenteAutorizadoService, times(1)).getAaById(eq(1));
        verify(client, times(1)).obterNomeTelefoniaPorId(eq(1));
        verify(client, times(1)).obterRamaisParaCanal(ECanal.AGENTE_AUTORIZADO, 1);
        verify(socioClient, times(1)).findSocioPrincipalByAaId(eq(1));
        verify(agenteAutorizadoService, times(1))
            .getUsuariosAaAtivoComVendedoresD2D(eq(1));
    }

    @Test
    public void getDadosAdicionais_deveLancarException_quandoOcorrerAlgumErro() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado());
        when(agenteAutorizadoService.getAaById(1)).thenReturn(umAgenteAutorizado());
        when(client.obterNomeTelefoniaPorId(1)).thenThrow(RetryableException.class);
        when(client.obterRamaisParaCanal(ECanal.AGENTE_AUTORIZADO, 1)).thenReturn(List.of());

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.getDadosAdicionais(
                umFiltrosSolicitacao(ECanal.AGENTE_AUTORIZADO, null, 1)))
            .withMessage("#008 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(agenteAutorizadoService, times(1)).getAaById(eq(1));
        verify(client, times(1)).obterNomeTelefoniaPorId(eq(1));
    }

    @Test
    public void verificaPermissaoSobreOAgenteAutorizado_deveRetornarTrue_seOUsuarioTemPermissaoSobreOAa() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(usuarioService.findComplete(1)).thenReturn(umUsuario());
        when(autenticacaoService.getUsuarioId()).thenReturn(1);
        when(agenteAutorizadoService.getAgentesAutorizadosPermitidos(umUsuario()))
            .thenReturn(List.of(1, 2));

        service.verificaPermissaoSobreOAgenteAutorizado(1);

        verify(autenticacaoService, times(1)).getUsuarioAutenticado();
        verify(usuarioService, times(1)).findComplete(1);
        verify(autenticacaoService, times(1)).getUsuarioId();
        verify(agenteAutorizadoService, times(1)).getAgentesAutorizadosPermitidos(umUsuario());
    }

    @Test
    public void getAllGerencia_deveListarSolicitacoes_seTodosOsParametrosPreenchidos() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(repository.findAllGerenciaAa(any(PageRequest.class), any(Predicate.class))).thenReturn((umaPageSolicitacaoRamal()));
        when(usuarioService.findComplete(1)).thenReturn(Usuario.builder().id(1).nome("teste").build());

        var filtros = new SolicitacaoRamalFiltros();
        filtros.setAgenteAutorizadoId(1);
        filtros.setSituacao(ESituacaoSolicitacao.PENDENTE);
        filtros.setCanal(ECanal.AGENTE_AUTORIZADO);

        var response = service.getAllGerencia(new PageRequest(), filtros);

        assertThat(response)
            .extracting("id", "canal", "dataCadastro",
                "situacao").containsExactly(

                Tuple.tuple(1, ECanal.AGENTE_AUTORIZADO,
                    LocalDateTime.of(2022, 02, 10, 10, 00, 00),
                    ESituacaoSolicitacao.PENDENTE),

                Tuple.tuple(2, ECanal.AGENTE_AUTORIZADO,
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
            .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.CTR_20014.getRole())))
            .build();
    }

    private Usuario umUsuario() {
        return Usuario.builder()
            .id(1)
            .nome("teste")
            .cpf("123456789")
            .build();
    }

    private TelefoniaResponse umaTelefonia() {
        var telefoniaResponse = new TelefoniaResponse();
        telefoniaResponse.setId(1);
        telefoniaResponse.setNome("teste");
        return telefoniaResponse;
    }

    private SocioResponse umSocioPrincipal() {
        var socioResponse = new SocioResponse();
        socioResponse.setId(1);
        socioResponse.setNome("teste");
        socioResponse.setCpf("12345678900");
        return socioResponse;
    }

    private AgenteAutorizadoResponse umAgenteAutorizado() {
        return AgenteAutorizadoResponse.builder()
            .id("1234")
            .razaoSocial("solteiro")
            .nomeFantasia("teste")
            .cnpj("123456789")
            .nacional(Eboolean.V)
            .discadoraId(1)
            .build();
    }

    private SolicitacaoRamalRequest criaSolicitacaoRamal(Integer id, Integer aaId) {
        return SolicitacaoRamalRequest.builder()
            .id(id)
            .quantidadeRamais(38)
            .canal(ECanal.AGENTE_AUTORIZADO)
            .agenteAutorizadoId(aaId)
            .melhorHorarioImplantacao(LocalTime.of(10, 00))
            .melhorDataImplantacao(LocalDate.of(2019, 01, 25))
            .tipoImplantacao(ETipoImplantacao.ESCRITORIO.getCodigo())
            .emailTi("reanto@ti.com.br")
            .telefoneTi("(18) 3322-2388")
            .usuariosSolicitadosIds(Arrays.asList(100, 101))
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

    private SolicitacaoRamal umaSolicitacaoRamal(Integer id) {
        var solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.setId(id);
        solicitacaoRamal.setCanal(ECanal.AGENTE_AUTORIZADO);
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
            List.of(umaSolicitacaoRamal(1),
                umaSolicitacaoRamal(2))
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
