package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

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
    private String situacao;
    private String usuarioSolicitante;

    public static SolicitacaoRamalHistoricoResponse convertFrom(SolicitacaoRamalHistorico historico) {
        SolicitacaoRamalHistoricoResponse response = new SolicitacaoRamalHistoricoResponse();
        response.situacao = historico.getSituacao().getDescricao();
        response.usuarioSolicitante = historico.getUsuario().getNome();

        BeanUtils.copyProperties(historico, response);

        return response;
    }
}
