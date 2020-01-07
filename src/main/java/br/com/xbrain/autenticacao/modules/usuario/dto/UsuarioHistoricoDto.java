package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.util.ObjectUtils.isEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioHistoricoDto {

    private Integer id;
    private String situacao;
    private String observacao;
    private LocalDateTime cadastro;
    private LocalDate feriasInicio;
    private LocalDate feriasFim;
    private LocalDate afastamentoInicio;
    private LocalDate afastamentoFim;
    private String usuarioAlteracao;

    public static UsuarioHistoricoDto of(UsuarioHistorico historico) {
        return UsuarioHistoricoDto
                .builder()
                .id(historico.getId())
                .situacao(historico.getSituacaoComMotivo())
                .observacao(historico.getObservacao())
                .cadastro(historico.getDataCadastro())
                .feriasInicio(getFeriasInicio(historico))
                .feriasFim(getFeriasFim(historico))
                .afastamentoInicio(getAfastamentoInicio(historico))
                .afastamentoFim(getAfastamentoFim(historico))
                .usuarioAlteracao(historico.getUsuarioAlteracao().getNome())
                .build();
    }

    public static LocalDate getFeriasInicio(UsuarioHistorico historico) {
        return !isEmpty(historico.getFerias())
                ? historico.getFerias().getInicio()
                : null;
    }

    public static LocalDate getFeriasFim(UsuarioHistorico historico) {
        return !isEmpty(historico.getFerias())
                ? historico.getFerias().getFim()
                : null;
    }

    public static LocalDate getAfastamentoInicio(UsuarioHistorico historico) {
        return !isEmpty(historico.getAfastamento())
                ? historico.getAfastamento().getInicio()
                : null;
    }

    public static LocalDate getAfastamentoFim(UsuarioHistorico historico) {
        return !isEmpty(historico.getAfastamento())
                ? historico.getAfastamento().getFim()
                : null;
    }
}
