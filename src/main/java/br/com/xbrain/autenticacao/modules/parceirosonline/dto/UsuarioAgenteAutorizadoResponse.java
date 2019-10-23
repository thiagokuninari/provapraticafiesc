package br.com.xbrain.autenticacao.modules.parceirosonline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioAgenteAutorizadoResponse {

    private Integer id;
    private String nome;
    private String email;
    private Integer equipeVendaId;

    public UsuarioAgenteAutorizadoResponse(Integer id) {
        this.id = id;
    }

    public UsuarioAgenteAutorizadoResponse(Integer id, String nome, Integer equipeVendaId) {
        this.id = id;
        this.nome = nome;
        this.equipeVendaId = equipeVendaId;
    }
}
