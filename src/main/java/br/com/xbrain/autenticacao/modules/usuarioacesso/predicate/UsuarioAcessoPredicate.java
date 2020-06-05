package br.com.xbrain.autenticacao.modules.usuarioacesso.predicate;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.QCargo;
import br.com.xbrain.autenticacao.modules.usuarioacesso.enums.ETipo;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.QUsuarioAcesso;
import com.querydsl.core.BooleanBuilder;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class UsuarioAcessoPredicate {

    private BooleanBuilder builder;
    private QUsuarioAcesso usuarioAcesso = QUsuarioAcesso.usuarioAcesso;
    private QCargo cargo = QCargo.cargo;

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

    public UsuarioAcessoPredicate porPeriodoComHora(LocalDateTime dataInicio, LocalDateTime dataFim, ETipo tipo) {
        if (!ObjectUtils.isEmpty(dataInicio) && !ObjectUtils.isEmpty(dataFim) && tipo.equals(ETipo.LOGOUT)) {
            builder.and(usuarioAcesso.dataCadastro.goe(dataInicio).and(usuarioAcesso.dataCadastro.loe(dataFim)))
                .and(usuarioAcesso.flagLogout.eq(Eboolean.V.name()));
        } else {
            builder.and(usuarioAcesso.dataCadastro.goe(dataInicio).and(usuarioAcesso.dataCadastro.loe(dataFim)))
                .and(usuarioAcesso.flagLogout.eq(Eboolean.F.name()));
        }
        return this;
    }

    public UsuarioAcessoPredicate porOrganizacao(Integer organizacaoId) {
        if (!ObjectUtils.isEmpty(organizacaoId)) {
            this.builder.and(usuarioAcesso.usuario.organizacao.id.eq(organizacaoId));
        }
        return this;
    }

    public UsuarioAcessoPredicate porCargos(List<CodigoCargo> cargos) {
        if (!ObjectUtils.isEmpty(cargos)) {
            this.builder.and(cargo.codigo.in(cargos));
        }
        return this;
    }
}