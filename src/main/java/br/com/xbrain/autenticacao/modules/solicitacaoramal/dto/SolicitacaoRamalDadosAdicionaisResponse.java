package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.AgenteAutorizadoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitacaoRamalDadosAdicionaisResponse {

    private String discadora;
    private String socioPrincipal;
    private long usuariosAtivos;
    private long quantidadeRamais;
    private String agenteAutorizadoRazaoSocial;
    private String nome;
    private Integer discadoraId;

    public static SolicitacaoRamalDadosAdicionaisResponse convertFrom(String nomeDiscadora, String nomeSocioPrincipal,
                                                                      long qtdUsuariosAtivos, long qtdRamais,
                                                                      AgenteAutorizadoResponse agenteAutorizadoResponse) {
        return SolicitacaoRamalDadosAdicionaisResponse.builder()
                .discadora(nomeDiscadora)
                .socioPrincipal(nomeSocioPrincipal)
                .usuariosAtivos(qtdUsuariosAtivos)
                .quantidadeRamais(qtdRamais)
                .agenteAutorizadoRazaoSocial(agenteAutorizadoResponse.getRazaoSocial())
                .build();
    }

    public static SolicitacaoRamalDadosAdicionaisResponse convertFrom(String nome, Integer discadoraId) {

        return SolicitacaoRamalDadosAdicionaisResponse.builder()
            .nome(nome)
            .discadoraId(discadoraId)
            .build();
    }
}
