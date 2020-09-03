package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginLogoutResponse {

    private String usuarioNome;
    private LocalTime horarioLogin;
    private LocalTime horarioLogout;
    private String tempoTotalLogado;
}
