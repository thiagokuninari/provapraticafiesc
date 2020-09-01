package br.com.xbrain.autenticacao.modules.usuarioacesso.predicate;

import br.com.xbrain.autenticacao.modules.usuarioacesso.enums.ETipo;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.QUsuarioAcesso;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public class UsuarioAcessoPredicate {

    private static final int QTD_MAX_IN_NO_ORACLE = 1000;

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

    public UsuarioAcessoPredicate porUsuarioIds(Collection<Integer> usuarioIds) {
        if (!ObjectUtils.isEmpty(usuarioIds)) {
            var expression = Expressions.anyOf(Lists.partition(List.copyOf(usuarioIds), QTD_MAX_IN_NO_ORACLE)
                .stream()
                .map(usuarioAcesso.usuario.id::in)
                .toArray(BooleanExpression[]::new));
            this.builder.and(expression);
        }
        return this;
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

    public UsuarioAcessoPredicate porDataCadastro(LocalDate dataCadastro) {
        if (!ObjectUtils.isEmpty(dataCadastro)) {
            this.builder.and(usuarioAcesso.dataCadastro.between(
                dataCadastro.atStartOfDay(),
                dataCadastro.atTime(LocalTime.MAX)
            ));
        }
        return this;
    }

    public UsuarioAcessoPredicate porDataCadastroMinima(LocalDate dataCadastro) {
        if (!ObjectUtils.isEmpty(dataCadastro)) {
            this.builder.and(usuarioAcesso.dataCadastro.goe(dataCadastro.atStartOfDay()));
        }
        return this;
    }

    public UsuarioAcessoPredicate porPeriodo(
        LocalDate dataInicio, LocalDate dataFim, ETipo tipo) {
        if (!ObjectUtils.isEmpty(dataInicio) && !ObjectUtils.isEmpty(dataFim) && tipo.equals(ETipo.LOGOUT)) {
            builder.and(usuarioAcesso.dataCadastro.after(dataInicio.atTime(LocalTime.MIN))
                .and(usuarioAcesso.dataCadastro.before(dataFim.atTime(LocalTime.MAX))))
                .and(usuarioAcesso.flagLogout.eq("V"));
        } else {
            builder.and(usuarioAcesso.dataCadastro.after(dataInicio.atTime(LocalTime.MIN))
                .and(usuarioAcesso.dataCadastro.before(dataFim.atTime(LocalTime.MAX))))
                .and(usuarioAcesso.flagLogout.eq("F"));
        }
        return this;
    }

    public UsuarioAcessoPredicate porAa(Integer aaId, List<Integer> listaUsuarioId) {
        if (!ObjectUtils.isEmpty(aaId)) {
            this.builder.and(usuarioAcesso.usuario.id.in(listaUsuarioId));
        }
        return this;
    }
}