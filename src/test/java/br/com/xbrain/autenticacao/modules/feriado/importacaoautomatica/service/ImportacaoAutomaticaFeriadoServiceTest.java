//package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service;
//
//import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
//import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
//import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
//import br.com.xbrain.autenticacao.modules.comum.enums.ENivel;
//import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
//import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
//import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
//import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoFiltros;
//import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
//import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
//import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
//import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.repository.ImportacaoAutomaticaFeriadoRepository;
//import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
//import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoAutomacaoClient;
//import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
//import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
//import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
//import feign.RetryableException;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//
//import static br.com.xbrain.autenticacao.modules.feriado.helper.FeriadoHelper.*;
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@RunWith(MockitoJUnitRunner.class)
//public class ImportacaoAutomaticaFeriadoServiceTest {
//
//    @InjectMocks
//    private ImportacaoAutomaticaFeriadoService service;
//    @Mock
//    private FeriadoService feriadoService;
//    @Mock
//    private AutenticacaoService autenticacaoService;
//    @Mock
//    private FeriadoAutomacaoClient feriadoAutomacaoClient;
//    @Mock
//    private CidadeRepository cidadeRepositoy;
//    @Mock
//    private ImportacaoAutomaticaFeriadoRepository importacaoAutomaticaFeriadoRepository;
//    @Mock
//    private UfRepository ufRepository;
//    @Mock
//    private FeriadoRepository feriadoRepository;
//
//    @Test
//    public void importarFeriadosAutomacaoMunicipais_deveImportarFeriados_seSolicitado() {
//        var request = new FeriadoRequest();
//        request.setEstadoId(1);
//        request.setAno(2023);
//        var listaFeriados = umaListFeriadoAutomacao();
//
//        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
//        when(feriadoAutomacaoClient.buscarFeriadosMunicipais(2023, "PR", "londrina"))
//            .thenReturn(listaFeriados);
//        when(cidadeRepositoy.findCidadesByUfId(1)).thenReturn(List.of(umaCidade(1, "londrina"),
//            umaCidade(2, "maringa")));
//        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
//            .thenReturn(new ImportacaoFeriado());
//        when(feriadoService.validarSeFeriadoNaoCadastrado(listaFeriados.get(0), request))
//            .thenReturn(true);
//        when(feriadoService.validarSeFeriadoNaoCadastrado(listaFeriados.get(1), request))
//            .thenReturn(true);
//
//        service.importarFeriadosAutomacaoMunicipais(request, umFeriadoImportacao(1));
//
//        verify(autenticacaoService).getUsuarioAutenticado();
//        verify(feriadoAutomacaoClient).buscarFeriadosMunicipais(2023, "PR", "londrina");
//        verify(cidadeRepositoy).findCidadesByUfId(1);
//        verify(importacaoAutomaticaFeriadoRepository, times(2)).save(any(ImportacaoFeriado.class));
//    }
//
//    @Test
//    public void importarFeriadosAutomacaoMunicipais_deveLancarException_seFeriadosJaCadastrados() {
//        var request = new FeriadoRequest();
//        request.setEstadoId(1);
//        request.setAno(2023);
//        var listaFeriado = umaListFeriadoAutomacao();
//
//        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
//        when(feriadoAutomacaoClient.buscarFeriadosMunicipais(2023, "PR", "londrina"))
//            .thenReturn(listaFeriado);
//        when(cidadeRepositoy.findCidadesByUfId(1)).thenReturn(List.of(umaCidade(1, "londrina"),
//            umaCidade(2, "maringa")));
//        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
//            .thenReturn(new ImportacaoFeriado());
//
//        assertThatExceptionOfType(ValidacaoException.class)
//            .isThrownBy(() -> service.importarFeriadosAutomacaoMunicipais(request, umFeriadoImportacao(1)))
//            .withMessage("Feriados ja cadastrados");
//
//        verify(autenticacaoService).getUsuarioAutenticado();
//        verify(feriadoAutomacaoClient).buscarFeriadosMunicipais(2023, "PR", "londrina");
//        verify(cidadeRepositoy).findCidadesByUfId(1);
//        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
//    }
//
//    @Test
//    public void importarFeriadosAutomacaoMunicipais_deveLancarException_seUsuarioSemPermissaoParaGerenciarFeriados() {
//        var usuario = UsuarioAutenticado.builder()
//            .id(1).nivel(ENivel.XBRAIN.name())
//            .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.CTR_2034.getRole())))
//            .build();
//
//        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuario);
//
//        var request = new FeriadoRequest();
//        request.setEstadoId(1);
//        request.setAno(2023);
//
//        assertThatExceptionOfType(ValidacaoException.class)
//            .isThrownBy(() -> service.importarFeriadosAutomacaoMunicipais(request, umFeriadoImportacao(1)))
//            .withMessage("Usuario sem permissao para gerenciamento de feriados");
//
//        verify(autenticacaoService).getUsuarioAutenticado();
//    }
//
//    @Test
//    public void importarFeriadosAutomacaoMunicipais_deveLancarException_seHouverFalhaNaChamadaDoClient() {
//        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
//        when(cidadeRepositoy.findCidadesByUfId(1)).thenReturn(List.of(umaCidade(1, "londrina"),
//            umaCidade(2, "maringa")));
//        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
//            .thenReturn(new ImportacaoFeriado());
//        when(feriadoAutomacaoClient.buscarFeriadosMunicipais(2023, "PR", "londrina"))
//            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));
//
//        var request = new FeriadoRequest();
//        request.setEstadoId(1);
//        request.setAno(2023);
//
//        assertThatExceptionOfType(IntegracaoException.class)
//            .isThrownBy(() -> service.importarFeriadosAutomacaoMunicipais(request, umFeriadoImportacao(1)))
//            .withMessage("#050 - Desculpe, ocorreu um erro interno. Contate a administrador.");
//
//        verify(autenticacaoService).getUsuarioAutenticado();
//        verify(feriadoAutomacaoClient).buscarFeriadosMunicipais(2023, "PR", "londrina");
//        verify(cidadeRepositoy).findCidadesByUfId(1);
//        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
//    }
//
//    @Test
//    public void importarFeriadosAutomacaoEstaduais_deveImportarFeriados_seSolicitado() {
//        var request = new FeriadoRequest();
//        request.setEstadoId(1);
//        request.setAno(2023);
//        var listaFeriado = umaListFeriadoAutomacao();
//
//        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
//        when(feriadoAutomacaoClient.buscarFeriadosEstaduais(2023, "PR")).thenReturn(listaFeriado);
//        when(ufRepository.findByOrderByNomeAsc()).thenReturn(umaListUf());
//        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
//            .thenReturn(new ImportacaoFeriado());
//        when(feriadoService.validarSeFeriadoNaoCadastrado(listaFeriado.get(0), request))
//            .thenReturn(true);
//        when(feriadoService.validarSeFeriadoNaoCadastrado(listaFeriado.get(1), request))
//            .thenReturn(true);
//
//        service.importarFeriadosAutomacaoEstaduais(request, umFeriadoImportacao(1));
//
//        verify(autenticacaoService).getUsuarioAutenticado();
//        verify(feriadoAutomacaoClient, times(2)).buscarFeriadosEstaduais(2023, "PR");
//        verify(ufRepository).findByOrderByNomeAsc();
//        verify(importacaoAutomaticaFeriadoRepository, times(2)).save(any(ImportacaoFeriado.class));
//    }
//
//    @Test
//    public void importarFeriadosAutomacaoEstaduais_deveLancarException_seFeriadosJaCadastrados() {
//        var request = new FeriadoRequest();
//        request.setEstadoId(1);
//        request.setAno(2023);
//
//        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
//        when(feriadoAutomacaoClient.buscarFeriadosEstaduais(2023, "PR"))
//            .thenReturn(umaListFeriadoAutomacao());
//        when(ufRepository.findByOrderByNomeAsc()).thenReturn(umaListUf());
//        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
//            .thenReturn(new ImportacaoFeriado());
//
//        assertThatExceptionOfType(ValidacaoException.class)
//            .isThrownBy(() -> service.importarFeriadosAutomacaoEstaduais(request, umFeriadoImportacao(1)))
//            .withMessage("Feriados ja cadastrados");
//
//        verify(autenticacaoService).getUsuarioAutenticado();
//        verify(feriadoAutomacaoClient, times(2)).buscarFeriadosEstaduais(2023, "PR");
//        verify(ufRepository).findByOrderByNomeAsc();
//        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
//    }
//
//    @Test
//    public void importarFeriadosAutomacaoEstaduais_deveLancarException_seUsuarioSemPermissaoParaGerenciarFeriados() {
//        var usuario = UsuarioAutenticado.builder()
//            .id(1).nivel(ENivel.XBRAIN.name())
//            .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.CTR_2034.getRole())))
//            .build();
//
//        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuario);
//
//        var request = new FeriadoRequest();
//        request.setEstadoId(1);
//        request.setAno(2023);
//
//        assertThatExceptionOfType(ValidacaoException.class)
//            .isThrownBy(() -> service.importarFeriadosAutomacaoEstaduais(request, umFeriadoImportacao(1)))
//            .withMessage("Usuario sem permissao para gerenciamento de feriados");
//
//        verify(autenticacaoService).getUsuarioAutenticado();
//    }
//
//    @Test
//    public void importarFeriadosAutomacaoEstaduais_deveLancarException_seHouverFalhaNaChamadaDoClient() {
//        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
//        when(ufRepository.findByOrderByNomeAsc()).thenReturn(umaListUf());
//        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
//            .thenReturn(new ImportacaoFeriado());
//        when(feriadoAutomacaoClient.buscarFeriadosEstaduais(2023, "PR"))
//            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));
//
//        var request = new FeriadoRequest();
//        request.setEstadoId(1);
//        request.setAno(2023);
//
//        assertThatExceptionOfType(IntegracaoException.class)
//            .isThrownBy(() -> service.importarFeriadosAutomacaoEstaduais(request, umFeriadoImportacao(1)))
//            .withMessage("#050 - Desculpe, ocorreu um erro interno. Contate a administrador.");
//
//        verify(autenticacaoService).getUsuarioAutenticado();
//        verify(feriadoAutomacaoClient).buscarFeriadosEstaduais(2023, "PR");
//        verify(ufRepository).findByOrderByNomeAsc();
//        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
//    }
//
//    @Test
//    public void importarFeriadosAutomacaoNacionais_deveImportarFeriados_seSolicitado() {
//        var request = new FeriadoRequest();
//        request.setEstadoId(1);
//        request.setAno(2023);
//        var listaFeriados = umaListFeriadoAutomacao();
//
//        when(autenticacaoService.getUsuarioAutenticado())
//            .thenReturn(umUsuarioAutenticado());
//        when(feriadoAutomacaoClient.buscarFeriadosNacionais(2023))
//            .thenReturn(listaFeriados);
//        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
//            .thenReturn(new ImportacaoFeriado());
//        when(feriadoService.validarSeFeriadoNaoCadastrado(listaFeriados.get(0), request))
//            .thenReturn(true);
//        when(feriadoService.validarSeFeriadoNaoCadastrado(listaFeriados.get(1), request))
//            .thenReturn(true);
//
//        service.processarFeriadosNacionais(request, umFeriadoImportacao(1));
//
//        verify(autenticacaoService).getUsuarioAutenticado();
//        verify(feriadoAutomacaoClient).buscarFeriadosNacionais(2023);
//        verify(importacaoAutomaticaFeriadoRepository, times(2)).save(any(ImportacaoFeriado.class));
//    }
//
//    @Test
//    public void importarFeriadosAutomacaoNacionais_deveLancarException_seFeriadosJaCadastrados() {
//        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
//        when(feriadoAutomacaoClient.buscarFeriadosNacionais(2023))
//            .thenReturn(umaListFeriadoAutomacao());
//        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
//            .thenReturn(new ImportacaoFeriado());
//        doThrow(new ValidacaoException("Já existe feriado com os mesmos dados."))
//            .when(feriadoService).validarSeFeriadoNaoCadastrado(any(), any());
//
//        var request = new FeriadoRequest();
//        request.setEstadoId(1);
//        request.setAno(2023);
//
//        assertThatExceptionOfType(ValidacaoException.class)
//            .isThrownBy(() -> service.processarFeriadosNacionais(request, umFeriadoImportacao(1)))
//            .withMessage("Já existe feriado com os mesmos dados.");
//
//        verify(autenticacaoService).getUsuarioAutenticado();
//        verify(feriadoAutomacaoClient).buscarFeriadosNacionais(2023);
//        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
//    }
//
//    @Test
//    public void importarFeriadosAutomacaoNacionais_deveLancarException_seUsuarioSemPermissaoParaGerenciarFeriados() {
//        var usuario = UsuarioAutenticado.builder()
//            .id(1).nivel(ENivel.XBRAIN.name())
//            .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.CTR_2034.getRole())))
//            .build();
//
//        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuario);
//
//        var request = new FeriadoRequest();
//        request.setEstadoId(1);
//        request.setAno(2023);
//
//        assertThatExceptionOfType(ValidacaoException.class)
//            .isThrownBy(() -> service.processarFeriadosNacionais(request, umFeriadoImportacao(1)))
//            .withMessage("Usuario sem permissao para gerenciamento de feriados");
//
//        verify(autenticacaoService).getUsuarioAutenticado();
//    }
//
//    @Test
//    public void importarFeriadosAutomacaoNacionais_deveLancarException_seHouverFalhaNaChamadaDoClient() {
//        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
//        when(importacaoAutomaticaFeriadoRepository.save(any(ImportacaoFeriado.class)))
//            .thenReturn(new ImportacaoFeriado());
//        when(feriadoAutomacaoClient.buscarFeriadosNacionais(2023))
//            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));
//
//        var request = new FeriadoRequest();
//        request.setEstadoId(1);
//        request.setAno(2023);
//
//        assertThatExceptionOfType(IntegracaoException.class)
//            .isThrownBy(() -> service.processarFeriadosNacionais(request, umFeriadoImportacao(1)))
//            .withMessage("#050 - Desculpe, ocorreu um erro interno. Contate a administrador.");
//
//        verify(autenticacaoService).getUsuarioAutenticado();
//        verify(feriadoAutomacaoClient).buscarFeriadosNacionais(2023);
//        verify(importacaoAutomaticaFeriadoRepository).save(any(ImportacaoFeriado.class));
//    }
//
//    @Test
//    public void getAllImportacaoHistorico_deveRetornarPageDeHistoricos_seSolicitado() {
//        var filtros = new FeriadoFiltros();
//        filtros.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO);
//
//        when(importacaoAutomaticaFeriadoRepository.findAll(filtros.toPredicate().build(), new PageRequest()))
//            .thenReturn(umaPageImportacaoHistorico());
//
//        assertThat(service.getAllImportacaoHistorico(new PageRequest(), filtros))
//            .extracting("id", "usuarioCadastroId", "situacaoFeriadoAutomacao")
//            .containsExactlyInAnyOrder(
//                tuple(1, 1, ESituacaoFeriadoAutomacao.IMPORTADO),
//                tuple(2, 1, ESituacaoFeriadoAutomacao.IMPORTADO));
//
//        verify(importacaoAutomaticaFeriadoRepository).findAll(filtros.toPredicate().build(), new PageRequest());
//    }
//
//    @Test
//    public void getAllImportacaoHistorico_deveRetornarPageVazia_seNaoHouverHistoricos() {
//        var filtros = new FeriadoFiltros();
//        filtros.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO);
//
//        when(importacaoAutomaticaFeriadoRepository.findAll(filtros.toPredicate().build(), new PageRequest()))
//            .thenReturn(new PageImpl<>(Collections.emptyList()));
//
//        assertThat(service.getAllImportacaoHistorico(new PageRequest(), filtros)).isEmpty();
//
//        verify(importacaoAutomaticaFeriadoRepository).findAll(filtros.toPredicate().build(), new PageRequest());
//    }
//
//    private static Page<ImportacaoFeriado> umaPageImportacaoHistorico() {
//        return new PageImpl<>(
//            List.of(umFeriadoImportacaoHistorico(1),
//                umFeriadoImportacaoHistorico(2)
//            ));
//    }
//
//    private static ImportacaoFeriado umFeriadoImportacaoHistorico(Integer id) {
//        return ImportacaoFeriado.builder()
//            .id(id)
//            .situacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO)
//            .usuarioCadastroId(1)
//            .build();
//    }
//
//    private static ImportacaoFeriado umFeriadoImportacao(Integer id) {
//        return ImportacaoFeriado.builder()
//            .id(id)
//            .situacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.EM_IMPORTACAO)
//            .build();
//    }
//}
