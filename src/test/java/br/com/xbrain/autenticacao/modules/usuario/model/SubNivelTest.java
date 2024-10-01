package br.com.xbrain.autenticacao.modules.usuario.model;

import org.junit.Test;

import java.util.Set;

import static br.com.xbrain.autenticacao.modules.permissao.helper.FuncionalidadeHelper.umaFuncionalidadeBko;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.umCargoMsoAnalista;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.umCargoMsoConsultor;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umCargoFuncionalidadeSubNivel;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umSubNivel;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("LineLength")
public class SubNivelTest {

    @Test
    public void getFuncionalidadesIds_deveRetornarOsIdsDasFuncionalidades_quandoSolicitado() {
        var subNivel = umSubNivel(1, "BACKOFFICE", "BACKOFFICE",
            Set.of(umCargoFuncionalidadeSubNivel(1, null, umaFuncionalidadeBko(1, "Teste 1"))));
        assertThat(subNivel.getFuncionalidadesIds())
            .containsExactly(1);
    }

    @Test
    public void getFuncionalidadesIdsByCargoId_deveRetornarOsIdsDasFuncionalidadesFiltradosPeloCargo_quandoPossuirApenasPermissoesComCargo() {
        var subNivel = umSubNivel(1, "BACKOFFICE", "BACKOFFICE",
            Set.of(umCargoFuncionalidadeSubNivel(1, umCargoMsoConsultor(), umaFuncionalidadeBko(1, "Teste 1")),
                umCargoFuncionalidadeSubNivel(2, umCargoMsoConsultor(), umaFuncionalidadeBko(2, "Teste 2")))
        );
        assertThat(subNivel.getFuncionalidadesIdsByCargoId(22))
            .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    public void getFuncionalidadesIdsByCargoId_deveRetornarOsIdsDasFuncionalidadesSemNenhumCargoEspecifico_quandoPossuirApenasPermissoesSemCargoEspecifico() {
        var subNivel = umSubNivel(1, "BACKOFFICE", "BACKOFFICE",
            Set.of(umCargoFuncionalidadeSubNivel(1, null, umaFuncionalidadeBko(1, "Teste 1")),
                umCargoFuncionalidadeSubNivel(2, null, umaFuncionalidadeBko(2, "Teste 2")),
                umCargoFuncionalidadeSubNivel(3, null, umaFuncionalidadeBko(3, "Teste 3")),
                umCargoFuncionalidadeSubNivel(4, null, umaFuncionalidadeBko(4, "Teste 4"))
            ));
        assertThat(subNivel.getFuncionalidadesIdsByCargoId(22))
            .containsExactlyInAnyOrder(1, 2, 3, 4);
    }

    @Test
    public void getFuncionalidadesIdsByCargoId_deveRetornarOsIdsDasFuncionalidadesSemNenhumCargoEspecificoOuComUmCargoEspecifico_quandoCargoEspecificoCorresponder() {
        var subNivel = umSubNivel(1, "BACKOFFICE", "BACKOFFICE",
            Set.of(umCargoFuncionalidadeSubNivel(1, umCargoMsoConsultor(), umaFuncionalidadeBko(1, "Teste 1")),
                umCargoFuncionalidadeSubNivel(2, null, umaFuncionalidadeBko(2, "Teste 2")),
                umCargoFuncionalidadeSubNivel(3, null, umaFuncionalidadeBko(3, "Teste 3")),
                umCargoFuncionalidadeSubNivel(4, umCargoMsoAnalista(), umaFuncionalidadeBko(4, "Teste 4"))
            ));
        assertThat(subNivel.getFuncionalidadesIdsByCargoId(22))
            .containsExactlyInAnyOrder(1, 2, 3);
    }
}
