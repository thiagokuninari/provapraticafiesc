package br.com.xbrain.autenticacao.modules.cep.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaCepResponse {
    private String cep;
    private String nomeCompleto;
    private String bairro;
    private String cidade;
    private String uf;
    private Eboolean cepUnicoPorCidade;
}
