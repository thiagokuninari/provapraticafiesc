package br.com.xbrain.autenticacao.modules.notificacao.dto;

import lombok.Data;

import java.util.List;

@Data
public class BoaVindaAgenteAutorizadoRequest {

    private Integer agenteAutorizadoId;
    private String agenteAutorizadoRazaoSocial;
    private List<String> emails;
    private String link;
    private String senha;

}
