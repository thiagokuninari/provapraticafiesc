package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import lombok.Data;

@Data
public class UsuarioDadosAcessoRequest {

    private Integer usuarioId;
    private String emailAtual;
    private String emailNovo;
    private String senhaAtual;
    private String senhaNova;
    private Eboolean alterarSenha;
    private Boolean ignorarSenhaAtual;
}
