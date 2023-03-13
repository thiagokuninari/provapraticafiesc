package br.com.xbrain.autenticacao.modules.solicitacaoramal.helper;

import br.com.xbrain.autenticacao.modules.call.dto.RamalResponse;
import br.com.xbrain.autenticacao.modules.call.dto.TelefoniaResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.SocioResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalAtualizarStatusRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ETipoImplantacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao.REJEITADO;

public class SolicitacaoRamalHelper {

    public static SolicitacaoRamal umaOutraSolicitacaoRamal(Integer id) {
        var solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.setId(id);
        solicitacaoRamal.setAgenteAutorizadoNome("Teste");
        solicitacaoRamal.setAgenteAutorizadoId(101);
        solicitacaoRamal.setMelhorHorarioImplantacao(LocalTime.of(2, 2));
        solicitacaoRamal.setMelhorDataImplantacao(LocalDate.of(2023, 2, 1));
        solicitacaoRamal.setDataFinalizacao(LocalDateTime.of(2023, 1, 2, 10, 10, 2));
        solicitacaoRamal.setQuantidadeRamais(2);
        solicitacaoRamal.setSituacao(ESituacaoSolicitacao.PENDENTE);
        solicitacaoRamal.setDataCadastro(LocalDateTime.of(2022, 2, 10, 10, 0, 0));
        solicitacaoRamal.setTipoImplantacao(ETipoImplantacao.ESCRITORIO);
        solicitacaoRamal.setUsuario(umUsuario());
        solicitacaoRamal.setUsuariosSolicitados(List.of(umUsuario()));
        solicitacaoRamal.setEmailTi("teste@hotmail.com");

        return solicitacaoRamal;
    }

    public static SolicitacaoRamalRequest criaSolicitacaoRamal(Integer id, Integer aaId) {
        return SolicitacaoRamalRequest.builder()
            .id(id)
            .quantidadeRamais(2)
            .agenteAutorizadoId(aaId)
            .melhorHorarioImplantacao(LocalTime.of(10, 00))
            .melhorDataImplantacao(LocalDate.of(2019, 01, 25))
            .tipoImplantacao(ETipoImplantacao.ESCRITORIO.getCodigo())
            .emailTi("reanto@ti.com.br")
            .telefoneTi("(18) 3322-2388")
            .situacao(ESituacaoSolicitacao.PENDENTE)
            .usuariosSolicitadosIds(Arrays.asList(100, 101))
            .build();
    }

    public static AgenteAutorizadoResponse criaAa() {
        return AgenteAutorizadoResponse.builder()
            .id("303030")
            .cnpj("81733187000134")
            .nomeFantasia("Fulano")
            .discadoraId(1)
            .razaoSocial("RAZAO SOCIAL AA")
            .build();
    }

    public static SolicitacaoRamalAtualizarStatusRequest criaSolicitacaoRamalAtualizarStatusRequest() {
        return SolicitacaoRamalAtualizarStatusRequest.builder()
            .idSolicitacao(1)
            .observacao("Rejeitada teste")
            .situacao(REJEITADO)
            .build();
    }

    public static TelefoniaResponse criaTelefonia() {
        return TelefoniaResponse.builder()
            .id(13)
            .nome("DISCADORA UN")
            .build();
    }

    public static List<RamalResponse> criaListaRamal() {
        return Arrays.asList(new RamalResponse(), new RamalResponse());
    }

    public static SocioResponse criaSocio() {
        return SocioResponse.builder()
            .cpf("33333333333")
            .nome("FULANO")
            .build();
    }

    public static List<UsuarioAgenteAutorizadoResponse> criaListaUsuariosAtivos() {
        return Arrays.asList(new UsuarioAgenteAutorizadoResponse(), new UsuarioAgenteAutorizadoResponse());
    }

    public static SolicitacaoRamalAtualizarStatusRequest criaSolicitacaoRamalRequest() {
        return SolicitacaoRamalAtualizarStatusRequest.builder()
            .idSolicitacao(4)
            .situacao(ESituacaoSolicitacao.ENVIADO)
            .build();
    }

    public static List<UsuarioAgenteAutorizadoResponse> umaListaUsuarioAgenteAutorizadoResponse() {
        return List.of(umUsuarioAgenteAutorizadoResponse(1), umUsuarioAgenteAutorizadoResponse(2));
    }

    public static UsuarioAgenteAutorizadoResponse umUsuarioAgenteAutorizadoResponse(Integer id) {
        return UsuarioAgenteAutorizadoResponse.builder()
            .id(1)
            .nome("TESTE")
            .agenteAutorizadoId(11111)
            .email("TESTE@XBRAIN.COM.BR")
            .equipeVendaId(1)
            .build();
    }

    public static SolicitacaoRamal umaSolicitacaoRamal(Integer id) {
        var solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.setId(id);
        solicitacaoRamal.setAgenteAutorizadoNome("Teste");
        solicitacaoRamal.setAgenteAutorizadoId(1);
        solicitacaoRamal.setMelhorHorarioImplantacao(LocalTime.of(2, 2));
        solicitacaoRamal.setMelhorDataImplantacao(LocalDate.of(2023, 2, 1));
        solicitacaoRamal.setDataFinalizacao(LocalDateTime.of(2023, 1, 2, 10, 10, 2));
        solicitacaoRamal.setQuantidadeRamais(2);
        solicitacaoRamal.setDataCadastro(LocalDateTime.of(2022,2,10,10,0,0));
        solicitacaoRamal.setTipoImplantacao(ETipoImplantacao.ESCRITORIO);
        solicitacaoRamal.setUsuario(umUsuario());
        solicitacaoRamal.setUsuariosSolicitados(List.of(umUsuario()));
        solicitacaoRamal.setEmailTi("teste@hotmail.com");

        return solicitacaoRamal;
    }

    public static List<SolicitacaoRamal> umaListaSolicitacaoRamal() {
        return List.of(
            umaSolicitacaoRamal(1),
            umaSolicitacaoRamal(2),
            umaSolicitacaoRamal(3)
        );
    }

    public static List<SolicitacaoRamal> umaListaSolicitacaoRamalEmpty() {
        List<SolicitacaoRamal> lista = new ArrayList<>();
        return lista;
    }

    public static SolicitacaoRamalRequest umaSolicitacaoRamalRequest() {
        return SolicitacaoRamalRequest.builder()
            .id(1)
            .agenteAutorizadoId(1)
            .emailTi("EMAIL@XBRAIN.COM.BR")
            .quantidadeRamais(1)
            .melhorDataImplantacao(LocalDate.of(2022,02,10))
            .melhorHorarioImplantacao(LocalTime.of(13,30,00))
            .telefoneTi("1498761234")
            .usuariosSolicitadosIds(List.of(1))
            .dataCadastro(LocalDateTime.now())
            .tipoImplantacao("ESCRITORIO")
            .situacao(ESituacaoSolicitacao.ENVIADO)
            .build();
    }

    public static AgenteAutorizadoResponse umAgenteAutorizadoResponse() {
        return AgenteAutorizadoResponse.builder()
            .id("1")
            .razaoSocial("Teste AA")
            .cnpj("78.788.558/0001-70")
            .build();
    }

    public static SolicitacaoRamalHistorico umaSolicitacaoRamalHistorico() {
        return SolicitacaoRamalHistorico.builder()
            .solicitacaoRamal(umaSolicitacaoRamal(1))
            .comentario("")
            .situacao(ESituacaoSolicitacao.ENVIADO)
            .dataCadastro(LocalDateTime.of(2023, 2, 28, 14, 00, 00))
            .usuario(umaSolicitacaoRamal(1).getUsuario())
            .id(1)
            .build();
    }

    public static Usuario umUsuario() {
        return Usuario
            .builder()
            .id(100)
            .nome("Fulano de Teste")
            .email("teste@teste.com")
            .agenteAutorizadoId(101)
            .build();
    }
}
