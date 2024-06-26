package br.com.xbrain.autenticacao.modules.agenteautorizado.helper;

import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioDtoVendas;

public class UsuarioDtoVendasHelper {

    public static UsuarioDtoVendas umUsuarioDtoVendas(Integer id) {
        return UsuarioDtoVendas
            .builder()
            .id(id)
            .build();
    }

    public static UsuarioDtoVendas umOutroUsuarioDtoVendas() {
        return UsuarioDtoVendas.builder()
            .id(1)
            .email("mso_analistaadm_claromovel_pessoal@net.com.br")
            .agenteAutorizadoCnpj("64.262.572/0001-21")
            .agenteAutorizadoRazaoSocial("Razao Social Teste")
            .agenteAutorizadoId(1)
            .build();
    }
}
