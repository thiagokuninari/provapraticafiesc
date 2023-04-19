package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.service.UfService;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacaoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacaoMunicipais;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FeriadoAutomacaoService {

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

    public void importarFeriadosAutomacaoEstaduais(Integer ano) {
        log.info("Importando feriados Estaduais do ano: " + ano);

        //todo criar feriado importacao com status em importacao

        var feriadosEstaduais = feriadoAutomacaoClient.buscarFeriadosEstaduais(ano);

//        var feriadosEst = List.of(
//            FeriadoAutomacaoEstadual.builder()
//                .sigla("AC")
//                .feriados(List.of(
//                    FeriadoAutomacao.builder()
//                        .dataFeriado("20/01/2023")
//                        .nome("Dia do Católico")
//                        .tipoFeriado(ETipoFeriado.ESTADUAL)
//                        .build(),
//                    FeriadoAutomacao.builder()
//                        .dataFeriado("27/01/2023")
//                        .nome("Dia do Evangélico")
//                        .tipoFeriado(ETipoFeriado.ESTADUAL)
//                        .build()))
//                .build());

        feriadosEstaduais.forEach(sigla -> {
            var uf = ufService.findUfByUf(sigla.getSigla());
            sigla.getFeriados().forEach(feriado -> {
                feriado.setUf(Uf.builder().id(uf.getId()).build());
                log.info("Uf: " + uf.getNome());
                feriadoService.salvarFeriadoAutomacao(feriado);
            });
        });


        log.info("Feriados ESTADUAIS importados.");
    }

    public List<FeriadoAutomacao> consultarFeriadoAutomacao(FeriadoAutomacaoFiltros filtros) {
        return feriadoAutomacaoClient.consultarFeriadosMunicipais(
            filtros.getAno(), filtros.getEstado(), filtros.getCidade());
    }

    public void importarFeriadosAutomacaoMunicipais(FeriadoAutomacaoFiltros filtros) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        var cidades = new ArrayList<Cidade>();
        cidades.add(Cidade.builder().id(1).nome("Londrina").build());
        cidades.add(Cidade.builder().id(2).nome("Cambe").build());

        var feriadosMunicipais = new ArrayList<FeriadoAutomacaoMunicipais>();

        cidades.forEach(cidade -> {
                filtros.setCidade(cidade.getNome());
                var listaFeriadosAutomacao = consultarFeriadoAutomacao(filtros);

                var feriadoAutomacaoMunicipal = new FeriadoAutomacaoMunicipais();
                feriadoAutomacaoMunicipal.setCidadeId(cidade.getId());
                feriadoAutomacaoMunicipal.setCidadeNome(cidade.getNome());
                feriadoAutomacaoMunicipal.setFeriadosMunicipais(listaFeriadosAutomacao.stream().filter(
                    f -> !f.getNome().equals("Sexta Feira Santa")).collect(Collectors.toList()));
                feriadosMunicipais.add(feriadoAutomacaoMunicipal);
            });

        var res = feriadosMunicipais;
    }
}
