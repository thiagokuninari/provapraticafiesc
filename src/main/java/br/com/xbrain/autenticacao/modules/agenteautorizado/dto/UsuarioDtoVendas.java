package br.com.xbrain.autenticacao.modules.agenteautorizado.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDtoVendas {

    private Integer id;
    private String nome;
    private String email;
    private ESituacao situacao;
    private Integer agenteAutorizadoId;
    private String agenteAutorizadoCnpj;
    private String agenteAutorizadoRazaoSocial;
}
