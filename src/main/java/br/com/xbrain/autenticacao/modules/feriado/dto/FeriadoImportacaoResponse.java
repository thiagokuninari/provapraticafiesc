package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.springframework.util.ObjectUtils.isEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeriadoImportacaoResponse {

    private Integer id;
    private String nome;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFeriado;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dataCadastro;
    private Eboolean feriadoNacional;
    private Integer cidadeId;
    private String cidadeNome;
    private Integer estadoId;
    private String estadoNome;
    private ETipoFeriado tipoFeriado;
    private List<String> motivoNaoImportacao;
    private Eboolean feriadoImportadoComSucesso;

    public static FeriadoImportacaoResponse of(Feriado feriadoSalvo) {
        var response = new FeriadoImportacaoResponse();
        BeanUtils.copyProperties(feriadoSalvo, response);
        if (nonNull(feriadoSalvo.getCidade())) {
            response.setCidadeId(feriadoSalvo.getCidade().getId());
            response.setCidadeNome(feriadoSalvo.getCidade().getNome());
        }
        if (nonNull(feriadoSalvo.getUf())) {
            response.setEstadoId(feriadoSalvo.getUf().getId());
            response.setEstadoNome(feriadoSalvo.getUf().getNome());
        }
        response.setMotivoNaoImportacao(Collections.emptyList());
        response.setFeriadoImportadoComSucesso(Eboolean.V);
        return response;
    }

    public static FeriadoImportacaoResponse of(FeriadoImportacao feriadoParaImportar) {
        var response = new FeriadoImportacaoResponse();
        BeanUtils.copyProperties(feriadoParaImportar, response);
        if (nonNull(feriadoParaImportar.getCidade())) {
            response.setCidadeId(feriadoParaImportar.getCidade().getId());
            response.setCidadeNome(feriadoParaImportar.getCidade().getNome());
        }
        if (nonNull(feriadoParaImportar.getUf())) {
            response.setEstadoId(feriadoParaImportar.getUf().getId());
            response.setEstadoNome(feriadoParaImportar.getUf().getNome());
        }
        response.setFeriadoImportadoComSucesso(Eboolean.valueOf(isEmpty(feriadoParaImportar.getMotivoNaoImportacao())));
        return response;
    }
}
