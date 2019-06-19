package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private String usuarioAlteracao;

    public static UsuarioHistoricoDto of(UsuarioHistorico historico) {
        return UsuarioHistoricoDto
                .builder()
                .id(historico.getId())
                .situacao(historico.getSituacaoComMotivo())
                .observacao(historico.getObservacao())
                .cadastro(historico.getDataCadastro())
                .feriasInicio(!ObjectUtils.isEmpty(historico.getFerias())
                        ? historico.getFerias().getInicio()
                        : null)
                .feriasFim(!ObjectUtils.isEmpty(historico.getFerias())
                        ? historico.getFerias().getFim()
                        : null)
                .usuarioAlteracao(historico.getUsuarioAlteracao().getNome())
                .build();
    }
}
