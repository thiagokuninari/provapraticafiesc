package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizacaoEmpresaRequest {

    @NotNull
    private String nome;
    @NotNull
    private Integer nivelId;
    @NotNull
    private String codigo;
    private ECanal canal;

    private ESituacaoOrganizacaoEmpresa situacao;

}
