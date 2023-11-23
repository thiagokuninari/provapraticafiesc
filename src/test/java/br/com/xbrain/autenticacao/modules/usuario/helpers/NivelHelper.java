package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;

import java.util.List;

public class NivelHelper {

    public static Nivel umNivelOperacao() {
        return Nivel.builder()
            .id(1)
            .nome("Operação")
            .codigo(CodigoNivel.OPERACAO)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

    public static Nivel umNivelMso() {
        return Nivel.builder()
            .id(2)
            .nome("MSO")
            .codigo(CodigoNivel.MSO)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

    public static Nivel umNivelAa() {
        return Nivel.builder()
            .id(3)
            .nome("Agente Autorizado")
            .codigo(CodigoNivel.AGENTE_AUTORIZADO)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.F)
            .build();
    }

    public static Nivel umNivelXbrain() {
        return Nivel.builder()
            .id(4)
            .nome("X-BRAIN")
            .codigo(CodigoNivel.XBRAIN)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

    public static Nivel umNivelVarejo() {
        return Nivel.builder()
            .id(5)
            .nome("Varejo")
            .codigo(CodigoNivel.VAREJO)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

    public static Nivel umNivelAtp() {
        return Nivel.builder()
            .id(6)
            .nome("Atendimento Pessoal")
            .codigo(CodigoNivel.ATP)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.F)
            .build();
    }

    public static Nivel umNivelLojas() {
        return Nivel.builder()
            .id(7)
            .nome("Lojas")
            .codigo(CodigoNivel.LOJAS)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

    public static Nivel umNivelReceptivo() {
        return Nivel.builder()
            .id(8)
            .nome("Receptivo")
            .codigo(CodigoNivel.RECEPTIVO)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.F)
            .build();
    }

    public static Nivel umNivelAtivoLocalProprio() {
        return Nivel.builder()
            .id(9)
            .nome("Ativo Local Proprio")
            .codigo(CodigoNivel.ATIVO_LOCAL_PROPRIO)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

    public static Nivel umNivelAtivoLocalTerceiro() {
        return Nivel.builder()
            .id(10)
            .nome("Ativo Local Terceiro")
            .codigo(CodigoNivel.ATIVO_LOCAL_TERCEIRO)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

    public static Nivel umNivelAtivoNacionalTerceiro() {
        return Nivel.builder()
            .id(11)
            .nome("Ativo Nacional Terceiro")
            .codigo(CodigoNivel.ATIVO_NACIONAL_TERCEIRO)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

    public static Nivel umNivelAtivoNacionalTerceiroSegmentado() {
        return Nivel.builder()
            .id(12)
            .nome("Ativo Nacional Terceiro Segmentado")
            .codigo(CodigoNivel.ATIVO_NACIONAL_TERCEIRO_SEGMENTADO)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

    public static Nivel umNivelAtivoRentabilizacao() {
        return Nivel.builder()
            .id(13)
            .nome("Ativo Rentabilização")
            .codigo(CodigoNivel.ATIVO_RENTABILIZACAO)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

    public static Nivel umNivelOuvidoria() {
        return Nivel.builder()
            .id(15)
            .nome("Ouvidoria")
            .codigo(CodigoNivel.OUVIDORIA)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

    public static Nivel umNivelAtivoLocalColaborador() {
        return Nivel.builder()
            .id(16)
            .nome("Ativo Local Colaborador")
            .codigo(CodigoNivel.ATP)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

    public static Nivel umNivelBko() {
        return Nivel.builder()
            .id(18)
            .nome("Backoffice")
            .codigo(CodigoNivel.BACKOFFICE)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

    public static Nivel umNivelBkoCentralizado() {
        return Nivel.builder()
            .id(19)
            .nome("Backoffice Centralizado")
            .codigo(CodigoNivel.BACKOFFICE_CENTRALIZADO)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

    public static Nivel umNivel() {
        return Nivel.builder()
            .id(200)
            .nome("Nivel teste")
            .codigo(CodigoNivel.MSO)
            .situacao(ESituacao.A)
            .exibirCadastroUsuario(Eboolean.V)
            .build();
    }

    public static List<Nivel> umaListaDeNiveis() {
        return List.of(
            umNivelAtp(),
            umNivelAtivoLocalColaborador(),
            umNivelAtivoLocalProprio(),
            umNivelAtivoLocalTerceiro(),
            umNivelAtivoNacionalTerceiro(),
            umNivelAtivoNacionalTerceiroSegmentado(),
            umNivelAtivoRentabilizacao(),
            umNivelBko(),
            umNivelBkoCentralizado(),
            umNivelLojas(),
            umNivelMso(),
            umNivelOperacao(),
            umNivelOuvidoria(),
            umNivelReceptivo()
        );
    }

    public static List<Nivel> umaListaComNiveisReceptivoBkoEOperacao() {
        return List.of(
            umNivelReceptivo(),
            umNivelBko(),
            umNivelOperacao()
            );
    }
}
