package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.NivelHelper.*;

public class DepartamentoHelper {

    public static Departamento umDepartamento(Integer id, String nome) {
        return Departamento.builder()
            .id(id)
            .nome(nome)
            .nivel(umNivel())
            .situacao(ESituacao.A)
            .codigo(CodigoDepartamento.COMERCIAL)
            .build();
    }

    public static List<Departamento> umaListaDepartamentos() {
        return List.of(umDepartamento(1, "Departamento 1"), umDepartamento(2, "Departamento 2"));
    }

    public static Departamento umDepartamentoAa() {
        return Departamento.builder()
            .id(40)
            .nome("Agente Autorizado")
            .nivel(umNivelAa())
            .codigo(CodigoDepartamento.AGENTE_AUTORIZADO)
            .situacao(ESituacao.A)
            .build();
    }

    public static Departamento umDepartamentoComercial() {
        return Departamento.builder()
            .id(57)
            .nome("Comercial")
            .nivel(umNivelReceptivo())
            .codigo(CodigoDepartamento.COMERCIAL)
            .situacao(ESituacao.A)
            .build();
    }

    public static Departamento umDepartamentoAdministrador() {
        return Departamento.builder()
            .id(50)
            .nome("Administrador")
            .nivel(umNivelXbrain())
            .codigo(CodigoDepartamento.ADMINISTRADOR)
            .situacao(ESituacao.A)
            .build();
    }

    public static Departamento umDepartamentoHelpDesk() {
        return Departamento.builder()
            .id(51)
            .nome("HelpDesk")
            .nivel(umNivelXbrain())
            .codigo(CodigoDepartamento.HELP_DESK)
            .situacao(ESituacao.A)
            .build();
    }

    public static Departamento umDepartamentoEndomarketing() {
        return Departamento.builder()
            .id(5)
            .nome("Endomarketing")
            .nivel(umNivelOperacao())
            .codigo(CodigoDepartamento.ENDOMARKETING)
            .situacao(ESituacao.A)
            .build();
    }

    public static Departamento umDepartamentoAdministrativoMso() {
        return Departamento.builder()
            .id(20)
            .nome("Administrativo")
            .nivel(umNivelMso())
            .codigo(CodigoDepartamento.ADMINISTRATIVO)
            .situacao(ESituacao.A)
            .build();
    }

    public static Departamento umDepartamentoAdministrativo() {
        return Departamento.builder()
            .id(1)
            .nome("Administrativo")
            .nivel(umNivelOperacao())
            .situacao(ESituacao.A)
            .build();
    }
}
