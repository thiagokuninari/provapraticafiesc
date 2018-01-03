package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class UsuarioInativacaoDto {

    @NotNull
    private Integer idUsuario;
    @NotNull
    private Integer idMotivoInativacao;
    @NotNull
    private LocalDateTime dataCadastro;
    @Size(max = 250)
    private String observacao;

}
