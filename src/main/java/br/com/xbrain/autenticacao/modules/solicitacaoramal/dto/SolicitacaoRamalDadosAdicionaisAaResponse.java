package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SolicitacaoRamalDadosAdicionaisAaResponse {

    private String discadora;
    private String socioPrincipal;
    private long usuariosAtivos;
    private long quantidadeRamais;

    public static SolicitacaoRamalDadosAdicionaisAaResponse convertFrom(String nomeDiscadora, String nomeSocioPrincipal,
                                                                 long qtdUsuariosAtivos, long qtdRamais) {
        return SolicitacaoRamalDadosAdicionaisAaResponse.builder()
                .discadora(nomeDiscadora)
                .socioPrincipal(nomeSocioPrincipal)
                .usuariosAtivos(qtdUsuariosAtivos)
                .quantidadeRamais(qtdRamais)
                .build();
    }
}
