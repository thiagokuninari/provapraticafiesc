package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.dto.ImportacaoFeriadoHistoricoResponse;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.repository.ImportacaoAutomaticaFeriadoRepository;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.feriado.predicate.FeriadoPredicate;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoAutomacaoClient;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImportacaoAutomaticaFeriadoService {

    private static final Set<String> FERIADOS_QUE_NAO_CADASTRAM = new HashSet<>(
        Arrays.asList("SEXTA-FEIRA SANTA", "CORPUS CHRISTI", "DIA DA CONCIÊNCIA NEGRA", "FINADOS"));

    private final FeriadoAutomacaoClient feriadoAutomacaoClient;
    private final AutenticacaoService autenticacaoService;
    private final FeriadoService feriadoService;
    private final FeriadoRepository feriadoRepository;
    private final ImportacaoAutomaticaFeriadoRepository importacaoAutomaticaRepository;
    private final UfRepository ufRepository;
    private final CidadeRepository cidadeRepository;
    @Value("${app-config.upload-async}")
    private boolean uploadAsync;

    private List<FeriadoAutomacao> consultarFeriadosNacionais(Integer ano) {
        try {
            return feriadoAutomacaoClient.buscarFeriadosNacionais(ano);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                ImportacaoAutomaticaFeriadoService.class.getName(),
                EErrors.ERRO_BUSCAR_FERIADOS);
        }
    }

    private List<FeriadoAutomacao> consultarFeriadosEstaduais(Integer ano, String uf) {
        try {
            return feriadoAutomacaoClient.buscarFeriadosEstaduais(ano, uf);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                ImportacaoAutomaticaFeriadoService.class.getName(),
                EErrors.ERRO_BUSCAR_FERIADOS);
        }
    }

    private List<FeriadoAutomacao> consultarFeriadosMunicipais(Integer ano, String uf, String cidade) {
        try {
            return feriadoAutomacaoClient.buscarFeriadosMunicipais(ano, uf, cidade);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                ImportacaoAutomaticaFeriadoService.class.getName(),
                EErrors.ERRO_BUSCAR_FERIADOS);
        }
    }

    public Page<ImportacaoFeriadoHistoricoResponse> getAllImportacaoHistorico(PageRequest pageRequest, FeriadoFiltros filtros) {
        var predicate = new FeriadoPredicate().comSituacaoFeriadoAutomacao(filtros.getSituacaoFeriadoAutomacao());
        return importacaoAutomaticaRepository.findAll(predicate.build(), pageRequest)
            .map(ImportacaoFeriadoHistoricoResponse::of);
    }

    public void importarTodosOsFeriadoAnuais() {
        var usuario = autenticacaoService.getUsuarioAutenticado();
        var importado = importacaoAutomaticaRepository.save(
            ImportacaoFeriado.of(ESituacaoFeriadoAutomacao.EM_IMPORTACAO, usuario));

        if (uploadAsync) {
            CompletableFuture.runAsync(() -> processarTodosOsFeriados(importado))
                .exceptionally(ex -> {
                    log.error("Erro ao importar feriados", ex);
                    importado.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.ERRO_IMPORTACAO);
                    importacaoAutomaticaRepository.save(importado);
                    return null;
                });
        } else {
            processarTodosOsFeriados(importado);
        }
    }

    private void processarTodosOsFeriados(ImportacaoFeriado importacaoFeriado) {
        var ano = LocalDate.now().plusYears(1).getYear();

        var feriadosComErro = processarFeriadosMunicipais(importacaoFeriado, ano);
        processarFeriadosEstaduais(importacaoFeriado, ano);
        processarFeriadosNacionais(importacaoFeriado, feriadosComErro, ano);

        importacaoFeriado.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO);
        importacaoAutomaticaRepository.save(importacaoFeriado);
    }

    public void processarFeriadosNacionais(ImportacaoFeriado importacaoFeriado, List<FeriadoAutomacao> feriadosErro,
                                           Integer ano) {
        log.info("Importando feriados nacionais");

        var feriados = corrigirFeriadosComErro(feriadosErro);
        feriados.addAll(consultarFeriadosNacionais(ano).stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));

        var feriadosParaCadastrar = filtrarFeriadosNaoCadastradosNoBanco(feriados);
        cadastrarFeriados(feriadosParaCadastrar, importacaoFeriado);

        importacaoFeriado.gerarDescricao("TOTAL DE FERIADOS NACIONAIS IMPORTADOS:" + feriadosParaCadastrar.size());
        log.info("Total de feriados nacionais importados: {}", feriadosParaCadastrar.size());
    }

    private List<FeriadoAutomacao> processarFeriadosMunicipais(ImportacaoFeriado importacaoFeriado, Integer ano) {
        log.info("Importando feriados municipais");

        List<FeriadoAutomacao> feriados = new ArrayList<>();
        List<FeriadoAutomacao> feriadosComErro = new ArrayList<>();

        cidadeRepository.findAllCidades()
            .forEach(cidade -> feriados.addAll(filtrarFeriadosMunicipais(feriadosComErro, cidade, ano)));

        var feriadosParaCadastrar = filtrarFeriadosNaoCadastradosNoBanco(feriados);

        cadastrarFeriados(feriadosParaCadastrar, importacaoFeriado);
        importacaoFeriado.setDescricao("TOTAL DE FERIADOS MUNICIPAIS CADASTRADOS:" + feriadosParaCadastrar.size());
        log.info("Quantidade de feriados municipais importados: {}", feriadosParaCadastrar.size());
        return feriadosComErro;
    }

    private List<FeriadoAutomacao> filtrarFeriadosMunicipais(List<FeriadoAutomacao> feriadosComErro, Cidade cidade,
                                                             Integer ano) {
        var feriados = consultarFeriadosMunicipais(ano, cidade.getCodigoUf(), cidade.getNome());
        feriadosComErro.addAll(filtrarFeriadosComErro(feriados));

        return feriados.stream()
            .filter(feriado -> feriado != null && !FERIADOS_QUE_NAO_CADASTRAM.contains(feriado.getNome().toUpperCase()))
            .peek(feriadoAutomacao -> preencherInformacoes(feriadoAutomacao, cidade))
            .collect(Collectors.toList());
    }

    public void processarFeriadosEstaduais(ImportacaoFeriado importacaoFeriado, Integer ano) {
        log.info("Importando feriados estaduais");

        List<FeriadoAutomacao> feriados = new ArrayList<>();

        ufRepository.findByOrderByNomeAsc().forEach(uf ->
            feriados.addAll(consultarFeriadosEstaduais(ano, uf.getUf()).stream()
                .filter(Objects::nonNull)
                .peek(feriado -> feriado.setUfId(uf.getId()))
                .collect(Collectors.toList())));

        var feriadosParaCadastrar = filtrarFeriadosNaoCadastradosNoBanco(feriados);

        cadastrarFeriados(feriadosParaCadastrar, importacaoFeriado);
        importacaoFeriado.gerarDescricao("TOTAL DE FERIADOS ESTADUAIS CADASTRADOS:" + feriadosParaCadastrar.size());
        log.info("Quantidade de feriados estaduais importados: {}", feriadosParaCadastrar.size());
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

    private List<FeriadoAutomacao> filtrarFeriadosComErro(List<FeriadoAutomacao> feriadosAutomacao) {
        return feriadosAutomacao.stream()
            .filter(feriadoAutomacao -> FERIADOS_QUE_NAO_CADASTRAM.contains(feriadoAutomacao.getNome().toUpperCase()))
            .collect(Collectors.toList());
    }

    private List<FeriadoAutomacao> corrigirFeriadosComErro(List<FeriadoAutomacao> feriadosAutomacao) {
        return feriadosAutomacao.stream()
            .peek(FeriadoAutomacao::mapToFeriadoNacional)
            .distinct().collect(Collectors.toList());
    }

    private List<FeriadoAutomacao> filtrarFeriadosNaoCadastradosNoBanco(List<FeriadoAutomacao> feriadosAutomacao) {
        return feriadosAutomacao.stream()
            .filter(feriadoService::validarSeFeriadoNaoCadastrado)
            .collect(Collectors.toList());
    }
}
