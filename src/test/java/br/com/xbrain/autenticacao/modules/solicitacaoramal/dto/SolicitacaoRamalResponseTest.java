package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SolicitacaoRamalResponseTest {

    @Test
    public void convertFrom_deveRetornarSolicitacaoRamalResponseSemColaboradores_quandoUsuariosNull() {
        assertThat(SolicitacaoRamalResponse.convertFrom(umaSolicitacaoRamal()).getColaboradores())
            .isEqualTo(List.of());
    }

    private SolicitacaoRamal umaSolicitacaoRamal() {
        return SolicitacaoRamal.builder()
            .usuario(Usuario.builder().nome("nome").build())
            .usuariosSolicitados(null)
            .build();
    }
}
