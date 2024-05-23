package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.dto.ImportacaoFeriadoHistoricoResponse;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.repository.ImportacaoAutomaticaFeriadoRepository;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.feriado.predicate.FeriadoPredicate;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado.*;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportacaoAutomaticaFeriadoService {

    private final FeriadoAutomacaoService feriadoAutomacaoService;
    private final AutenticacaoService autenticacaoService;
    private final FeriadoService feriadoService;
    private final FeriadoRepository feriadoRepository;
    private final ImportacaoAutomaticaFeriadoRepository importacaoAutomaticaRepository;
    private final UfRepository ufRepository;
    private final CidadeRepository cidadeRepository;

    @Value("${app-config.upload-async}")
    private boolean uploadAsync;

    public Page<ImportacaoFeriadoHistoricoResponse> getAllImportacaoHistorico(PageRequest pageRequest, FeriadoFiltros filtros) {
        var predicate = new FeriadoPredicate().comSituacaoFeriadoAutomacao(filtros.getSituacaoFeriadoAutomacao());
        return importacaoAutomaticaRepository.findAll(predicate.build(), pageRequest)
            .map(ImportacaoFeriadoHistoricoResponse::of);
    }

    public void importarTodosOsFeriadoAnuais(Integer ano) {
        var usuario = autenticacaoService.getUsuarioAutenticado();
        var importado = importacaoAutomaticaRepository.save(
            ImportacaoFeriado.of(ESituacaoFeriadoAutomacao.EM_IMPORTACAO, usuario));

        if (uploadAsync) {
            CompletableFuture.runAsync(() -> processarTodosOsFeriados(importado, ano))
                .exceptionally(ex -> {
                    log.error("Erro ao importar feriados", ex);
                    importado.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.ERRO_IMPORTACAO);
                    gerarDescricaoParaErroDeImportacao(importado);
                    importacaoAutomaticaRepository.save(importado);
                    return null;
                });
        } else {
            processarTodosOsFeriados(importado, ano);
        }
    }

    private void processarTodosOsFeriados(ImportacaoFeriado importacaoFeriado, Integer ano) {
        processarFeriadosMunicipais(importacaoFeriado, ano);
        processarFeriadosEstaduais(importacaoFeriado, ano);
        processarFeriadosNacionais(importacaoFeriado, ano);

        importacaoFeriado.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO);
        importacaoAutomaticaRepository.save(importacaoFeriado);
    }

    public void processarFeriadosNacionais(ImportacaoFeriado importacaoFeriado, Integer ano) {
        log.info("Importando feriados nacionais");

        cadastrarFeriados(filtrarFeriadosNacionais(ano), importacaoFeriado);
        gerarDescricaoDeImportacao(NACIONAL, importacaoFeriado);
    }

    public void processarFeriadosEstaduais(ImportacaoFeriado importacaoFeriado, Integer ano) {
        log.info("Importando feriados estaduais");

        ufRepository.findByOrderByNomeAsc()
            .forEach(uf -> cadastrarFeriados(filtrarFeriadosEstaduais(uf, ano), importacaoFeriado));
        gerarDescricaoDeImportacao(ESTADUAL, importacaoFeriado);
    }

    private void processarFeriadosMunicipais(ImportacaoFeriado importacaoFeriado, Integer ano) {
        log.info("Importando feriados municipais");

        processarCidadesAindaNaoCadastradas(ano)
            .forEach(cidade -> cadastrarFeriados(filtrarFeriadosMunicipais(cidade, ano), importacaoFeriado));
        gerarDescricaoDeImportacao(MUNICIPAL, importacaoFeriado);
    }

    private List<FeriadoAutomacao> filtrarFeriadosNacionais(Integer ano) {
        var feriados = feriadoAutomacaoService.consultarFeriadosNacionais(ano)
            .stream()
            .filter(feriadoAutomacao -> feriadoAutomacao != null && !feriadoAutomacao.isFacultativo())
            .collect(toList());

        return filtrarFeriadosNaoCadastradosNoBanco(feriados);
    }

    private List<FeriadoAutomacao> filtrarFeriadosEstaduais(Uf uf, Integer ano) {
        var feridos = feriadoAutomacaoService.consultarFeriadosEstaduais(ano, uf.getUf())
            .stream()
            .filter(feriado -> feriado != null && !feriado.isFacultativo())
            .peek(feriado -> feriado.setUfId(uf.getId()))
            .collect(toList());

        return filtrarFeriadosNaoCadastradosNoBanco(feridos);
    }

    private List<FeriadoAutomacao> filtrarFeriadosMunicipais(Cidade cidade, Integer ano) {
        var feriados = feriadoAutomacaoService.consultarFeriadosMunicipais(ano, cidade.getCodigoUf(), cidade.getNome())
            .stream()
            .filter(feriado -> feriado != null && !feriado.isFacultativo())
            .peek(feriado -> preencherInformacoes(feriado, cidade))
            .collect(toList());

        return filtrarFeriadosNaoCadastradosNoBanco(feriados);
    }

    private void cadastrarFeriados(List<FeriadoAutomacao> feriadosAutomacao, ImportacaoFeriado importacaoFeriado) {
        if (!ObjectUtils.isEmpty(feriadosAutomacao)) {
            feriadoRepository.save(Feriado.ofAutomacao(feriadosAutomacao, importacaoFeriado));
        }
    }

    private void preencherInformacoes(FeriadoAutomacao feriadoAutomacao, Cidade cidade) {
        if (cidade.getIdUf() != null) {
            feriadoAutomacao.setUfId(cidade.getIdUf());
        }
        if (cidade.getId() != null) {
            feriadoAutomacao.setCidadeId(cidade.getId());
        }
    }

    private List<FeriadoAutomacao> filtrarFeriadosNaoCadastradosNoBanco(List<FeriadoAutomacao> feriadosAutomacao) {
        return feriadosAutomacao.stream()
            .filter(feriadoService::validarSeFeriadoNaoCadastrado)
            .collect(toList());
    }

    private List<Cidade> processarCidadesAindaNaoCadastradas(Integer ano) {
        var ultimaCidade = feriadoRepository.findUtimaCidadeFeriadoCadastradoByAno(ano);
        var cidades = cidadeRepository.findAllCidades();

        if (ultimaCidade != null) {
            var index = cidades.stream().map(Cidade::getId).collect(toList()).indexOf(ultimaCidade.getId());
            return cidades.subList(index, cidades.size() - 1);
        }

        return cidades;
    }

    private void gerarDescricaoDeImportacao(ETipoFeriado tipoFeriado, ImportacaoFeriado importacaoFeriado) {
        var feriados = getTotalFeriadosImportadosByTipoFeriado(tipoFeriado, importacaoFeriado.getId());

        importacaoFeriado.gerarDescricao("Total de feriados " + tipoFeriado.getPlural() + " importados: " + feriados);
        log.info("Quantidade de feriados " + tipoFeriado.getPlural() + " importados: {}", feriados);
    }

    private void gerarDescricaoParaErroDeImportacao(ImportacaoFeriado importacaoFeriado) {
        var importacaoFeriadoId = importacaoFeriado.getId();

        var municipais = getTotalFeriadosImportadosByTipoFeriado(MUNICIPAL, importacaoFeriadoId);
        var estaduais = getTotalFeriadosImportadosByTipoFeriado(ESTADUAL, importacaoFeriadoId);
        var nacionais = getTotalFeriadosImportadosByTipoFeriado(NACIONAL, importacaoFeriadoId);

        importacaoFeriado.gerarDescricao("Erro ao importar todos os feriados, total de feriados importados: "
            .concat("Municipais: " + municipais + "Estaduais: " + estaduais + "Nacionais: " + nacionais));
    }

    private long getTotalFeriadosImportadosByTipoFeriado(ETipoFeriado tipoFeriado, Integer importacaoFeriadoId) {
        return feriadoRepository.findTotalFeriadosImportadosByTipoFeriado(tipoFeriado, importacaoFeriadoId);
    }
}
