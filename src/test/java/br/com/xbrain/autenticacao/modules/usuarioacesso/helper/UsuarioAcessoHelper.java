package br.com.xbrain.autenticacao.modules.usuarioacesso.helper;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.PaLogadoDto;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioLogadoRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioLogadoResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.enums.ETipo;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.UsuarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class UsuarioAcessoHelper {
    public static PaLogadoDto umPaLogadoDto(String dataInicial, String dataFinal, Integer totalUsuariosLogados) {
        return PaLogadoDto.builder()
            .dataInicial(dataInicial)
            .dataFinal(dataFinal)
            .totalUsuariosLogados(totalUsuariosLogados)
            .build();
    }

    public static List<PaLogadoDto> umaListaPaLogadoDto() {

        return List.of(
            umPaLogadoDto(
                "2023-06-10",
                "2023-06-20",
                5),
            umPaLogadoDto(
                "2023-06-05",
                "2023-06-09",
                3),
            umPaLogadoDto(
                "2023-06-15",
                "2023-06-22",
                2)
        );
    }

    public static UsuarioLogadoRequest umUsuarioLogadoRequest() {
        return UsuarioLogadoRequest.builder()
            .periodos(umaListaPaLogadoDto())
            .cargos(Lists.newArrayList(CodigoCargo.ADMINISTRADOR))
            .organizacaoId(1)
            .usuariosIds(Lists.newArrayList(1))
            .build();
    }

    public static UsuarioAutenticado umUsuarioAgenteAutorizado() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.AGENTE_AUTORIZADO.name());
        usuario.setAgentesAutorizados(List.of(67, 90));
        return usuario;
    }

    public static UsuarioAutenticado umUsuarioAutenticado(String nivelCodigo) {
        return UsuarioAutenticado.builder()
            .id(100)
            .nivelCodigo(nivelCodigo)
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticado() {
        return UsuarioAutenticado.builder()
            .id(89)
            .nome("Hwasa Maria")
            .usuario(Usuario.builder()
                .canais(Set.of(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO))
                .build())
            .build();
    }

    public static UsuarioAutenticado umUsuarioCoordenadorOperacao() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.OPERACAO.name());
        usuario.setCargoCodigo(CodigoCargo.COORDENADOR_OPERACAO);
        return usuario;
    }

    public static UsuarioAutenticado umUsuarioXBrain() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.XBRAIN.name());
        return usuario;
    }

    public static UsuarioAutenticado umUsuarioGerenteOperacao() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.OPERACAO.name());
        usuario.setCargoCodigo(CodigoCargo.GERENTE_OPERACAO);
        return usuario;
    }

    public static List<Integer> umUsuariosIdsList() {
        return List.of(12, 7, 90, 1, 3, 100);
    }

    public static UsuarioAutenticado umUsuarioMso() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.MSO.name());
        return usuario;
    }

    public static UsuarioAutenticado umUsuarioAssistenteOperacao() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.OPERACAO.name());
        usuario.setCargoCodigo(CodigoCargo.ASSISTENTE_OPERACAO);
        return usuario;
    }

    public static UsuarioAutenticado umUsuarioExecutivoOperacao() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.OPERACAO.name());
        usuario.setCargoCodigo(CodigoCargo.EXECUTIVO);
        return usuario;
    }

    public static UsuarioAutenticado umUsuarioExecutivoHunterOperacao() {
        var usuario = umUsuarioAutenticado();
        usuario.setNivelCodigo(CodigoNivel.OPERACAO.name());
        usuario.setCargoCodigo(CodigoCargo.EXECUTIVO_HUNTER);
        return usuario;
    }

    public static UsuarioAcesso umUsuarioAcesso(Integer id, Integer hora, Integer dia) {
        return UsuarioAcesso.builder()
            .id(id)
            .dataCadastro(LocalDateTime.of(2020, 01, dia, hora, 00))
            .usuario(Usuario.builder().id(id).build())
            .build();
    }

    public static UsuarioAcesso umUsuarioAcesso() {
        return UsuarioAcesso.builder()
            .id(1)
            .dataCadastro(LocalDateTime.of(2020, 10, 15, 0, 0))
            .usuario(Usuario.builder().id(1).build())
            .build();
    }

    public static Page<UsuarioAcesso> umaPaginaUsuarioAcesso() {
        var lista = List.of(umUsuarioAcesso());

        return new PageImpl<>(lista, new PageRequest(), lista.size());
    }

    public static UsuarioAcessoResponse umUsuarioAcessoResponse() {
        return UsuarioAcessoResponse.of(umUsuarioAcesso());
    }

    public static Page<UsuarioAcessoResponse> umaPaginaUsuarioAcessoResponse() {
        var lista = List.of(umUsuarioAcessoResponse());

        return new PageImpl<>(lista, new PageRequest(), lista.size());
    }

    public static UsuarioAcessoFiltros umUsuarioAcessoFiltros() {
        return UsuarioAcessoFiltros.builder()
            .dataInicio(LocalDate.now().minusDays(1))
            .dataFim(LocalDate.now())
            .dataInicial(LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIN))
            .dataFinal(LocalDateTime.of(LocalDate.now(), LocalTime.MAX))
            .tipo(ETipo.LOGIN)
            .build();
    }

    public static List<PaLogadoDto> umUsuarioLogadoResponse() {
        return List.of(
            PaLogadoDto.builder()
                .dataInicial("2020-12-01T10:00:00.000Z")
                .dataFinal("2020-12-01T10:59:59.999Z")
                .totalUsuariosLogados(10)
                .build(),
            PaLogadoDto.builder()
                .dataInicial("2020-12-01T11:00:00.000Z")
                .dataFinal("2020-12-01T11:42:39.999Z")
                .totalUsuariosLogados(3)
                .build());
    }

    public static UsuarioLogadoRequest umUsuarioLogadoRequestComPeriodo() {
        return UsuarioLogadoRequest.builder()
            .cargos(List.of(CodigoCargo.BACKOFFICE_OPERADOR_TRATAMENTO, CodigoCargo.BACKOFFICE_ANALISTA_TRATAMENTO))
            .organizacaoId(6)
            .periodos(List.of(PaLogadoDto.builder()
                    .dataInicial("2020-12-01T10:00:00.000Z")
                    .dataFinal("2020-12-01T10:59:59.999Z")
                    .build(),
                PaLogadoDto.builder()
                    .dataInicial("2020-12-01T11:00:00.000Z")
                    .dataFinal("2020-12-01T11:42:39.999Z")
                    .build()))
            .build();
    }

    public static UsuarioAgenteAutorizadoResponse umUsuarioAgenteAutorizadoResponse(Integer id, Integer aaId) {
        return UsuarioAgenteAutorizadoResponse.builder()
            .id(id)
            .nome("FULANO DE TESTE")
            .email("TESTE@TESTE.COM")
            .agenteAutorizadoId(aaId)
            .build();
    }

    public static UsuarioLogadoResponse umUsuarioLogadoResponseBko(Integer usuarioId) {
        return UsuarioLogadoResponse.builder()
            .usuarioId(usuarioId)
            .dataEntrada(LocalDateTime.of(2024, 3, 22, 10, 30))
            .build();
    }

    public static List<UsuarioLogadoResponse> umaListaDeUsuariosLogados() {
        return List.of(
            umUsuarioLogadoResponseBko(4444),
            umUsuarioLogadoResponseBko(2000)
        );
    }
}
