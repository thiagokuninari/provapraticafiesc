package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.permissao.dto.CargoDepartamentoFuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.model.Aplicacao;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioPermissaoResponseTest {

    @Test
    public void of_deveRetornarUsuarioPermissaoResponse_quandoSolicitado() {
        assertThat(UsuarioPermissaoResponse.of(List.of(umCargoDepartamentoFuncionalidade()),
            List.of(umaFuncionalidade())))
            .extracting("permissoesCargoDepartamento", "permissoesEspeciais")
            .containsExactly(List.of(umCargoDepartamentoFuncionalidadeResponse()), List.of(umaFuncionalidadeResponse()));
    }

    private CargoDepartamentoFuncionalidade umCargoDepartamentoFuncionalidade() {
        var cargo = new CargoDepartamentoFuncionalidade();
        cargo.setCargo(Cargo.builder()
            .nome("cargo")
            .nivel(Nivel.builder()
                .nome("nivel")
                .build())
            .build());
        cargo.setDepartamento(Departamento.builder()
            .nome("departamento")
            .build());
        cargo.setFuncionalidade(Funcionalidade.builder()
            .id(1)
            .role("role")
            .aplicacao(Aplicacao.builder()
                .nome("aplicacao")
                .build())
            .build());
        return cargo;
    }

    private Funcionalidade umaFuncionalidade() {
        var funcionalidade = new Funcionalidade();
        funcionalidade.setId(2);
        funcionalidade.setAplicacao(Aplicacao.builder()
            .nome("aplicacao da funcionalidade")
            .build());

        return funcionalidade;
    }

    private CargoDepartamentoFuncionalidadeResponse umCargoDepartamentoFuncionalidadeResponse() {
        var response = new CargoDepartamentoFuncionalidadeResponse();
        response.setNivelNome("nivel");
        response.setCargoNome("cargo");
        response.setDepartamentoNome("departamento");
        response.setFuncionalidadeId(1);
        response.setFuncionalidadeRole("role");
        response.setAplicacaoNome("aplicacao");

        return response;
    }

    private FuncionalidadeResponse umaFuncionalidadeResponse() {
        var response = new FuncionalidadeResponse();
        response.setId(2);
        response.setFuncionalidadeId(2);
        response.setAplicacao("aplicacao da funcionalidade");
        return response;
    }
}
