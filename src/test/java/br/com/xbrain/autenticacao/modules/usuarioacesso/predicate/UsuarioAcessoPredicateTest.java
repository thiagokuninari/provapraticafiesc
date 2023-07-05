package br.com.xbrain.autenticacao.modules.usuarioacesso.predicate;

import br.com.xbrain.autenticacao.modules.usuarioacesso.enums.ETipo;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuarioacesso.model.QUsuarioAcesso.usuarioAcesso;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioAcessoPredicateTest {

    @Test
    public void porNome_deveMontarPredicate_quandoInformarNome() {
        assertThat(new UsuarioAcessoPredicate().porNome("Teste").build())
            .isEqualTo(new BooleanBuilder(usuarioAcesso.usuario.nome.contains("Teste")));
    }

    @Test
    public void porNome_deveMontarPredicate_quandoInformarNomeBlank() {
        assertThat(new UsuarioAcessoPredicate().porNome("  ").build())
            .isEqualTo(new BooleanBuilder(usuarioAcesso.usuario.nome.contains("  ")));
    }

    @Test
    public void porNome_naoDeveMontarPredicate_quandoInformarNomeNull() {
        assertThat(new UsuarioAcessoPredicate().porNome(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void porCpf_deveMontarPredicate_quandoInformarCpf() {
        assertThat(new UsuarioAcessoPredicate().porCpf("Teste").build())
            .isEqualTo(new BooleanBuilder(usuarioAcesso.usuario.cpf.contains("Teste")));
    }

    @Test
    public void porCpf_deveMontarPredicate_quandoInformarCpfBlank() {
        assertThat(new UsuarioAcessoPredicate().porCpf("  ").build())
            .isEqualTo(new BooleanBuilder(usuarioAcesso.usuario.cpf.contains("  ")));
    }

    @Test
    public void porCpf_naoDeveMontarPredicate_quandoInformarCpfNull() {
        assertThat(new UsuarioAcessoPredicate().porCpf(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void porEmail_deveMontarPredicate_quandoInformarEmail() {
        assertThat(new UsuarioAcessoPredicate().porEmail("Teste").build())
            .isEqualTo(new BooleanBuilder(usuarioAcesso.usuario.email.contains("Teste")));
    }

    @Test
    public void porEmail_deveMontarPredicate_quandoInformarEmailBlank() {
        assertThat(new UsuarioAcessoPredicate().porEmail("  ").build())
            .isEqualTo(new BooleanBuilder(usuarioAcesso.usuario.email.contains("  ")));
    }

    @Test
    public void porEmail_naoDeveMontarPredicate_quandoInformarEmailNull() {
        assertThat(new UsuarioAcessoPredicate().porEmail(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void porPeriodo_deveMontarPredicate_quandoInformarDatasETipoIgualLogout() {
        var dataInicio = LocalDate.of(2023, 5, 9);
        var dataFim = LocalDate.of(2023, 5, 11);

        assertThat(new UsuarioAcessoPredicate().porPeriodo(dataInicio, dataFim, ETipo.LOGOUT).build())
            .isEqualTo(
                new BooleanBuilder(
                    usuarioAcesso.dataCadastro.after(dataInicio.atTime(LocalTime.MIN))
                        .and(usuarioAcesso.dataCadastro.before(dataFim.atTime(LocalTime.MAX)))
                        .and(usuarioAcesso.flagLogout.eq("V"))
                )
            );
    }

    @Test
    public void porPeriodo_deveMontarPredicate_quandoInformarDatasETipoDiferenteDeLogout() {
        var dataInicio = LocalDate.of(2023, 5, 9);
        var dataFim = LocalDate.of(2023, 5, 11);

        assertThat(new UsuarioAcessoPredicate().porPeriodo(dataInicio, dataFim, ETipo.LOGIN).build())
            .isEqualTo(
                new BooleanBuilder(
                    usuarioAcesso.dataCadastro.after(dataInicio.atTime(LocalTime.MIN))
                        .and(usuarioAcesso.dataCadastro.before(dataFim.atTime(LocalTime.MAX)))
                        .and(usuarioAcesso.flagLogout.eq("F"))
                )
            );
    }

    @Test
    public void porPeriodo_NaodeveMontarPredicate_quandoNaoInformarData() {
        assertThat(new UsuarioAcessoPredicate().porPeriodo(null, null, ETipo.LOGIN).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void porAa_deveMontarPredicate_quandoInformarAaId() {
        assertThat(new UsuarioAcessoPredicate().porAa(1, List.of(1, 2, 3)).build())
            .isEqualTo(new BooleanBuilder(usuarioAcesso.usuario.id.in(List.of(1, 2, 3))));
    }

    @Test
    public void porAa_deveMontarPredicate_quandosNaoInformarAaId() {
        assertThat(new UsuarioAcessoPredicate().porAa(null, List.of(1, 2, 3)).build())
            .isEqualTo(new BooleanBuilder());
    }
}
