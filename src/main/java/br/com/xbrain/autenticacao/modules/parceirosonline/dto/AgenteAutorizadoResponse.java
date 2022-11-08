package br.com.xbrain.autenticacao.modules.parceirosonline.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgenteAutorizadoResponse implements Serializable {

    private String id;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private Eboolean nacional;
    private Integer discadoraId;
    private String situacao;
}
