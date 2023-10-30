package br.com.xbrain.autenticacao.modules.equipevenda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipeVendaUsuarioRequest {

    public Integer usuarioId;
    public String usuarioNome;
    public String cargoNome;
    public boolean isTrocaDeSubCanal;
}
