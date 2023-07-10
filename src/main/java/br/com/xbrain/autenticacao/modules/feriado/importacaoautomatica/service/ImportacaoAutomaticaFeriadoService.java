package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.dto.ImportacaoFeriadoHistoricoResponse;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
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
import feign.Request;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImportacaoAutomaticaFeriadoService {

    private static final Set<String> FERIADOS_QUE_NAO_CADASTRAM = new HashSet<>(
        Arrays.asList("SEXTA-FEIRA SANTA", "CORPUS CHRISTI", "DIA DA CONSCIENCIÊNCIA NEGRA", "FINADOS"));

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
                FeriadoAutomacao.class.getName(),
                EErrors.ERRO_BUSCAR_FERIADOS);
        }
    }

    private List<FeriadoAutomacao> consultarFeriadosEstaduais(Integer ano, String uf) {
        try {
            return feriadoAutomacaoClient.buscarFeriadosEstaduais(ano, uf);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                FeriadoAutomacao.class.getName(),
                EErrors.ERRO_BUSCAR_FERIADOS);
        }
    }

    private List<FeriadoAutomacao> consultarFeriadosMunicipais(FeriadoRequest request) {
        try {
            return feriadoAutomacaoClient.buscarFeriadosMunicipais(
                request.getAno(), request.getUf(), request.getCidadeNome());
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                FeriadoAutomacao.class.getName(),
                EErrors.ERRO_BUSCAR_FERIADOS);
        }
    }

    public void importarTodosOsFeriadoAnuais() {
        var ano = 2026;
        var ufs = ufRepository.findByOrderByNomeAsc();
        var usuario = autenticacaoService.getUsuarioAutenticado();
        var request = FeriadoRequest.builder().ano(ano).build();

        var importado = importacaoAutomaticaRepository.save(
            ImportacaoFeriado.of(ESituacaoFeriadoAutomacao.EM_IMPORTACAO, usuario));

        if (uploadAsync) {
            CompletableFuture.runAsync(() -> processarTodosOsFeriados(request, importado, ufs))
                .exceptionally(ex -> {
                    log.error("Erro ao importar feriados", ex);
                    importado.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.ERRO_IMPORTACAO);
                    importacaoAutomaticaRepository.save(importado);
                    return null;
                });
        } else {
            processarTodosOsFeriados(request, importado, ufs);
        }
    }

    private void processarTodosOsFeriados(FeriadoRequest request, ImportacaoFeriado importacaoFeriado, List<Uf> ufs) {
        processarFeriadosNacionais(request, importacaoFeriado);
        processarFeriadosEstaduais(request, importacaoFeriado, ufs);
        processarFeriadosMunicipais(request, importacaoFeriado, ufs);
        processarFeriadosNacionaisRepetidos(request, importacaoFeriado);

        importacaoFeriado.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO);
        importacaoAutomaticaRepository.save(importacaoFeriado);
    }

    public void processarFeriadosNacionais(FeriadoRequest request, ImportacaoFeriado importacaoFeriado) {
        var feriados = consultarFeriadosNacionais(request.getAno()).stream()
            .filter(feriado -> feriado != null
                && feriadoService.validarSeFeriadoNaoCadastrado(feriado, request))
            .collect(Collectors.toList());

        cadastrarFeriados(feriados, request, importacaoFeriado);

        importacaoFeriado.setDescricao("TOTAL DE FERIADOS NACIONAIS IMPORTADOS:" + feriados.size());
        log.info("Total de feriados nacionais importados: {}", feriados.size());
    }

    private void processarFeriadosMunicipais(FeriadoRequest request, ImportacaoFeriado importacaoFeriado, List<Uf> ufs) {
        List<FeriadoAutomacao> feriados = new ArrayList<>();

        ufs.forEach(estado -> {
            request.setEstadoId(estado.getId());
            var cidades = cidadeRepository.findCidadesByUfId(request.getEstadoId());
            cidades.forEach(cidade -> importarFeriadoMunicipal(request, importacaoFeriado, feriados, cidade));
        });

        importacaoFeriado.gerarDescricao("TOTAL DE FERIADOS MUNICIPAIS CADASTRADOS" + feriados.size());
        log.info("Quantidade de feriados municipais importados: {}", feriados.size());
    }

    private void importarFeriadoMunicipal(FeriadoRequest request, ImportacaoFeriado importacaoFeriado,
                                          List<FeriadoAutomacao> feriados, Cidade cidade) {
        preencherInformacoes(request, cidade);

        var feriadosNaoCadastrados = consultarFeriadosMunicipais(request).stream()
            .filter(feriado -> feriado != null
                && !FERIADOS_QUE_NAO_CADASTRAM.contains(feriado.getNome().toUpperCase())
                && feriadoService.validarSeFeriadoNaoCadastrado(feriado, request))
            .collect(Collectors.toList());

        cadastrarFeriados(feriadosNaoCadastrados, request, importacaoFeriado);
        feriados.addAll(feriadosNaoCadastrados);
    }

    public void processarFeriadosEstaduais(FeriadoRequest request, ImportacaoFeriado importacaoFeriado, List<Uf> ufs) {
        List<FeriadoAutomacao> feriados = new ArrayList<>();

        log.info("Importando feriados");
        ufs.forEach(uf -> {
            var feriadosNaoCadastrados = consultarFeriadosEstaduais(request.getAno(), uf.getUf()).stream()
                .filter(feriado -> feriado != null
                    && feriadoService.validarSeFeriadoNaoCadastrado(feriado, request))
                .collect(Collectors.toList());

            cadastrarFeriados(feriadosNaoCadastrados, request, importacaoFeriado);
            feriados.addAll(feriadosNaoCadastrados);
        });

        importacaoFeriado.gerarDescricao("TOTAL DE FERIADOS ESTADUAIS CADASTRADOS" + feriados.size());

        log.info("Quantidade de feriados estaduais importados: {}", feriados.size());
    }

    private void processarFeriadosNacionaisRepetidos(FeriadoRequest request, ImportacaoFeriado importacaoFeriado) {
        request.setUf("SP");
        request.setNome("são paulo");
        var feriados = consultarFeriadosMunicipais(request).stream()
            .filter(feriado -> feriado != null
                && FERIADOS_QUE_NAO_CADASTRAM.contains(feriado.getNome().toUpperCase()))
            .collect(Collectors.toList());

        feriados.forEach(feriado -> feriado.setTipoFeriado(ETipoFeriado.NACIONAL));
        cadastrarFeriados(feriados, request, importacaoFeriado);
    }


    private void cadastrarFeriados(List<FeriadoAutomacao> feriadosAutomacao, FeriadoRequest request,
                                   ImportacaoFeriado importacaoFeriado) {
        if (!feriadosAutomacao.isEmpty()) {
            feriadosAutomacao.forEach(feriado -> {
                feriadoRepository.save(Feriado.ofAutomacao(feriado, request, importacaoFeriado));
            });
        }
    }

    public Page<ImportacaoFeriadoHistoricoResponse> getAllImportacaoHistorico(PageRequest pageRequest, FeriadoFiltros filtros) {
        var predicate = new FeriadoPredicate().comSituacaoFeriadoAutomacao(filtros.getSituacaoFeriadoAutomacao());
        return importacaoAutomaticaRepository.findAll(predicate.build(), pageRequest)
            .map(ImportacaoFeriadoHistoricoResponse::convertFrom);
    }

    private void preencherInformacoes(FeriadoRequest request, Cidade cidade) {
        if (nonNull(cidade.getNome())) {
            request.setCidadeNome(cidade.getNome());
        }
        if (nonNull(cidade.getUf())) {
            request.setUf(cidade.getUf().getUf());
        }
        if (nonNull(cidade.getId())) {
            request.setCidadeId(cidade.getId());
        }
    }
}
