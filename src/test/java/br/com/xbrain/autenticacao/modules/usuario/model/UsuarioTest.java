package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UsuarioTest {

    @Test
    public void isUsuarioEquipeVendas_deveRetornarTrue_quandoForVendedor() {
        assertTrue(umUsuarioComCargo(CodigoCargo.VENDEDOR_OPERACAO)
                .isUsuarioEquipeVendas());
    }

    @Test
    public void isUsuarioEquipeVendas_deveRetornarTrue_quandoForSupervisor() {
        assertTrue(umUsuarioComCargo(CodigoCargo.SUPERVISOR_OPERACAO)
                .isUsuarioEquipeVendas());
    }

    @Test
    public void isUsuarioEquipeVendas_deveRetornarTrue_quandoForAssistente() {
        assertTrue(umUsuarioComCargo(CodigoCargo.ASSISTENTE_OPERACAO)
                .isUsuarioEquipeVendas());
    }

    @Test
    public void isUsuarioEquipeVendas_deveRetornarFalse_quandoForGerenteCordenadorOuDiretor() {
        assertFalse(umUsuarioComCargo(CodigoCargo.GERENTE_OPERACAO)
                .isUsuarioEquipeVendas());

        assertFalse(umUsuarioComCargo(CodigoCargo.DIRETOR_OPERACAO)
                .isUsuarioEquipeVendas());

        assertFalse(umUsuarioComCargo(CodigoCargo.COORDENADOR_OPERACAO)
                .isUsuarioEquipeVendas());
    }

    private Usuario umUsuarioComCargo(CodigoCargo codigoCargo) {
        return Usuario.builder()
                .cargo(Cargo
                        .builder()
                        .codigo(codigoCargo)
                        .build())
                .build();
    }
}
