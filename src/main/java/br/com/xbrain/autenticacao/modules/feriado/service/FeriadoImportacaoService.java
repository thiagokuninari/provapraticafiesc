package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.comum.service.CsvFileService;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoImportacao;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoImportacaoRequest;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoImportacaoResponse;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.predicate.FeriadoPredicate;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import br.com.xbrain.xbrainutils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.feriado.util.FeriadoPlanilhaLayoutUtil.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@Slf4j
public class FeriadoImportacaoService {

    @Autowired
    private UfRepository ufRepository;
    @Autowired
    private CidadeService cidadeService;
    @Autowired
    private FeriadoRepository feriadoRepository;
    @Autowired
    private FeriadoService feriadoService;

    private static final ValidacaoException EX_ARQUIVO_VAZIO = new ValidacaoException("O arquivo não pode ser vazio.");
    private static final ValidacaoException EX_CABECALHO_DIFERENTE =
        new ValidacaoException("O cabeçalho do arquivo não pode ser diferente do exemplo.");
    private static final String MENSAGEM_VAZIA = "";
    private static final String ERRO_TIPO_FERIADO = "Falha ao recuperar Tipo do Feriado.";
    private static final String ERRO_UF = "Falha ao recuperar UF.";
    private static final String ERRO_CIDADE = "Falha ao recuperar Cidade.";
    private static final String ERRO_NOME = "Feriado está com nome inválido.";
    private static final String ERRO_FERIADO_EXISTENTE = "Feriado já cadastrado.";
    private static final String ERRO_DATA_INVALIDA = "Feriado está com data inválida.";
    private static final String ERRO_DATA_FORA_ANO_REFERENCIA = "A data do feriado não está no ano de referência.";

    public List<FeriadoImportacaoResponse> importarFeriadoArquivo(MultipartFile file, FeriadoImportacaoRequest request) {
        var linhas = CsvFileService.readCsvFile(file, LER_SEM_CABECALHO);
        validarArquivoVazio(linhas);
        validarCabecalho(linhas.get(PRIMEIRA_LINHA));
        linhas.remove(PRIMEIRA_LINHA);
        var linhasProcessados = processarLinhas(linhas, request.getAnoReferencia());
        validarLinhasProcessados(linhasProcessados);
        feriadoService.flushCacheFeriados();
        feriadoService.flushCacheFeriadoTelefonia();
        feriadoService.flushCacheFeriadoMailing();
        return linhasProcessados;
    }

    private void validarCabecalho(String cabecalho) {
        var colunas = cabecalho.split(DELIMITADOR);
        if (colunas.length < QNT_COLUNAS || !isColunasValidas(colunas)) {
            throw EX_CABECALHO_DIFERENTE;
        }
    }

    private boolean isColunasValidas(String[] colunas) {
        return compararColunas(colunas[ORDEM_COL_TIPO_FERIADO], NOME_COL_TIPO_FERIADO)
            && compararColunas(colunas[ORDEM_COL_CIDADE_NOME], NOME_COL_CIDADE_NOME)
            && compararColunas(colunas[ORDEM_COL_UF], NOME_COL_UF)
            && compararColunas(colunas[ORDEM_COL_DATA_FERIADO], NOME_COL_DATA_FERIADO)
            && compararColunas(colunas[ORDEM_COL_NOME], NOME_COL_NOME);
    }

    private boolean compararColunas(String colunaUploaded, String colunaLayout) {
        return !isEmpty(colunaLayout)
            && colunaUploaded.trim()
            .equalsIgnoreCase(colunaLayout);
    }

    private void validarArquivoVazio(List<String> linhas) {
        if (linhas.size() <= 1) {
            throw EX_ARQUIVO_VAZIO;
        }
    }

    private void validarLinhasProcessados(List<FeriadoImportacaoResponse> linhasProcessados) {
        if (isEmpty(linhasProcessados)) {
            throw EX_ARQUIVO_VAZIO;
        }
    }

    private List<FeriadoImportacaoResponse> processarLinhas(List<String> linhas, Integer anoReferencia) {
        return linhas.stream()
            .map(linha -> linha.split(DELIMITADOR))
            .filter(linha -> !isLinhaVazia(linha))
            .map(linha -> importarFeriado(linha, anoReferencia))
            .collect(Collectors.toList());
    }

    public FeriadoImportacaoResponse importarFeriado(String[] linha, Integer anoReferencia) {
        var feriadoParaImportar = gerarFeriadoImportacao(linha);
        validarFeriado(feriadoParaImportar, anoReferencia);

        if (feriadoParaImportar.getMotivoNaoImportacao().isEmpty()) {
            var feriadoImportado = feriadoService.salvarFeriadoImportado(feriadoParaImportar);
            return FeriadoImportacaoResponse.of(feriadoImportado);
        } else {
            return FeriadoImportacaoResponse.of(feriadoParaImportar);
        }
    }

    private FeriadoImportacao gerarFeriadoImportacao(String[] linha) {
        var feriadoParaImportar = FeriadoImportacao.builder()
            .nome(linha[ORDEM_COL_NOME].trim())
            .dataFeriado(tratarDataFeriado(linha[ORDEM_COL_DATA_FERIADO]).orElse(null))
            .tipoFeriado(recuperarTipoFeriado(linha[ORDEM_COL_TIPO_FERIADO]).orElse(null))
            .build();

        recuperarUfECidade(feriadoParaImportar, linha[ORDEM_COL_UF], linha[ORDEM_COL_CIDADE_NOME]);
        return feriadoParaImportar;
    }

    private FeriadoImportacao validarFeriado(FeriadoImportacao feriadoParaImportar, Integer anoReferencia) {
        feriadoParaImportar.setMotivoNaoImportacao(
            Stream.of(
                validarTipoFeriado(feriadoParaImportar),
                validarUf(feriadoParaImportar),
                validarCidade(feriadoParaImportar),
                validarDataFeriado(feriadoParaImportar, anoReferencia),
                validarNome(feriadoParaImportar),
                validarFeriadoExistente(feriadoParaImportar))
                .filter(validacao -> !validacao.isEmpty())
                .collect(Collectors.toList()));
        return feriadoParaImportar;
    }

    private boolean isLinhaVazia(String[] linha) {
        return linha.length == 0;
    }

    private Optional<ETipoFeriado> recuperarTipoFeriado(String tipoFeriadoStr) {
        try {
            return Optional.of(ETipoFeriado.valueOf(tratarString(tipoFeriadoStr)));
        } catch (IllegalArgumentException ex) {
            log.error(ERRO_TIPO_FERIADO, ex);
            return Optional.empty();
        }
    }

    private void recuperarUfECidade(FeriadoImportacao feriado, String uf, String cidadeNome) {
        if (feriado.isTipoFeriadoComUfObrigatorio()) {
            feriado.setUf(recuperarUf(tratarString(uf)).orElse(null));
        }
        if (feriado.isTipoFeriadoComCidadeObrigatorio()) {
            feriado.setCidade(recuperarCidade(tratarString(uf), tratarString(cidadeNome)).orElse(null));
        }
    }

    private Optional<Uf> recuperarUf(String uf) {
        try {
            return ufRepository.findByUf(uf);
        } catch (Exception ex) {
            log.error(ERRO_UF, ex);
            return Optional.empty();
        }
    }

    private Optional<Cidade> recuperarCidade(String uf, String cidadeNome) {
        try {
            return Optional.of(cidadeService.findByUfNomeAndCidadeNome(uf, cidadeNome));
        } catch (Exception ex) {
            log.error(ERRO_CIDADE, ex);
            return Optional.empty();
        }
    }

    private String tratarString(String str) {
        return StringUtil.removerAcentos(str)
            .trim()
            .toUpperCase();
    }

    private Optional<LocalDate> tratarDataFeriado(String dataFeriadoStr) {
        try {
            return Optional.ofNullable(DateUtils.parseStringToLocalDate(dataFeriadoStr.trim()));
        } catch (Exception ex) {
            log.error("Erro ao tratar Data do Feriado.", ex);
            return Optional.empty();
        }
    }

    private String validarTipoFeriado(FeriadoImportacao feriado) {
        return Optional.ofNullable(feriado.getTipoFeriado())
            .map(tipoFeriado -> MENSAGEM_VAZIA)
            .orElse(ERRO_TIPO_FERIADO);
    }

    private String validarUf(FeriadoImportacao feriado) {
        return feriado.isTipoFeriadoComUfObrigatorio() && isEmpty(feriado.getUf())
            ? ERRO_UF : MENSAGEM_VAZIA;
    }

    private String validarCidade(FeriadoImportacao feriado) {
        return feriado.isTipoFeriadoComCidadeObrigatorio() && isEmpty(feriado.getCidade())
            ? ERRO_CIDADE : MENSAGEM_VAZIA;
    }

    private String validarDataFeriado(FeriadoImportacao feriado, Integer anoReferencia) {
        return Optional.ofNullable(feriado.getDataFeriado())
            .map(dataFeriado -> dataFeriado.getYear() != anoReferencia
                ? ERRO_DATA_FORA_ANO_REFERENCIA
                : MENSAGEM_VAZIA)
            .orElse(ERRO_DATA_INVALIDA);
    }

    private String validarNome(FeriadoImportacao feriado) {
        return isEmpty(feriado.getNome())
            || feriado.getNome().length() > TAMANHO_MAX_NOME
            ? ERRO_NOME : MENSAGEM_VAZIA;
    }

    private String validarFeriadoExistente(FeriadoImportacao feriado) {
        var predicate = new FeriadoPredicate()
            .comNome(feriado.getNome())
            .comTipoFeriado(feriado.getTipoFeriado())
            .comEstado(!isEmpty(feriado.getUf()) ? feriado.getUf().getId() : null)
            .comCidadeId(!isEmpty(feriado.getCidade()) ? feriado.getCidade().getId() : null,
                !isEmpty(feriado.getUf()) ? feriado.getUf().getId() : null)
            .comDataFeriado(feriado.getDataFeriado())
            .excetoExcluidos()
            .excetoFeriadosFilhos()
            .build();
        if (feriadoRepository.existsByPredicate(predicate)) {
            return ERRO_FERIADO_EXISTENTE;
        }

        return MENSAGEM_VAZIA;
    }
}
