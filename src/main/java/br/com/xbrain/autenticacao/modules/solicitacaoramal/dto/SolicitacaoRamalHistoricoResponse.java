package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class SolicitacaoRamalHistoricoResponse {

    private Integer id;
    private String comentario;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataCadastro;
    private ESituacaoSolicitacao situacao;
    private String usuarioSolicitante;

    public static SolicitacaoRamalHistoricoResponse convertFrom(SolicitacaoRamalHistorico historico) {
        SolicitacaoRamalHistoricoResponse response = new SolicitacaoRamalHistoricoResponse();
        response.usuarioSolicitante = historico.getUsuario().getNome();

        BeanUtils.copyProperties(historico, response);

        return response;
    }
}
