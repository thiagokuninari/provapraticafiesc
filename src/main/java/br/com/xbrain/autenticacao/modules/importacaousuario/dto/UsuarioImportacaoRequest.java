package br.com.xbrain.autenticacao.modules.importacaousuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioImportacaoRequest {

    private boolean senhaPadrao;

    public UsuarioImportacaoRequest() {
    }

}
