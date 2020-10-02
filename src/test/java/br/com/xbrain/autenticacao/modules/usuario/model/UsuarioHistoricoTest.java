package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import org.assertj.core.groups.Tuple;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioHistoricoTest {

    @Test
    public void gerarHistorico_deveGerarHistoricoDosUsuarios() {
        assertThat(UsuarioHistorico.gerarHistorico(List.of(100, 101), "OBSERVACAO TESTE", ESituacao.A))
            .extracting("usuario.id", "observacao", "situacao")
            .containsExactly(
                Tuple.tuple(100, "OBSERVACAO TESTE", ESituacao.A),
                Tuple.tuple(101, "OBSERVACAO TESTE", ESituacao.A));
    }
}