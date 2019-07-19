package br.com.xbrain.autenticacao.modules.usuario.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class UsuarioLogoutDtoTest {
    private lateinit var objectMapper: ObjectMapper

    @Before
    fun setUp() {
        objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    @Throws(Exception::class)
    fun usuarioLogoutDto_deveConverterMensagemParaObjeto_quandoForUmaMensagemValida() {
        val usuarioLogoutDto = objectMapper.readValue(umUsuarioLogoutDtoValido, UsuarioLogoutDto::class.java)
        assertThat(usuarioLogoutDto.usuariosIds).contains(85,109)
        assertThat(usuarioLogoutDto.datahora).isEqualTo("2019-07-02T14:11:36.916566")
    }

    companion object {
        const val umUsuarioLogoutDtoValido = "{\"usuariosIds\":[85,109],\"datahora\":\"2019-07-02T14:11:36.916566\"}"
    }
}