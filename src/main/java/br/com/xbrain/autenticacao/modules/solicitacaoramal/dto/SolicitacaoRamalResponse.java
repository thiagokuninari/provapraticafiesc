package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
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

    private static final int HORA_DEFAULT_EXPIRACAO = 72;

    private Integer id;
    private Integer usuarioId;
    private Integer quantidadeRamais;
    private ESituacao situacao;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataCadastro;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalDateTime horaExpiracao;

    public static SolicitacaoRamalResponse convertFrom(SolicitacaoRamal solicitacaoRamal) {
        SolicitacaoRamalResponse response = new SolicitacaoRamalResponse();
        response.setUsuarioId(solicitacaoRamal.getUsuario().getId());

        response.calcularHoraDeExpiracaoDaSolicitacao(solicitacaoRamal.getDataCadastro());

        BeanUtils.copyProperties(solicitacaoRamal, response);

        return response;
    }

    public void calcularHoraDeExpiracaoDaSolicitacao(LocalDateTime dataCadastro) {
        LocalDateTime expiracao = getDataExpiracao(dataCadastro);

        long diferencaEmSegundos = getDiferencaEmSegundosDataExpiracaoEDataAtual(expiracao);

        this.horaExpiracao = LocalDateTime.now().plusSeconds(diferencaEmSegundos);
    }

    private LocalDateTime getDataExpiracao(LocalDateTime dataCadastro) {
        return dataCadastro.plusHours(HORA_DEFAULT_EXPIRACAO);
    }

    private long getDiferencaEmSegundosDataExpiracaoEDataAtual(LocalDateTime expiracao) {
        return LocalDateTime.now().until(expiracao, ChronoUnit.SECONDS);
    }

}
