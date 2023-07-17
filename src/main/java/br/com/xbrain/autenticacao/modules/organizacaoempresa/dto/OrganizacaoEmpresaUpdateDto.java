package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class OrganizacaoEmpresaUpdateDto implements Serializable {

    private String organizacaoNome;
    private String organizacaoNomeAtualizado;
    private Integer nivelId;
}
