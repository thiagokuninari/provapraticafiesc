package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ENivel;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.service.UfService;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.repository.ImportacaoAutomaticaFeriadoRepository;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoAutomacaoClient;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.feriado.helper.FeriadoHelper.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImportacaoAutomaticaFeriadoServiceTest {

    @InjectMocks
    private ImportacaoAutomaticaFeriadoService service;
    @Mock
    private FeriadoService feriadoService;
    @Mock
    private FeriadoRepository feriadoRepository;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private FeriadoAutomacaoClient feriadoAutomacaoClient;
    @Mock
    private CidadeService cidadeService;
    @Mock
    private ImportacaoAutomaticaFeriadoRepository importacaoAutomaticaFeriadoRepository;
    @Mock
    private UfService ufService;

    @Test
    public void importarFeriadosAutomacaoMunicipais_deveImportarFeriados_seSolicitado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(feriadoAutomacaoClient.buscarFeriadosMunicipais(2023, "PR", "londrina"))
            .thenReturn(umaListFeriadoAutomacao());
        when(cidadeService.getAllCidadeByUf(1)).thenReturn(List.of(umaCidade(1, "londrina"),
            umaCidade(2, "maringa")));
        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
            .thenReturn(new ImportacaoFeriado());

        var request = new FeriadoRequest();
        request.setEstadoId(1);
        request.setAno(2023);

        service.importarFeriadosAutomacaoMunicipais(request);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(feriadoAutomacaoClient).buscarFeriadosMunicipais(2023, "PR", "londrina");
        verify(cidadeService).getAllCidadeByUf(1);
        verify(importacaoAutomaticaFeriadoRepository, times(2)).save(any(ImportacaoFeriado.class));
    }

    @Test
    public void importarFeriadosAutomacaoMunicipais_deveLancarException_seFeriadoJaCadastrado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(feriadoAutomacaoClient.buscarFeriadosMunicipais(2023, "PR", "londrina"))
            .thenReturn(umaListFeriadoAutomacao());
        when(cidadeService.getAllCidadeByUf(1)).thenReturn(List.of(umaCidade(1, "londrina"),
            umaCidade(2, "maringa")));
        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
            .thenReturn(new ImportacaoFeriado());
        doThrow(new ValidacaoException("Já existe feriado com os mesmos dados."))
            .when(feriadoService).validarSeFeriadoAutomacaoJaCadastado(any(), any());

        var request = new FeriadoRequest();
        request.setEstadoId(1);
        request.setAno(2023);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.importarFeriadosAutomacaoMunicipais(request))
            .withMessage("Já existe feriado com os mesmos dados.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(feriadoAutomacaoClient).buscarFeriadosMunicipais(2023, "PR", "londrina");
        verify(cidadeService).getAllCidadeByUf(1);
        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
    }

    @Test
    public void importarFeriadosAutomacaoMunicipais_deveLancarException_seUsuarioSemPermissaoParaGerenciarFeriados() {
        var usuario = UsuarioAutenticado.builder()
            .id(1).nivel(ENivel.XBRAIN.name())
            .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.CTR_2034.getRole())))
            .build();

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuario);

        var request = new FeriadoRequest();
        request.setEstadoId(1);
        request.setAno(2023);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.importarFeriadosAutomacaoMunicipais(request))
            .withMessage("Usuario sem permissao para gerenciamento de feriados");

        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void importarFeriadosAutomacaoMunicipais_deveLancarException_seHouverFalhaNaChamadaDoClient() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(cidadeService.getAllCidadeByUf(1)).thenReturn(List.of(umaCidade(1, "londrina"),
            umaCidade(2, "maringa")));
        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
            .thenReturn(new ImportacaoFeriado());
        when(feriadoAutomacaoClient.buscarFeriadosMunicipais(2023, "PR", "londrina"))
            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));

        var request = new FeriadoRequest();
        request.setEstadoId(1);
        request.setAno(2023);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.importarFeriadosAutomacaoMunicipais(request))
            .withMessage("#050 - Desculpe, ocorreu um erro interno. Contate a administrador.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(feriadoAutomacaoClient).buscarFeriadosMunicipais(2023, "PR", "londrina");
        verify(cidadeService).getAllCidadeByUf(1);
        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
    }

    @Test
    public void importarFeriadosAutomacaoEstaduais_deveImportarFeriados_seSolicitado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(feriadoAutomacaoClient.buscarFeriadosEstaduais(2023, "PR"))
            .thenReturn(umaListFeriadoAutomacao());
        when(ufService.findById(1)).thenReturn(umUf());
        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
            .thenReturn(new ImportacaoFeriado());

        var request = new FeriadoRequest();
        request.setEstadoId(1);
        request.setAno(2023);

        service.importarFeriadosAutomacaoEstaduais(request);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(feriadoAutomacaoClient).buscarFeriadosEstaduais(2023, "PR");
        verify(ufService).findById(1);
        verify(importacaoAutomaticaFeriadoRepository, times(2)).save(any(ImportacaoFeriado.class));
    }

    @Test
    public void importarFeriadosAutomacaoEstaduais_deveLancarException_seFeriadoJaCadastrado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(feriadoAutomacaoClient.buscarFeriadosEstaduais(2023, "PR"))
            .thenReturn(umaListFeriadoAutomacao());
        when(ufService.findById(1)).thenReturn(umUf());
        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
            .thenReturn(new ImportacaoFeriado());
        doThrow(new ValidacaoException("Já existe feriado com os mesmos dados."))
            .when(feriadoService).validarSeFeriadoAutomacaoJaCadastado(any(), any());

        var request = new FeriadoRequest();
        request.setEstadoId(1);
        request.setAno(2023);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.importarFeriadosAutomacaoEstaduais(request))
            .withMessage("Já existe feriado com os mesmos dados.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(feriadoAutomacaoClient).buscarFeriadosEstaduais(2023, "PR");
        verify(ufService).findById(1);
        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
    }

    @Test
    public void importarFeriadosAutomacaoEstaduais_deveLancarException_seUsuarioSemPermissaoParaGerenciarFeriados() {
        var usuario = UsuarioAutenticado.builder()
            .id(1).nivel(ENivel.XBRAIN.name())
            .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.CTR_2034.getRole())))
            .build();

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuario);

        var request = new FeriadoRequest();
        request.setEstadoId(1);
        request.setAno(2023);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.importarFeriadosAutomacaoEstaduais(request))
            .withMessage("Usuario sem permissao para gerenciamento de feriados");

        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void importarFeriadosAutomacaoEstaduais_deveLancarException_seNaoHouverFeriadosEstaduaisParaCadastrar() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(feriadoAutomacaoClient.buscarFeriadosEstaduais(2023, "PR"))
            .thenReturn(List.of());
        when(ufService.findById(1)).thenReturn(umUf());
        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
            .thenReturn(new ImportacaoFeriado());

        var request = new FeriadoRequest();
        request.setEstadoId(1);
        request.setAno(2023);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.importarFeriadosAutomacaoEstaduais(request))
            .withMessage("Não ha feriados para importar");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(feriadoAutomacaoClient).buscarFeriadosEstaduais(2023, "PR");
        verify(ufService).findById(1);
        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
    }

    @Test
    public void importarFeriadosAutomacaoEstaduais_deveLancarException_seHouverFalhaNaChamadaDoClient() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(ufService.findById(1)).thenReturn(umUf());
        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
            .thenReturn(new ImportacaoFeriado());
        when(feriadoAutomacaoClient.buscarFeriadosEstaduais(2023, "PR"))
            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));

        var request = new FeriadoRequest();
        request.setEstadoId(1);
        request.setAno(2023);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.importarFeriadosAutomacaoEstaduais(request))
            .withMessage("#050 - Desculpe, ocorreu um erro interno. Contate a administrador.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(feriadoAutomacaoClient).buscarFeriadosEstaduais(2023, "PR");
        verify(ufService).findById(1);
        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
    }

    @Test
    public void importarFeriadosAutomacaoNacionais_deveImportarFeriados_seSolicitado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(feriadoAutomacaoClient.buscarFeriadosNacionais(2023))
            .thenReturn(umaListFeriadoAutomacao());
        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
            .thenReturn(new ImportacaoFeriado());

        var request = new FeriadoRequest();
        request.setEstadoId(1);
        request.setAno(2023);

        service.importarFeriadosAutomacaoNacionais(request);

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(feriadoAutomacaoClient).buscarFeriadosNacionais(2023);
        verify(importacaoAutomaticaFeriadoRepository, times(2)).save(any(ImportacaoFeriado.class));
    }

    @Test
    public void importarFeriadosAutomacaoNacionais_deveLancarException_seFeriadoJaCadastrado() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(feriadoAutomacaoClient.buscarFeriadosNacionais(2023))
            .thenReturn(umaListFeriadoAutomacao());
        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
            .thenReturn(new ImportacaoFeriado());
        doThrow(new ValidacaoException("Já existe feriado com os mesmos dados."))
            .when(feriadoService).validarSeFeriadoAutomacaoJaCadastado(any(), any());

        var request = new FeriadoRequest();
        request.setEstadoId(1);
        request.setAno(2023);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.importarFeriadosAutomacaoNacionais(request))
            .withMessage("Já existe feriado com os mesmos dados.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(feriadoAutomacaoClient).buscarFeriadosNacionais(2023);
        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
    }

    @Test
    public void importarFeriadosAutomacaoNacionais_deveLancarException_seUsuarioSemPermissaoParaGerenciarFeriados() {
        var usuario = UsuarioAutenticado.builder()
            .id(1).nivel(ENivel.XBRAIN.name())
            .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.CTR_2034.getRole())))
            .build();

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuario);

        var request = new FeriadoRequest();
        request.setEstadoId(1);
        request.setAno(2023);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.importarFeriadosAutomacaoNacionais(request))
            .withMessage("Usuario sem permissao para gerenciamento de feriados");

        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void importarFeriadosAutomacaoNacionais_deveLancarException_seHouverFalhaNaChamadaDoClient() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
            .thenReturn(new ImportacaoFeriado());
        when(feriadoAutomacaoClient.buscarFeriadosNacionais(2023))
            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));

        var request = new FeriadoRequest();
        request.setEstadoId(1);
        request.setAno(2023);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.importarFeriadosAutomacaoNacionais(request))
            .withMessage("#050 - Desculpe, ocorreu um erro interno. Contate a administrador.");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(feriadoAutomacaoClient).buscarFeriadosNacionais(2023);
        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
    }
}
