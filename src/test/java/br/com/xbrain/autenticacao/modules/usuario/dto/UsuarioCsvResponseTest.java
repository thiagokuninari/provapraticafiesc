package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Canal;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioCsvResponseTest {

    @Test
    public void getCabecalhoCsv_deveRetornarCabecalho_quandoSolicitado() {
        assertThat(UsuarioCsvResponse.getCabecalhoCsv())
            .isEqualTo("CODIGO;NOME;EMAIL;TELEFONE;CPF;CARGO;DEPARTAMENTO;UNIDADE NEGOCIO;EMPRESA;SITUACAO;"
                + "DATA ULTIMO ACESSO;LOGIN NETSALES;NIVEL;RAZAO SOCIAL;CNPJ;ORGANIZACAO EMPRESA;CANAL;HIERARQUIA\n");
    }

    @Test
    public void toCsv_deveRetornarLinhas_quandoSolicitado() {
        assertThat(umUsuarioCsvResponse().toCsv())
            .isEqualTo("1;nome;email@xbrain.com;99999999999;007.009.887-65;XBRAIN;DEPARTAMENTO;unidade;"
                + "empresas;A;2024-09-08T00:00;login net sales;nivel;razao social;01179675000189;organizacao;"
                + "Agente autorizado;admin");
    }

    @Test
    public void toCsv_deveRetonarLinhasSemDataUltimoAcesso_quandoDataUltimoAcessoNull() {
        var csv = umUsuarioCsvResponse();
        csv.setDataUltimoAcesso(null);
        assertThat(csv.toCsv())
            .isEqualTo("1;nome;email@xbrain.com;99999999999;007.009.887-65;XBRAIN;DEPARTAMENTO;unidade;"
                + "empresas;A;;login net sales;nivel;razao social;01179675000189;organizacao;"
                + "Agente autorizado;admin");
    }

    @Test
    public void toCsv_deveRetonarLinhasSemCanais_quandoCanaisNull() {
        var csv = umUsuarioCsvResponse();
        csv.setCanais(null);
        assertThat(csv.toCsv())
            .isEqualTo("1;nome;email@xbrain.com;99999999999;007.009.887-65;XBRAIN;DEPARTAMENTO;unidade;"
                + "empresas;A;2024-09-08T00:00;login net sales;nivel;razao social;01179675000189;organizacao;"
                + ";admin");
    }

    @Test
    public void of_deveRetornarUsuarioCsvResponse_quandoSolicitado() {
        assertThat(UsuarioCsvResponse.of(umUsuarioCsvResponse(), umAgenteAutorizadoUsuarioDto()))
            .extracting("id", "nome", "email", "telefone", "cpf", "cargo", "departamento",
                "unidadesNegocios", "empresas", "situacao", "dataUltimoAcesso", "loginNetSales", "nivel", "hierarquia",
                "razaoSocial", "cnpj", "organizacaoEmpresa")
            .containsExactly(1, "nome", "email@xbrain.com", "99999999999", "00700988765", "XBRAIN", "DEPARTAMENTO",
                "unidade", "empresas", A, LocalDateTime.of(2024, 9, 8, 0, 0,0),
                "login net sales", "nivel", "admin", "RAZAO SOCIAL DELE", "07718907000167", "organizacao");
    }

    private UsuarioCsvResponse umUsuarioCsvResponse() {
        return UsuarioCsvResponse.builder()
            .id(1)
            .nome("nome")
            .email("email@xbrain.com")
            .telefone("99999999999")
            .cpf("00700988765")
            .cargo("XBRAIN")
            .departamento("DEPARTAMENTO")
            .unidadesNegocios("unidade")
            .empresas("empresas")
            .situacao(A)
            .dataUltimoAcesso(LocalDateTime.of(2024, 9, 8, 0, 0, 0))
            .loginNetSales("login net sales")
            .nivel("nivel")
            .razaoSocial("razao social")
            .cnpj("01179675000189")
            .organizacaoEmpresa("organizacao")
            .canais(List.of(Canal.builder().usuarioId(1).canal(ECanal.AGENTE_AUTORIZADO).build()))
            .hierarquia("admin")
            .build();
    }

    private AgenteAutorizadoUsuarioDto umAgenteAutorizadoUsuarioDto() {
        return AgenteAutorizadoUsuarioDto.builder()
            .razaoSocial("RAZAO SOCIAL DELE")
            .cnpj("07718907000167")
            .build();
    }
}
