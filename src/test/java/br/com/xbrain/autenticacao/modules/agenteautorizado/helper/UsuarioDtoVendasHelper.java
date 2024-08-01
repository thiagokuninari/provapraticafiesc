package br.com.xbrain.autenticacao.modules.agenteautorizado.helper;

import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioDtoVendas;
import br.com.xbrain.autenticacao.modules.usuario.dto.PublicoAlvoComunicadoFiltros;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelAa;

public class UsuarioDtoVendasHelper {

    public static UsuarioDtoVendas umUsuarioDtoVendas(Integer id) {
        return UsuarioDtoVendas
            .builder()
            .id(id)
            .build();
    }

    public static PublicoAlvoComunicadoFiltros umPublicoAlvoComunicadoFiltros() {
        return PublicoAlvoComunicadoFiltros.builder()
            .todoCanalD2d(false)
            .todoCanalAa(true)
            .agentesAutorizadosIds(List.of(1, 2))
            .equipesVendasIds(List.of(3, 4))
            .usuariosIds(List.of(5, 6))
            .cargosIds(List.of(7, 8))
            .cidadesIds(List.of(9, 10))
            .niveisIds(List.of(11, 12))
            .regionalId(15)
            .ufId(17)
            .usuariosFiltradosPorCidadePol(List.of(18, 19))
            .usuarioAutenticado(umUsuarioAutenticadoNivelAa())
            .comUsuariosLogadosHoje(true)
            .build();
    }

    public static UsuarioDtoVendas umOutroUsuarioDtoVendas() {
        return UsuarioDtoVendas.builder()
            .id(1)
            .email("mso_analistaadm_claromovel_pessoal@net.com.br")
            .agenteAutorizadoCnpj("64.262.572/0001-21")
            .agenteAutorizadoRazaoSocial("Razao Social Teste")
            .agenteAutorizadoId(1)
            .build();
    }
}
