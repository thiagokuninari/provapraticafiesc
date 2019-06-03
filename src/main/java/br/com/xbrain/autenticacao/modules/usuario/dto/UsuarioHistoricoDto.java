package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioHistoricoDto {

    private Integer id;
    private String motivo;
    private String observacao;
    private String cadastro;
    private LocalDate feriasInicio;
    private LocalDate feriasFim;

    public static UsuarioHistoricoDto of(UsuarioHistorico historico) {
        return UsuarioHistoricoDto
                .builder()
                .id(historico.getId())
                .motivo(historico.getMotivoInativacao().getDescricao())
                .observacao(historico.getObservacao())
                .cadastro(historico.getDataCadastro().toString())
                .feriasInicio(!ObjectUtils.isEmpty(historico.getFerias())
                        ? historico.getFerias().getInicio()
                        : null)
                .feriasFim(!ObjectUtils.isEmpty(historico.getFerias())
                        ? historico.getFerias().getFim()
                        : null)
                .build();
    }
}
