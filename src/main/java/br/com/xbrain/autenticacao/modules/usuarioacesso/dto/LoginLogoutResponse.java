package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginLogoutResponse {

    private Integer usuarioId;
    private LocalDateTime dataLogin;
    private LocalDateTime dataLogout;
    private String usuarioNome;
    private LocalTime horarioLogin;
    private LocalTime horarioLogout;
    private String tempoTotalLogado;
}
