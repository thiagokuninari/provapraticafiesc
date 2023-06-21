package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.dto.ImportacaoFeriadoHistoricoResponse;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.repository.ImportacaoAutomaticaFeriadoRepository;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoAutomacaoClient;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
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
        Arrays.asList("Sexta Feira Santa", "Corpus Christi"));
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

    public void importarFeriadosAutomacaoMunicipais(FeriadoRequest request) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        validarAutorizacaoGerenciamentoFeriados(usuarioAutenticado);
        var importacaoFeriado = importacaoAutomaticaRepository.save(
            ImportacaoFeriado.of(ESituacaoFeriadoAutomacao.EM_IMPORTACAO, usuarioAutenticado));

        if (uploadAsync) {
            CompletableFuture.runAsync(() -> processarFeriadosMunicipaisAsync(request, usuarioAutenticado, importacaoFeriado))
                .exceptionally(ex -> {
                    log.error("Erro ao importar feriados municipais", ex);
                    importacaoFeriado.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.ERRO_IMPORTACAO);
                    importacaoAutomaticaRepository.save(importacaoFeriado);
                    return null;
                });
        } else {
            processarFeriadosMunicipaisAsync(request, usuarioAutenticado, importacaoFeriado);
        }
    }

    public void importarFeriadosAutomacaoEstaduais(FeriadoRequest request) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        validarAutorizacaoGerenciamentoFeriados(usuarioAutenticado);
        var importacaoFeriado = importacaoAutomaticaRepository.save(
            ImportacaoFeriado.of(ESituacaoFeriadoAutomacao.EM_IMPORTACAO, usuarioAutenticado));

        if (uploadAsync) {
            CompletableFuture.runAsync(() -> processarFeriadosEstaduaisAsync(request, usuarioAutenticado, importacaoFeriado))
                .exceptionally(ex -> {
                    log.error("Erro ao importar feriados estaduais", ex);
                    importacaoFeriado.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.ERRO_IMPORTACAO);
                    importacaoAutomaticaRepository.save(importacaoFeriado);
                    return null;
                });
        } else {
            processarFeriadosEstaduaisAsync(request, usuarioAutenticado, importacaoFeriado);
        }
    }

    public void importarFeriadosAutomacaoNacionais(FeriadoRequest request) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        validarAutorizacaoGerenciamentoFeriados(usuarioAutenticado);
        var importado = importacaoAutomaticaRepository.save(
            ImportacaoFeriado.of(ESituacaoFeriadoAutomacao.EM_IMPORTACAO, usuarioAutenticado));

        var feriados = consultarFeriadosNacionais(request.getAno()).stream()
            .filter(feriado -> feriado != null
                && feriadoService.validarSeFeriadoNaoCadastrado(feriado, request))
            .collect(Collectors.toList());

        validarSeTodosFeriadosJaCadastrados(feriados);
        cadastrarFeriados(feriados, usuarioAutenticado.getId(), request, importado);

        importado.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO);
        importacaoAutomaticaRepository.save(importado);
        log.info("Usuario importação cadastrado com sucesso");
    }

    private void cadastrarFeriados(List<FeriadoAutomacao> feriadosAutomacao, Integer usuarioId,
                                   FeriadoRequest request, ImportacaoFeriado importacaoFeriado) {
        log.info("Importando feriados");
        feriadosAutomacao.forEach(feriado -> {
            feriadoRepository.save(Feriado.ofAutomacao(feriado, usuarioId, request, importacaoFeriado));
        });

        log.info("Feriados importados com sucesso");
    }

    private void processarFeriadosMunicipaisAsync(FeriadoRequest request, UsuarioAutenticado usuarioAutenticado,
                                                  ImportacaoFeriado importacaoFeriado) {
        var cidades = cidadeRepository.findCidadesByUfId(request.getEstadoId());
        List<FeriadoAutomacao> feriados = new ArrayList<>();

        log.info("Importando feriados");
        cidades.forEach(cidade -> {
            preencherInformacoes(request, cidade);

            var feriadosNaoCadastrados = consultarFeriadosMunicipais(request).stream()
                .filter(feriado -> feriado != null
                    && !FERIADOS_QUE_NAO_CADASTRAM.contains(feriado.getNome())
                    && feriadoService.validarSeFeriadoNaoCadastrado(feriado, request))
                .collect(Collectors.toList());

            cadastrarFeriados(feriadosNaoCadastrados, usuarioAutenticado.getId(), request, importacaoFeriado);
            feriados.addAll(feriadosNaoCadastrados);
        });

        validarSeTodosFeriadosJaCadastrados(feriados);
        importacaoFeriado.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO);
        importacaoAutomaticaRepository.save(importacaoFeriado);

        log.info("Quantidade de feriados importados: {}", feriados.size());
        log.info("Usuario importação cadastrado com sucesso");
    }

    public void processarFeriadosEstaduaisAsync(FeriadoRequest request, UsuarioAutenticado usuarioAutenticado,
                                                ImportacaoFeriado importacaoFeriado) {
        var ufs = ufRepository.findByOrderByNomeAsc();
        List<FeriadoAutomacao> feriados = new ArrayList<>();

        log.info("Importando feriados");
        ufs.forEach(uf -> {
            var feriadosNaoCadastrados = consultarFeriadosEstaduais(request.getAno(), uf.getUf()).stream()
                .filter(feriado -> feriado != null
                    && feriadoService.validarSeFeriadoNaoCadastrado(feriado, request))
                .collect(Collectors.toList());

            cadastrarFeriados(feriadosNaoCadastrados, usuarioAutenticado.getId(), request, importacaoFeriado);
            feriados.addAll(feriadosNaoCadastrados);
        });

        validarSeTodosFeriadosJaCadastrados(feriados);
        importacaoFeriado.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO);
        importacaoAutomaticaRepository.save(importacaoFeriado);

        log.info("Quantidade de feriados importados: {}", feriados.size());
        log.info("Usuario importação cadastrado com sucesso");
    }

    public Page<ImportacaoFeriadoHistoricoResponse> getAllImportacaoHistorico(PageRequest pageRequest, FeriadoFiltros filtros) {
        return importacaoAutomaticaRepository.findAllImportacaoHistorico(pageRequest, filtros.toPredicate().build())
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

    private void validarAutorizacaoGerenciamentoFeriados(UsuarioAutenticado usuario) {
        if (!usuario.hasPermissao(CodigoFuncionalidade.CTR_2050)) {
            throw new ValidacaoException("Usuario sem permissao para gerenciamento de feriados");
        }
    }

    private void validarSeTodosFeriadosJaCadastrados(List<FeriadoAutomacao> feriadosNaoCadastrados) {
        if (feriadosNaoCadastrados.isEmpty()) {
            throw new ValidacaoException("Feriados ja cadastrados");
        }
    }
}
