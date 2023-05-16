package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.repository.ImportacaoAutomaticaFeriadoRepository;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoAutomacaoClient;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private final CidadeService cidadeService;
    private final UfRepository ufRepository;

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

    @Transactional
    public void importarFeriadosAutomacaoMunicipais(FeriadoRequest request) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        validarAutorizacaoGerenciamentoFeriados(usuarioAutenticado);
        var importado = importacaoAutomaticaRepository.save(
            ImportacaoFeriado.of(ESituacaoFeriadoAutomacao.EM_IMPORTACAO, usuarioAutenticado));

        var cidades = cidadeService.getAllCidadeByUf(request.getEstadoId());
        log.info("Importando feriados");
        cidades.forEach(cidade -> {
            preencherInformacoes(request, cidade);
            var feriados = consultarFeriadosMunicipais(request).stream()
                .filter(feriado -> !FERIADOS_QUE_NAO_CADASTRAM.contains(feriado.getNome()))
                .collect(Collectors.toList());

            cadastrarFeriados(feriados, usuarioAutenticado, request, importado);
        });

        importado.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO);
        importacaoAutomaticaRepository.save(importado);
        log.info("Usuario importação cadastrado com sucesso");
    }

    @Transactional
    public void importarFeriadosAutomacaoEstaduais(FeriadoRequest request) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        validarAutorizacaoGerenciamentoFeriados(usuarioAutenticado);
        var importado = importacaoAutomaticaRepository.save(
            ImportacaoFeriado.of(ESituacaoFeriadoAutomacao.EM_IMPORTACAO, usuarioAutenticado));

        var ufs = ufRepository.findByOrderByNomeAsc();
        ufs.forEach(uf -> {
            var feriados = consultarFeriadosEstaduais(request.getAno(), uf.getUf());
            validarFeriadosEstaduais(feriados);
            cadastrarFeriados(feriados, usuarioAutenticado, request, importado);
        });

        importado.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO);
        importacaoAutomaticaRepository.save(importado);
        log.info("Usuario importação cadastrado com sucesso");
    }

    @Transactional
    public void importarFeriadosAutomacaoNacionais(FeriadoRequest request) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        validarAutorizacaoGerenciamentoFeriados(usuarioAutenticado);
        var importado = importacaoAutomaticaRepository.save(
            ImportacaoFeriado.of(ESituacaoFeriadoAutomacao.EM_IMPORTACAO, usuarioAutenticado));

        var feriados = consultarFeriadosNacionais(request.getAno());
        cadastrarFeriados(feriados, usuarioAutenticado, request, importado);

        importado.setSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO);
        importacaoAutomaticaRepository.save(importado);
        log.info("Usuario importação cadastrado com sucesso");
    }

    private void cadastrarFeriados(List<FeriadoAutomacao> feriadosAutomacao, UsuarioAutenticado usuario,
                                   FeriadoRequest request, ImportacaoFeriado importacaoFeriado) {
        log.info("Importando feriados");
        feriadosAutomacao.forEach(feriado -> {
            feriadoService.validarSeFeriadoAutomacaoJaCadastado(feriado, request);
            feriadoRepository.save(Feriado.ofAutomacao(feriado, usuario.getId(), request, importacaoFeriado));
        });
        log.info("Feriados importados com sucesso");
    }

    private void validarFeriadosEstaduais(List<FeriadoAutomacao> feriados) {
        if (feriados.isEmpty()) {
            throw new ValidacaoException("Não ha feriados para importar");
        }
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
}
