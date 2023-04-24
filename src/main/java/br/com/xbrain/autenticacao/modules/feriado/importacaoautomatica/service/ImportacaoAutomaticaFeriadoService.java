package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
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
import br.com.xbrain.autenticacao.modules.usuario.predicate.CidadePredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    private List<FeriadoAutomacao> consultarFeriadosNacionais(Integer ano) {
        try {
            return feriadoAutomacaoClient.buscarFeriadosNacionais(ano);
        } catch (RetryableException | HystrixBadRequestException ex) {
            throw new IntegracaoException(ex,
                FeriadoAutomacao.class.getName(),
                EErrors.ERRO_BUSCAR_FERIADOS);
        }
    }

    private List<FeriadoAutomacao> consultarFeriadosMunicipais(FeriadoRequest request) {
        try {
            return feriadoAutomacaoClient.consultarFeriadosMunicipais(
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
            request.setCidadeNome(cidade.getNome());
            request.setUf(cidade.getUf().getUf());
            request.setCidadeId(cidade.getId());
            var feriados = consultarFeriadosMunicipais(request);
            cadastrarFeriados(feriados, usuarioAutenticado, request);
        });

        importacaoAutomaticaRepository.save(
            ImportacaoFeriado.of(ESituacaoFeriadoAutomacao.IMPORTADO, usuarioAutenticado));
        log.info("usuario importação cadastrado com sucesso");
    }

    private void cadastrarFeriados(List<FeriadoAutomacao> feriadosAutomacao, UsuarioAutenticado usuario,
                                   FeriadoRequest request) {
        feriadosAutomacao.forEach(feriado -> {
            feriadoService.validarSeFeriadoJaCadastado(feriado, request);
            request.setDataFeriado(feriado.getDataFeriado());
            request.setTipoFeriado(feriado.getTipoFeriado());
            request.setNome(feriado.getNome());
            feriadoRepository.save(Feriado.of(request, usuario.getId()));
        });

        log.info("feriados importados com sucesso");
    }

    public void importarFeriadosAutomacaoNacionais(Integer ano) {
        log.info("Importando feriados Nacionais do ano: " + ano);

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

    private void validarAutorizacaoGerenciamentoFeriados(UsuarioAutenticado usuario) {
        if (!usuario.hasPermissao(CodigoFuncionalidade.CTR_2050)) {
            throw new ValidacaoException("Usuario sem permissao para gerenciamento de feriados");
        }
    }
}
