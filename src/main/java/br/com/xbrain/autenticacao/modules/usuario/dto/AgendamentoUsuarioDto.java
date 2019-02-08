package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import lombok.*;
import org.springframework.beans.BeanUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoUsuarioDto {
    private Integer id;
    private String nome;
    private String equipeVendasNome;
    private String supervisorNome;
    private Long quantidade;

    public AgendamentoUsuarioDto(UsuarioAgenteAutorizadoAgendamentoResponse usuarioDto, Long quantidade) {
        BeanUtils.copyProperties(usuarioDto, this);
        this.quantidade = quantidade;
    }
}
