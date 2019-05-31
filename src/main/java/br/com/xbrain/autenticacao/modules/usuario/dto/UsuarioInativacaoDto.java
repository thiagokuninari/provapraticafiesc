package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static org.springframework.util.ObjectUtils.isEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioInativacaoDto {

    @NotNull
    private Integer idUsuario;

    private Integer idMotivoInativacao;

    private Integer idUsuarioInativacao;

    private CodigoMotivoInativacao codigoMotivoInativacao;

    @Size(max = 250)
    private String observacao;

    private String dataInicio;

    private String dataFim;

    @JsonIgnore
    private MotivoInativacao motivoInativacao;

    @JsonIgnore
    public Usuario getUsuarioInativacaoTratado(Integer usuarioAutenticadoId) {
        return !isEmpty(idUsuarioInativacao)
                ? new Usuario(idUsuarioInativacao)
                : new Usuario(usuarioAutenticadoId);
    }

    @JsonIgnore
    public boolean isFerias() {
        return !isEmpty(motivoInativacao)
                && motivoInativacao.getCodigo() == CodigoMotivoInativacao.FERIAS
                && !isEmpty(dataInicio)
                && !isEmpty(dataFim);
    }
}
