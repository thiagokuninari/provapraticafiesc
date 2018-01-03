package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UsuarioAtivacaoDto {

    @NotNull
    private Integer idUsuario;
    @Size(max = 250)
    private String observacao;

}
