package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.ECodigoObservacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColaboradorInativacaoPolRequest {

    private String email;
    private ECodigoObservacao codigo;

    public static ColaboradorInativacaoPolRequest of(String email, ECodigoObservacao codigo) {
        return ColaboradorInativacaoPolRequest.builder()
            .email(email)
            .codigo(codigo)
            .build();
    }
}
