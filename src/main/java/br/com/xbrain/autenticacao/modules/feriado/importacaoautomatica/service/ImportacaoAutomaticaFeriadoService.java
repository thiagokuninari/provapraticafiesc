package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.comum.service.UfService;
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
import br.com.xbrain.autenticacao.modules.usuario.predicate.CidadePredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Objects.nonNull;

@Service
@Slf4j
public class ImportacaoAutomaticaFeriadoService {

    @Autowired
    private FeriadoAutomacaoClient feriadoAutomacaoClient;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private FeriadoService feriadoService;
    @Autowired
    private UfService ufService;
    @Autowired
    private CidadeRepository cidadeRepository;
    @Autowired
    private FeriadoRepository feriadoRepository;
    @Autowired
    private ImportacaoAutomaticaFeriadoRepository importacaoAutomaticaRepository;
    @Autowired
    private UfRepository ufRepository;

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

        var predicate = new CidadePredicate().comUfId(request.getEstadoId()).build();
        var cidades = cidadeRepository.findCidadesByPredicate(predicate);
        log.info("Importando feriados");
        cidades.forEach(cidade -> {
            preencherInformacoes(request, cidade);
            var feriados = consultarFeriadosMunicipais(request);
            cadastrarFeriados(feriados, usuarioAutenticado, request);
        });

        importacaoAutomaticaRepository.save(
            ImportacaoFeriado.of(ESituacaoFeriadoAutomacao.IMPORTADO, usuarioAutenticado));
        log.info("usuario importação cadastrado com sucesso");
    }

    @Transactional
    public void importarFeriadosAutomacaoEstaduais(FeriadoRequest request) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        validarAutorizacaoGerenciamentoFeriados(usuarioAutenticado);

        var predicate = new CidadePredicate().comUfId(request.getEstadoId()).build();
        var cidades = cidadeRepository.findCidadesByPredicate(predicate);
        var uf = ufRepository.findOne(request.getEstadoId());
        var feriados = consultarFeriadosEstaduais(request.getAno(), uf.getUf());
        validarFeriados(feriados);
        log.info("Importando feriados");
        cidades.forEach(cidade -> {
            preencherInformacoes(request, cidade);
            cadastrarFeriados(feriados, usuarioAutenticado, request);
        });

        importacaoAutomaticaRepository.save(
            ImportacaoFeriado.of(ESituacaoFeriadoAutomacao.IMPORTADO, usuarioAutenticado));
        log.info("usuario importação cadastrado com sucesso");
    }

    @Transactional
    public void importarFeriadosAutomacaoNacionais(FeriadoRequest request) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        validarAutorizacaoGerenciamentoFeriados(usuarioAutenticado);

        log.info("Importando feriados nacionais do ano de " + request.getAno());
        var feriados = consultarFeriadosNacionais(request.getAno());
        cadastrarFeriados(feriados, usuarioAutenticado, request);

        importacaoAutomaticaRepository.save(
            ImportacaoFeriado.of(ESituacaoFeriadoAutomacao.IMPORTADO, usuarioAutenticado));
        log.info("usuario importação cadastrado com sucesso");
    }

    private void cadastrarFeriados(List<FeriadoAutomacao> feriadosAutomacao,
                                   UsuarioAutenticado usuario,
                                   FeriadoRequest request) {
        feriadosAutomacao.forEach(feriado -> {
            feriadoService.validarSeFeriadoJaCadastado(feriado, request);
            feriadoRepository.save(Feriado.of(feriado, usuario.getId()));
        });

        log.info("feriados importados com sucesso");
    }

    private void validarFeriados(List<FeriadoAutomacao> feriados) {
        if (feriados.isEmpty()) {
            throw new ValidacaoException("Não ha feriados para importar");
        }
    }

    private void preencherInformacoes(FeriadoRequest request, Cidade cidade) {
        if (nonNull(request.getUf())) {
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
