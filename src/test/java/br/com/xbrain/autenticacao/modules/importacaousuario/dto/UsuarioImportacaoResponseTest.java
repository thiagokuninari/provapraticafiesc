package br.com.xbrain.autenticacao.modules.importacaousuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import org.assertj.core.groups.Tuple;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioImportacaoResponseTest {

    @Test
    public void of_deveRetonarUsuarioImportacaoResponse_quandoSolicitado() {
        assertThat(UsuarioImportacaoResponse.of(umUsuarioImportacaoPlanilha("nome")))
            .extracting("nome", "cpf", "email", "telefone", "motivoNaoImportacao",
                "usuarioImportadoComSucesso", "departamento.codigo", "departamento.nome", "nivel.codigo",
                "nivel.nome", "cargo.codigo", "cargo.nome", "nascimento")
            .containsExactly("nome", "000.111.222-85", "email@xbrain.com", "(43) 3378-0098",
                List.of("motivo 1", "motivo 2"), "NÃO", CodigoDepartamento.ADMINISTRADOR, "Financeiro",
                CodigoNivel.OPERACAO, "OPERACAO", CodigoCargo.ADMINISTRADOR, "ADM",
                LocalDateTime.of(1990, 1, 1, 0, 0, 0));
    }

    @Test
    public void of_deveRetonarListaUsuarioImportacaoResponse_quandoSolicitado() {
        assertThat(UsuarioImportacaoResponse.of(List.of(
            umUsuarioImportacaoPlanilha("nome 1"),
            umUsuarioImportacaoPlanilha("nome 2"))))
            .extracting("nome", "cpf", "email", "telefone", "motivoNaoImportacao",
                "usuarioImportadoComSucesso", "departamento.codigo", "departamento.nome", "nivel.codigo",
                "nivel.nome", "cargo.codigo", "cargo.nome", "nascimento")
            .containsExactly(
                Tuple.tuple("nome 1", "000.111.222-85", "email@xbrain.com", "(43) 3378-0098",
                    List.of("motivo 1", "motivo 2"), "NÃO", CodigoDepartamento.ADMINISTRADOR, "Financeiro",
                    CodigoNivel.OPERACAO, "OPERACAO", CodigoCargo.ADMINISTRADOR, "ADM",
                    LocalDateTime.of(1990, 1, 1, 0, 0, 0)),
                Tuple.tuple("nome 2", "000.111.222-85", "email@xbrain.com", "(43) 3378-0098",
                    List.of("motivo 1", "motivo 2"), "NÃO", CodigoDepartamento.ADMINISTRADOR, "Financeiro",
                    CodigoNivel.OPERACAO, "OPERACAO", CodigoCargo.ADMINISTRADOR, "ADM",
                    LocalDateTime.of(1990, 1, 1, 0, 0, 0)));
    }

    private UsuarioImportacaoPlanilha umUsuarioImportacaoPlanilha(String nome) {
        return UsuarioImportacaoPlanilha.builder()
            .nome(nome)
            .cpf("000.111.222-85")
            .email("email@xbrain.com")
            .telefone("(43) 3378-0098")
            .motivoNaoImportacao(List.of("motivo 1", "motivo 2"))
            .departamento(Departamento.builder()
                .codigo(CodigoDepartamento.ADMINISTRADOR)
                .nome("Financeiro")
                .build())
            .nivel(Nivel.builder()
                .codigo(CodigoNivel.OPERACAO)
                .nome("OPERACAO")
                .build())
            .cargo(Cargo.builder()
                .codigo(CodigoCargo.ADMINISTRADOR)
                .nome("ADM")
                .build())
            .nascimento(LocalDateTime.of(1990, 1, 1, 0, 0, 0))
            .build();
    }
}
