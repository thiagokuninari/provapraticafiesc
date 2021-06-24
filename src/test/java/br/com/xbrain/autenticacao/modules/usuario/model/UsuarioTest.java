package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UsuarioTest {

    private static Cargo umCargo(CodigoCargo codigoCargo) {
        return Cargo
            .builder()
            .codigo(codigoCargo)
            .build();
    }

    private static Usuario umUsuarioComCargo(CodigoCargo codigoCargo) {
        return Usuario
            .builder()
            .cargo(umCargo(codigoCargo))
            .build();
    }

    private static Usuario umUsuarioComCargo(Integer id, CodigoCargo codigoCargo) {
        return Usuario.builder()
            .id(id)
            .cargo(Cargo
                .builder()
                .codigo(codigoCargo)
                .build())
            .build();
    }

    private static Usuario umUsuarioComLoginNetSales(String loginNetSales) {
        return Usuario
            .builder()
            .loginNetSales(loginNetSales)
            .build();
    }

    private static UsuarioAutenticado umUsuarioAutenticado(Integer id, CodigoNivel codigoNivel, CodigoCargo codigoCargo) {
        return UsuarioAutenticado
            .builder()
            .id(id)
            .nivelCodigo(codigoNivel.name())
            .usuario(umUsuarioComCargo(codigoCargo))
            .build();
    }

    @Test
    public void isUsuarioEquipeVendas_deveRetornarTrue_quandoForVendedor() {
        assertTrue(umUsuarioComCargo(1, CodigoCargo.VENDEDOR_OPERACAO)
            .isUsuarioEquipeVendas());
    }

    @Test
    public void isUsuarioEquipeVendas_deveRetornarTrue_quandoForSupervisor() {
        assertTrue(umUsuarioComCargo(1, CodigoCargo.SUPERVISOR_OPERACAO)
            .isUsuarioEquipeVendas());
    }

    @Test
    public void isUsuarioEquipeVendas_deveRetornarTrue_quandoForAssistente() {
        assertTrue(umUsuarioComCargo(1, CodigoCargo.ASSISTENTE_OPERACAO)
            .isUsuarioEquipeVendas());
    }

    @Test
    public void isUsuarioEquipeVendas_deveRetornarFalse_quandoForGerenteCordenadorOuDiretor() {
        assertFalse(umUsuarioComCargo(1, CodigoCargo.GERENTE_OPERACAO)
            .isUsuarioEquipeVendas());

        assertFalse(umUsuarioComCargo(1, CodigoCargo.DIRETOR_OPERACAO)
            .isUsuarioEquipeVendas());

        assertFalse(umUsuarioComCargo(1, CodigoCargo.COORDENADOR_OPERACAO)
            .isUsuarioEquipeVendas());
    }

    @Test
    public void permiteEditar_deveRetornarTrue_quandoForAdmin() {
        assertTrue(umUsuarioComCargo(1, CodigoCargo.GERENTE_OPERACAO)
            .permiteEditar(umUsuarioAutenticado(1, CodigoNivel.XBRAIN, CodigoCargo.ADMINISTRADOR)));

    }

    @Test
    public void permiteEditar_deveRetornarFalse_quandoOUsuarioEditadoForOMesmoDoAutenticado() {
        assertFalse(umUsuarioComCargo(1, CodigoCargo.GERENTE_OPERACAO)
            .permiteEditar(umUsuarioAutenticado(1, CodigoNivel.OPERACAO, CodigoCargo.GERENTE_OPERACAO)));
    }

    @Test
    public void permiteEditar_deveRetornarFalse_quandoOUsuarioAutenticadoEhDaEquipeDeVendasEOEditadoNaoForVendedor() {
        assertFalse(umUsuarioComCargo(1, CodigoCargo.SUPERVISOR_OPERACAO)
            .permiteEditar(umUsuarioAutenticado(1, CodigoNivel.OPERACAO, CodigoCargo.SUPERVISOR_OPERACAO)));
    }

    @Test
    public void permiteEditar_deveRetornarTrue_quandoOUsuarioAutenticadoEhDaEquipeDeVendasEOEditadoNaoForVendedor() {
        assertTrue(umUsuarioComCargo(1, CodigoCargo.VENDEDOR_OPERACAO)
            .permiteEditar(umUsuarioAutenticado(1, CodigoNivel.OPERACAO, CodigoCargo.SUPERVISOR_OPERACAO)));
    }

    @Test
    public void hasLoginNetSales_deveRetornarFalse_seUsuarioPossuirLoginNetSalesNulo() {
        assertThat(umUsuarioComLoginNetSales(null).hasLoginNetSales()).isFalse();
    }

    @Test
    public void hasLoginNetSales_deveRetornarFalse_seUsuarioPossuirLoginNetSalesVazio() {
        assertThat(umUsuarioComLoginNetSales("").hasLoginNetSales()).isFalse();
    }

    @Test
    public void hasLoginNetSales_deveRetornarTrue_seUsuarioPossuirLoginNetSales() {
        assertThat(umUsuarioComLoginNetSales("login").hasLoginNetSales()).isTrue();
    }
}
