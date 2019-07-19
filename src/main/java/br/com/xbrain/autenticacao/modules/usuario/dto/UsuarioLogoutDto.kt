package br.com.xbrain.autenticacao.modules.usuario.dto

import java.time.LocalDateTime

data class UsuarioLogoutDto(var usuariosIds: List<Int> = emptyList(),
                            var datahora: LocalDateTime = LocalDateTime.now())