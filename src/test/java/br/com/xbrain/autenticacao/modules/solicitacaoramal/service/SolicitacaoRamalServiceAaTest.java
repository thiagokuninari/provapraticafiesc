package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.EquipeVendasService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ETipoImplantacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.repository.SolicitacaoRamalRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

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
    @Autowired
    private SolicitacaoRamalServiceAa service;

    @MockBean
    private Usuario usuario;

    @MockBean
    private AgenteAutorizadoService agenteAutorizadoService;
    @MockBean
    private AgenteAutorizadoNovoService agenteAutorizadoNovoService;
    @MockBean
    private EquipeVendasService equipeVendasService;

    @Test
    public void save_deveSalvarUmaSolicitacaoRamal_seUsuarioForAgenteAutorizado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado());
        when(agenteAutorizadoNovoService.getAgentesAutorizadosPermitidos(eq(umUsuarioAutenticado()
            .getUsuario()))).thenReturn(Arrays.asList(1, 2));
        when(agenteAutorizadoNovoService.getAaById(eq(7129))).thenReturn(criaAa());
        when(repository.save(any(SolicitacaoRamal.class))).thenReturn(umaSolicitacaoRamal(1));

        service.save(criaSolicitacaoRamal(null, 7129));

        verify(autenticacaoService, times(1)).getUsuarioId();
        verify(agenteAutorizadoNovoService, times(1)).getAaById(eq(7129));
        verify(repository, times(1)).save(any(SolicitacaoRamal.class));
    }

    @Test
    public void save_deveLancarException_seCanalForAgenteAutorizadoEAgenteAutorizadoIdNaoForInformado() {
        var solicitacaoRamal = criaSolicitacaoRamal(null, 200);
        solicitacaoRamal.setCanal(ECanal.AGENTE_AUTORIZADO);
        solicitacaoRamal.setAgenteAutorizadoId(null);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado());

        assertThatExceptionOfType(ValidacaoException.class).isThrownBy(() -> service.save(solicitacaoRamal))
            .withMessage("agenteAutorizadoId obrigatório para o cargo agente autorizado");

        verify(repository, never()).save(any(SolicitacaoRamal.class));
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        return UsuarioAutenticado.builder()
            .id(1)
            .usuario(Usuario.builder().id(1).build())
            .cargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_SOCIO)
            .build();
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

}
