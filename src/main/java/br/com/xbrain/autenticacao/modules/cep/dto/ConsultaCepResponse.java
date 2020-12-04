package br.com.xbrain.autenticacao.modules.cep.dto;

import lombok.Data;

@Data
public class ConsultaCepResponse {
    private String cep;
    private String nomeCompleto;
    private String bairro;
    private String cidade;
    private String uf;
}
