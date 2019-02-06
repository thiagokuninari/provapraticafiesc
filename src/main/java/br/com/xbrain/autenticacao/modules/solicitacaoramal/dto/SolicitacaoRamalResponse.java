package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.util.SolicitacaoRamalExpiracaoAdjuster;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SolicitacaoRamalResponse {

    private Integer id;
    private Integer quantidadeRamais;
    private ESituacaoSolicitacao situacao;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataCadastro;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime horaExpiracao;
    private String usuarioSolicitante;

    public static SolicitacaoRamalResponse convertFrom(SolicitacaoRamal solicitacaoRamal) {
        SolicitacaoRamalResponse response = new SolicitacaoRamalResponse();
        response.usuarioSolicitante = solicitacaoRamal.getUsuario().getNome();

        response.calcularHoraDeExpiracaoDaSolicitacao(solicitacaoRamal.getDataCadastro());

        BeanUtils.copyProperties(solicitacaoRamal, response);

        return response;
    }

    private void calcularHoraDeExpiracaoDaSolicitacao(LocalDateTime dataCadastro) {
        LocalDateTime dataExpiracao = LocalDateTime.from(dataCadastro.with(new SolicitacaoRamalExpiracaoAdjuster()));

        long diferencaEmSegundos = getDiferencaEmSegundosDataExpiracaoEDataAtual(dataExpiracao);

        this.horaExpiracao = LocalDateTime.now().plusSeconds(diferencaEmSegundos);
    }

    private long getDiferencaEmSegundosDataExpiracaoEDataAtual(LocalDateTime expiracao) {
        return LocalDateTime.now().until(expiracao, ChronoUnit.SECONDS);
    }

}
