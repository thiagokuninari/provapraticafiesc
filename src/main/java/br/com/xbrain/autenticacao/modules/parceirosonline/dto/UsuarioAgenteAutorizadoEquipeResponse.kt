package br.com.xbrain.autenticacao.modules.parceirosonline.dto

data class UsuarioAgenteAutorizadoEquipeResponse(val id: Int,
                                                 val nome: String,
                                                 val equipeVendasId: Int? = null)