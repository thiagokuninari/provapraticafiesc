package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacaoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacaoMunicipais;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
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

    public void importarFeriadosAutomacaoNacionais(Integer ano) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        var feriadosNacionais = feriadoAutomacaoClient.consultarFeriadosNacionais(ano);


    }
    public List<FeriadoAutomacao> consultarFeriadoAutomacao(FeriadoAutomacaoFiltros filtros) {
        return feriadoAutomacaoClient.consultarFeriadosMunicipais(
            filtros.getAno(), filtros.getEstado(), filtros.getCidade());
    }

    public void importarFeriadosAutomacaoMunicipais(FeriadoAutomacaoFiltros filtros) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        //var cidades = cidadeService.getAllCidadeByUf(1);

        var cidades = new ArrayList<Cidade>();
        cidades.add(Cidade.builder().id(1).nome("Londrina").build());
        cidades.add(Cidade.builder().id(2).nome("Cambe").build());

        var feriadosMunicipais = new ArrayList<FeriadoAutomacaoMunicipais>();

        cidades.forEach(cidade -> {
                filtros.setCidade(cidade.getNome());
                var a = consultarFeriadoAutomacao(filtros);

                var b = new FeriadoAutomacaoMunicipais();
                b.setCidadeId(cidade.getId());
                b.setCidadeNome(cidade.getNome());
                b.setFeriadosMunicipais(a.stream().filter(
                    f -> !f.getNome().equals("Sexta Feira Santa")).collect(Collectors.toList()));
                feriadosMunicipais.add(b);
            });

        var res = feriadosMunicipais;
        //var feriadosAutomacao = consultarFeriadoAutomacao(filtros);

//        var feriados = feriadosAutomacao.stream()
//            .map(feriado -> Feriado.ofAutomacao(feriado, null, usuarioAutenticado))
//            .collect(Collectors.toList());

       // return feriados.stream().map(FeriadoResponse::of).collect(Collectors.toList());
    }
}
