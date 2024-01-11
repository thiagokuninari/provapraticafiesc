package br.com.xbrain.autenticacao.modules.usuarioacesso.predicate;

import br.com.xbrain.autenticacao.modules.comum.util.Constantes;
import br.com.xbrain.autenticacao.modules.usuarioacesso.enums.ETipo;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.QUsuarioAcesso;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioAcessoPredicate {

    private BooleanBuilder builder;
    private QUsuarioAcesso usuarioAcesso = QUsuarioAcesso.usuarioAcesso;

    public UsuarioAcessoPredicate() {
        this.builder = new BooleanBuilder();
    }

    public BooleanBuilder build() {
        return this.builder;
    }

    public UsuarioAcessoPredicate(BooleanBuilder builder) {
        this.builder = builder;
    }

    public UsuarioAcessoPredicate porNome(String nome) {
        if (!ObjectUtils.isEmpty(nome)) {
            this.builder.and(usuarioAcesso.usuario.nome.contains(nome));
        }
        return this;
    }

    public UsuarioAcessoPredicate porCpf(String cpf) {
        if (!ObjectUtils.isEmpty(cpf)) {
            this.builder.and(usuarioAcesso.usuario.cpf.contains(cpf));
        }
        return this;
    }

    public UsuarioAcessoPredicate porEmail(String email) {
        if (!ObjectUtils.isEmpty(email)) {
            this.builder.and(usuarioAcesso.usuario.email.contains(email));
        }
        return this;
    }

    public UsuarioAcessoPredicate porPeriodo(LocalDate dataInicio, LocalDate dataFim, ETipo tipo) {
        if (!ObjectUtils.isEmpty(dataInicio) && !ObjectUtils.isEmpty(dataFim)) {
            if (tipo.equals(ETipo.LOGOUT)) {
                builder.and(usuarioAcesso.dataCadastro.after(dataInicio.atTime(LocalTime.MIN))
                    .and(usuarioAcesso.dataCadastro.before(dataFim.atTime(LocalTime.MAX))))
                    .and(usuarioAcesso.flagLogout.eq("V"));
            } else {
                builder.and(usuarioAcesso.dataCadastro.after(dataInicio.atTime(LocalTime.MIN))
                    .and(usuarioAcesso.dataCadastro.before(dataFim.atTime(LocalTime.MAX))))
                    .and(usuarioAcesso.flagLogout.eq("F"));
            }
        }
        return this;
    }

    public UsuarioAcessoPredicate porAa(Integer aaId, List<Integer> listaUsuarioId) {
        if (!ObjectUtils.isEmpty(aaId)) {
            this.builder.and(usuarioAcesso.usuario.id.in(listaUsuarioId));
        }
        return this;
    }

    public UsuarioAcessoPredicate porNivel(List<Integer> listaUsuarioId) {
        if (!ObjectUtils.isEmpty(listaUsuarioId)) {
            builder.and(ExpressionUtils.anyOf(
                Lists.partition(listaUsuarioId, Constantes.QTD_MAX_IN_NO_ORACLE)
                    .stream()
                    .map(usuarioAcesso.usuario.id::in)
                    .collect(Collectors.toList())
            ));
        }
        return this;
    }

    public UsuarioAcessoPredicate porCargo(List<Integer> listaUsuarioId) {
        if (!ObjectUtils.isEmpty(listaUsuarioId)) {
            builder.and(ExpressionUtils.anyOf(
                Lists.partition(listaUsuarioId, Constantes.QTD_MAX_IN_NO_ORACLE)
                    .stream()
                    .map(usuarioAcesso.usuario.id::in)
                    .collect(Collectors.toList())
            ));
        }
        return this;
    }

    public UsuarioAcessoPredicate porCanal(List<Integer> listaUsuarioId) {
        if (!ObjectUtils.isEmpty(listaUsuarioId)) {
            builder.and(ExpressionUtils.anyOf(
                Lists.partition(listaUsuarioId, Constantes.QTD_MAX_IN_NO_ORACLE)
                    .stream()
                    .map(usuarioAcesso.usuario.id::in)
                    .collect(Collectors.toList())
            ));
        }
        return this;
    }

    public UsuarioAcessoPredicate porSubCanal(List<Integer> listaUsuarioId) {
        if (!ObjectUtils.isEmpty(listaUsuarioId)) {
            builder.and(ExpressionUtils.anyOf(
                Lists.partition(listaUsuarioId, Constantes.QTD_MAX_IN_NO_ORACLE)
                    .stream()
                    .map(usuarioAcesso.usuario.id::in)
                    .collect(Collectors.toList())
            ));
        }
        return this;
    }
}
