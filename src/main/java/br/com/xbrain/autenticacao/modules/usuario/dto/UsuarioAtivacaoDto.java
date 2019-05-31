package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioAtivacaoDto {

    @NotNull
    private Integer idUsuario;
    @Size(max = 250)
    private String observacao;
    private Integer idUsuarioAtivacao;

}
