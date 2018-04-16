package br.com.xbrain.autenticacao.modules.parceirosonline.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import lombok.Data;

import java.io.Serializable;

@Data
public class AgenteAutorizadoResponse implements Serializable {

    private String id;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private Eboolean nacional;
}
