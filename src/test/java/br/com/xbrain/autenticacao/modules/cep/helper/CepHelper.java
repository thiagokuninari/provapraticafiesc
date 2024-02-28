package br.com.xbrain.autenticacao.modules.cep.helper;

import br.com.xbrain.autenticacao.modules.cep.dto.ConsultaCepResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeUfResponse;

public class CepHelper {

    public static ConsultaCepResponse umConsultaCepResponse() {
        return ConsultaCepResponse.builder()
            .cep("86023112")
            .nomeCompleto("teste")
            .bairro("teste bairro")
            .cidade("LONDRINA")
            .uf("PR")
            .build();
    }

    public static CidadeUfResponse umCidadeUfResponse() {
        return CidadeUfResponse.builder()
            .cidadeId(1111)
            .cidade("LONDRINA")
            .uf("PARANA")
            .ufSigla("PR")
            .ufId(1)
            .bairro("teste bairro")
            .logradouro("teste")
            .build();
    }
}
