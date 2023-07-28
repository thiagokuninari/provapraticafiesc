package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;

import java.util.List;

public class MotivoInativacaoHelper {

    public static MotivoInativacao umMotivoInativacaoFerias() {
        return MotivoInativacao.builder()
            .id(1)
            .descricao("FÉRIAS")
            .codigo(CodigoMotivoInativacao.FERIAS)
            .situacao(ESituacao.A)
            .build();
    }

    public static MotivoInativacao umMotivoInativacaoAfastamento() {
        return MotivoInativacao.builder()
            .id(2)
            .descricao("AFASTAMENTO")
            .codigo(CodigoMotivoInativacao.AFASTAMENTO)
            .situacao(ESituacao.A)
            .build();
    }

    public static MotivoInativacao umMotivoInativacaoDemissao() {
        return MotivoInativacao.builder()
            .id(3)
            .descricao("DEMISSÃO")
            .codigo(CodigoMotivoInativacao.DEMISSAO)
            .situacao(ESituacao.A)
            .build();
    }

    public static MotivoInativacao umMotivoInativacaoDescredenciado() {
        return MotivoInativacao.builder()
            .id(4)
            .descricao("DESCREDENCIADO")
            .codigo(CodigoMotivoInativacao.DESCREDENCIADO)
            .situacao(ESituacao.A)
            .build();
    }

    public static MotivoInativacao umMotivoInativacaoUltimoAcessoUsuario() {
        return MotivoInativacao.builder()
            .id(5)
            .descricao("ÚLTIMO ACESSO DO USUÁRIO")
            .codigo(CodigoMotivoInativacao.ULTIMO_ACESSO)
            .situacao(ESituacao.A)
            .build();
    }

    public static MotivoInativacao umMotivoInativacaoInatividadeAcesso() {
        return MotivoInativacao.builder()
            .id(6)
            .descricao("INATIVIDADE DE ACESSO")
            .codigo(CodigoMotivoInativacao.INATIVADO_SEM_ACESSO)
            .situacao(ESituacao.A)
            .build();
    }

    public static List<MotivoInativacao> umaListaMotivoInativacoes() {
        return List.of(
            umMotivoInativacaoFerias(),
            umMotivoInativacaoAfastamento(),
            umMotivoInativacaoDemissao(),
            umMotivoInativacaoDescredenciado(),
            umMotivoInativacaoUltimoAcessoUsuario(),
            umMotivoInativacaoInatividadeAcesso()
        );
    }
}
