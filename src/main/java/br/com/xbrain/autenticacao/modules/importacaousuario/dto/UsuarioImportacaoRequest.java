package br.com.xbrain.autenticacao.modules.importacaousuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioImportacaoRequest {

    private boolean senhaPadrao;
    private boolean resetarSenhaUsuarioSalvo;

}
