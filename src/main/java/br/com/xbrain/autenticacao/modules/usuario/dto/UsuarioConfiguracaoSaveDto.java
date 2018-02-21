package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UsuarioConfiguracaoSaveDto {

    @NotNull
    private Integer usuarioId;

    @NotNull
    private Integer ramal;

    public UsuarioConfiguracaoSaveDto() {
    }

    public UsuarioConfiguracaoSaveDto(Integer usuarioId, Integer ramal) {
        this.usuarioId = usuarioId;
        this.ramal = ramal;
    }
}
