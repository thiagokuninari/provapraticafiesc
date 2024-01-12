package br.com.xbrain.autenticacao.modules.usuarioacesso.predicate;

import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuarioacesso.enums.ETipo;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.QUsuarioAcesso;
import com.querydsl.core.BooleanBuilder;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;

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

    public UsuarioAcessoPredicate porNivel(Integer nivelId) {
        if (nivelId != null) {
            this.builder.and(usuario.cargo.nivel.id.in(nivelId));
        }
        return this;
    }

    public UsuarioAcessoPredicate porCargo(Integer cargoId) {
        if (cargoId != null) {
            this.builder.and(usuarioAcesso.usuario.cargo.id.eq(cargoId));
        }
        return this;
    }

    public UsuarioAcessoPredicate porCanal(ECanal canal) {
        if (canal != null) {
            this.builder.and(usuarioAcesso.usuario.canais.any().eq(canal));
        }
        return this;
    }

    public UsuarioAcessoPredicate porSubCanal(Integer subCanalId) {
        if (subCanalId != null) {
            this.builder.and(usuarioAcesso.usuario.subCanais.any().id.eq(subCanalId));
        }
        return this;
    }
}
