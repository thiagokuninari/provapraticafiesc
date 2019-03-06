package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SolicitacaoRamalDadosAdicionaisAaResponse {

    private String discadora;
    private String socioPrincipal;
    private long usuariosAtivos;
    private long quantidadeRamais;
    private String agenteAutorizadoRazaoSocial;

    public static SolicitacaoRamalDadosAdicionaisAaResponse convertFrom(String nomeDiscadora, String nomeSocioPrincipal,
                                                                        long qtdUsuariosAtivos, long qtdRamais,
                                                                        AgenteAutorizadoResponse agenteAutorizadoResponse) {
        return SolicitacaoRamalDadosAdicionaisAaResponse.builder()
                .discadora(nomeDiscadora)
                .socioPrincipal(nomeSocioPrincipal)
                .usuariosAtivos(qtdUsuariosAtivos)
                .quantidadeRamais(qtdRamais)
                .agenteAutorizadoRazaoSocial(agenteAutorizadoResponse.getRazaoSocial())
                .build();
    }
}
