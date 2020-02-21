package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UsuarioUltimoAcessoPol {

    private Integer usuarioId;
    private LocalDateTime dataUltimoAcesso;

}
