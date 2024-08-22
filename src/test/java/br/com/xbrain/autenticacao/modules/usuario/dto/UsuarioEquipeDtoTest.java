package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaSupervisorDto;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioEquipeDtoTest {

    @Test
    public void setEquipe_deveSetarEquipe_quandoExistirEquipeVendaSupervisorDto() {
        var usuarioEquipe = new UsuarioEquipeDto();
        assertThat(usuarioEquipe.setEquipe(umaEquipeVendaSupervisorDto()))
            .extracting("equipeVendaId", "equipeVendaNome", "supervisorNome")
            .containsExactly(23, "uma descricao bem descritiva", "nome do supervisor");
    }

    @Test
    public void setEquipe_naoDeveSetarEquipe_quandoNaoExistirEquipeVendaSupervisorDto() {
        var usuarioEquipe = new UsuarioEquipeDto();
        assertThat(usuarioEquipe.setEquipe(null))
            .extracting("equipeVendaId", "equipeVendaNome", "supervisorNome")
            .containsExactly(null, null, null);
    }

    private EquipeVendaSupervisorDto umaEquipeVendaSupervisorDto() {
        return EquipeVendaSupervisorDto.builder()
            .id(23)
            .descricao("uma descricao bem descritiva")
            .supervisorNome("nome do supervisor")
            .build();
    }
}
