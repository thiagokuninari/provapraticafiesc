package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaSupervisorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEquipeDto {

    private Integer usuarioId;
    private String usuarioNome;
    private Integer equipeVendaId;
    private String equipeVendaNome;
    private String supervisorEquipeNome;
    private ESituacao situacao;

    public static UsuarioEquipeDto of(UsuarioSituacaoResponse usuarioSituacaoResponse) {
        return UsuarioEquipeDto.builder()
            .usuarioId(usuarioSituacaoResponse.getId())
            .usuarioNome(usuarioSituacaoResponse.getNome())
            .situacao(usuarioSituacaoResponse.getSituacao())
            .build();
    }

    public boolean isAtivo(Boolean buscarInativo) {
        return buscarInativo || ESituacao.A.equals(situacao);
    }

    public UsuarioEquipeDto setEquipe(EquipeVendaSupervisorDto equipe) {
        this.equipeVendaId = equipe.getId();
        this.equipeVendaNome = equipe.getDescricao();
        this.supervisorEquipeNome = equipe.getSupervisorNome();
        return this;
    }
}
