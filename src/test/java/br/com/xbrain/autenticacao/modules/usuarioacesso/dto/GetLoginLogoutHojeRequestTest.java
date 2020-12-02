package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class GetLoginLogoutHojeRequestTest {

    @Test
    public void of_dtoComInformacoesCorretas_quandoHouverUsuariosIds() {
        var pageRequest = new PageRequest(4, 25, "nomeFantasia", "DESC");
        var usuariosIds = Optional.of(List.of(98, 11, 10, 11, 7));

        assertThat(GetLoginLogoutHojeRequest.of(usuariosIds, pageRequest))
            .extracting("usuariosIds", "page", "size", "orderBy", "orderDirection")
            .containsExactly(Set.of(11, 98, 10, 7), 4, 25, "nomeFantasia", "DESC");
    }

    @Test
    public void of_deveSetarUsuariosIdsVazio_quandoUsuariosIdsForVazio() {
        var pageRequest = new PageRequest();
        var usuariosIds = Optional.of(List.<Integer>of());

        assertThat(GetLoginLogoutHojeRequest.of(usuariosIds, pageRequest).getUsuariosIds()).isEmpty();
    }

    @Test
    public void of_deveSetarUsuariosIdsNull_quandoNaoHouverUsuariosIds() {
        var pageRequest = new PageRequest();
        var usuariosIds = Optional.<List<Integer>>empty();

        assertThat(GetLoginLogoutHojeRequest.of(usuariosIds, pageRequest).getUsuariosIds()).isNull();
    }
}
