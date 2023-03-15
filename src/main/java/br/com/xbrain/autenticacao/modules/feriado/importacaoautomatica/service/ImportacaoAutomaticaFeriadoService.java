package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.service.UfService;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoAutomacaoClient;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ImportacaoAutomaticaFeriadoService {

    @Autowired
    private FeriadoAutomacaoClient feriadoAutomacaoClient;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private CidadeService cidadeService;
    @Autowired
    private FeriadoService feriadoService;
    @Autowired
    private UfService ufService;

    public void importarFeriadosAutomacaoNacionais(Integer ano) {
        log.info("Importando feriados Nacionais do ano: " + ano);

        //todo criar feriado importacao com status em importacao


        var usuarioAutenticao = autenticacaoService.getUsuarioAutenticado();
        var feriadosNacionais = consultarFeriadosNacionais(ano);


//        repository.save(br.com.xbrain.autenticacao.modules.feriado.model.FeriadoAutomacao.of(ESituacaoFeriadoAutomacao.EM_IMPORTACAO, usuarioAutenticao));
//
//        if (!feriadosNacionais.isEmpty()) {
//            var feriadoAutomacao
//            feriadosNacionais.forEach(feriadoAutomacao -> feriadoService.salvarFeriadoAutomacao(feriadoAutomacao));
//            log.info("Feriados Nacionais importados.");
//        }
    }

    private List<FeriadoAutomacao> consultarFeriadosNacionais(Integer ano) {
        try {
            return feriadoAutomacaoClient.buscarFeriadosNacionais(ano);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                FeriadoAutomacao.class.getName(),
                EErrors.ERRO_BUSCAR_FERIADOS);
        }
    }
}
