package br.com.xbrain.autenticacao.modules.solicitacaoramal.helper;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.call.dto.RamalResponse;
import br.com.xbrain.autenticacao.modules.call.dto.TelefoniaResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.SocioResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalAtualizarStatusRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalDadosAdicionaisResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ETipoImplantacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao.REJEITADO;

public class SolicitacaoRamalHelper {

    public static SolicitacaoRamalRequest umaSolicitacaoRamalRequest(Integer id, Integer aaId) {
        return SolicitacaoRamalRequest.builder()
            .id(id)
            .quantidadeRamais(38)
            .agenteAutorizadoId(aaId)
            .melhorHorarioImplantacao(LocalTime.of(10, 00))
            .melhorDataImplantacao(LocalDate.of(2019, 01, 25))
            .tipoImplantacao(ETipoImplantacao.ESCRITORIO.getCodigo())
            .emailTi("reanto@ti.com.br")
            .telefoneTi("(18) 3322-2388")
            .usuariosSolicitadosIds(Arrays.asList(100, 101))
            .situacao(ESituacaoSolicitacao.CONCLUIDO)
            .usuariosSolicitadosIds(List.of(2))
            .canal(ECanal.AGENTE_AUTORIZADO)
            .build();
    }

    public static SolicitacaoRamalRequest umaSolicitacaoRamalRequestBlank(Integer id) {
        return SolicitacaoRamalRequest.builder()
            .id(id)
            .quantidadeRamais(38)
            .agenteAutorizadoId(1)
            .melhorHorarioImplantacao(LocalTime.of(10, 00))
            .melhorDataImplantacao(LocalDate.of(2019, 01, 25))
            .tipoImplantacao("    ")
            .emailTi("    ")
            .telefoneTi("    ")
            .usuariosSolicitadosIds(List.of())
            .situacao(ESituacaoSolicitacao.EM_ANDAMENTO)
            .usuariosSolicitadosIds(List.of(3))
            .canal(ECanal.AGENTE_AUTORIZADO)
            .build();
    }

    public static AgenteAutorizadoResponse umAgenteAutorizadoResponse() {
        return AgenteAutorizadoResponse.builder()
            .id("303030")
            .cnpj("81733187000134")
            .nomeFantasia("Fulano")
            .discadoraId(1)
            .razaoSocial("RAZAO SOCIAL AA")
            .build();
    }

    public static SolicitacaoRamalAtualizarStatusRequest umaSolicitacaoRamalAtualizarStatusRequest() {
        return SolicitacaoRamalAtualizarStatusRequest.builder()
            .idSolicitacao(1)
            .observacao("Rejeitada teste")
            .situacao(REJEITADO)
            .build();
    }

    public static TelefoniaResponse umaTelefoniaResponse() {
        return TelefoniaResponse.builder()
            .id(13)
            .nome("DISCADORA UN")
            .build();
    }

    public static List<RamalResponse> umListRamalResponse() {
        return List.of(
            umRamalResponse(1),
            umRamalResponse(2));
    }

    public static RamalResponse umRamalResponse(Integer id) {
        var response = new RamalResponse();
        response.setId(id);
        response.setRamal("123456");
        return response;
    }

    public static SocioResponse umSocioResponse() {
        return SocioResponse.builder()
            .cpf("33333333333")
            .nome("FULANO")
            .build();
    }

    public static List<UsuarioAgenteAutorizadoResponse> umaListaUsuarioResponse() {
        return Arrays.asList(umUsuarioAgenteAutorizadoResponse(1),
            umUsuarioAgenteAutorizadoResponse(2));
    }

    public static SolicitacaoRamalAtualizarStatusRequest umaSolicitacaoRamalAtualizarRequest() {
        return SolicitacaoRamalAtualizarStatusRequest.builder()
            .idSolicitacao(4)
            .situacao(ESituacaoSolicitacao.ENVIADO)
            .build();
    }

    public static UsuarioAutenticado umUsuarioAutenticado() {
        return UsuarioAutenticado.builder()
            .id(1)
            .nome("teste")
            .usuario(Usuario.builder().id(1).build())
            .cargoCodigo(CodigoCargo.AGENTE_AUTORIZADO_SOCIO)
            .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.CTR_20014.getRole())))
            .build();
    }

    public static Usuario umUsuario() {
        return Usuario.builder()
            .id(1)
            .nome("teste")
            .cpf("123456789")
            .build();
    }

    public static TelefoniaResponse umaTelefonia() {
        var telefoniaResponse = new TelefoniaResponse();
        telefoniaResponse.setId(1);
        telefoniaResponse.setNome("teste");
        return telefoniaResponse;
    }

    public static SocioResponse umSocioPrincipal() {
        var socioResponse = new SocioResponse();
        socioResponse.setId(1);
        socioResponse.setNome("teste");
        socioResponse.setCpf("12345678900");
        return socioResponse;
    }

    public static AgenteAutorizadoResponse umAgenteAutorizado() {
        return AgenteAutorizadoResponse.builder()
            .id("1234")
            .razaoSocial("solteiro")
            .nomeFantasia("teste")
            .cnpj("123456789")
            .nacional(Eboolean.V)
            .discadoraId(1)
            .build();
    }

    public static SolicitacaoRamalRequest criaSolicitacaoRamal(Integer id) {
        return SolicitacaoRamalRequest.builder()
            .id(id)
            .quantidadeRamais(38)
            .canal(ECanal.D2D_PROPRIO)
            .subCanalId(3)
            .melhorHorarioImplantacao(LocalTime.of(10, 00))
            .melhorDataImplantacao(LocalDate.of(2019, 01, 25))
            .tipoImplantacao(ETipoImplantacao.ESCRITORIO.getCodigo())
            .emailTi("reanto@ti.com.br")
            .telefoneTi("(18) 3322-2388")
            .usuariosSolicitadosIds(Arrays.asList(100, 101))
            .equipeId(1)
            .build();
    }

    public static SolicitacaoRamalRequest criaSolicitacaoRamal(Integer id, Integer aaId) {
        return SolicitacaoRamalRequest.builder()
            .id(id)
            .quantidadeRamais(38)
            .canal(ECanal.AGENTE_AUTORIZADO)
            .agenteAutorizadoId(aaId)
            .melhorHorarioImplantacao(LocalTime.of(10, 00))
            .melhorDataImplantacao(LocalDate.of(2019, 01, 25))
            .tipoImplantacao(ETipoImplantacao.ESCRITORIO.getCodigo())
            .emailTi("reanto@ti.com.br")
            .telefoneTi("(18) 3322-2388")
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

    public static SolicitacaoRamal umaSolicitacaoRamal(Integer id) {
        var solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.setId(id);
        solicitacaoRamal.setCanal(ECanal.AGENTE_AUTORIZADO);
        solicitacaoRamal.setDataCadastro(LocalDateTime.of(2022, 2, 10, 10, 0, 0));
        solicitacaoRamal.setMelhorDataImplantacao(LocalDate.of(2022, 12, 1));
        solicitacaoRamal.setUsuariosSolicitados(List.of(Usuario.builder().id(1).build()));
        solicitacaoRamal.setSituacao(ESituacaoSolicitacao.PENDENTE);
        solicitacaoRamal.setUsuario(Usuario.builder().id(1).nome("teste").build());
        solicitacaoRamal.setTipoImplantacao(ETipoImplantacao.ESCRITORIO);

        return solicitacaoRamal;
    }

    public static Page<SolicitacaoRamal> umaPageSolicitacaoRamal() {
        return new PageImpl<>(
            List.of(umaSolicitacaoRamal(1),
                umaSolicitacaoRamal(2))
        );
    }

    public static SolicitacaoRamalFiltros umFiltrosSolicitacao(ECanal canal, Integer subCanalId, Integer aaId) {
        var filtro = new SolicitacaoRamalFiltros();
        filtro.setCanal(canal);
        filtro.setSubCanalId(subCanalId);
        filtro.setAgenteAutorizadoId(aaId);
        return filtro;
    }

    public static UsuarioAutenticado umUsuarioGerenteOperacao() {
        return UsuarioAutenticado.builder()
            .id(1)
            .usuario(Usuario.builder().id(1).build())
            .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
            .build();
    }

    public static SubCanalDto umSubCanal(Integer id) {
        return SubCanalDto.builder()
            .id(id)
            .nome("teste")
            .situacao(ESituacao.A)
            .codigo(ETipoCanal.PAP)
            .build();
    }

    public static SolicitacaoRamalDadosAdicionaisResponse umDadosAdicionaisResponse() {
        var adicionalResponse = SolicitacaoRamalDadosAdicionaisResponse
            .convertFrom("nome discadora", 2);
        adicionalResponse.setDiscadora("discadora teste");
        adicionalResponse.setQuantidadeRamais(2);
        return adicionalResponse;
    }

    public static RamalResponse umRamal() {
        var ramalResponse = new RamalResponse();
        ramalResponse.setId(1);
        ramalResponse.setRamal("2000");
        return ramalResponse;
    }

    public static UsuarioAutenticado umUsuarioAutenticadoOperacao() {
        return UsuarioAutenticado.builder()
            .id(1)
            .usuario(Usuario.builder().id(1).build())
            .cargoCodigo(CodigoCargo.GERENTE_OPERACAO)
            .permissoes(List.of(new SimpleGrantedAuthority(CodigoFuncionalidade.CTR_20015.getRole())))
            .build();
    }

    public static SolicitacaoRamal umaSolicitacaoRamalCanalD2d() {
        var solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.setCanal(ECanal.D2D_PROPRIO);
        solicitacaoRamal.setSubCanal(SubCanal.builder().id(3).build());
        solicitacaoRamal.setUsuariosSolicitados(
            List.of(Usuario.builder().id(100).build(), Usuario.builder().id(101).build()));
        solicitacaoRamal.setSituacao(ESituacaoSolicitacao.PENDENTE);
        solicitacaoRamal.setEquipeId(1);
        solicitacaoRamal.setTelefoneTi("1833222388");
        solicitacaoRamal.setEmailTi("reanto@ti.com.br");
        solicitacaoRamal.setMelhorHorarioImplantacao(LocalTime.of(10, 0));
        solicitacaoRamal.setMelhorDataImplantacao(LocalDate.of(2019, 1, 25));
        solicitacaoRamal.setDataCadastro(LocalDateTime.of(2023, 11, 13, 10, 0));
        solicitacaoRamal.setDataFinalizacao(LocalDateTime.of(2023, 11, 16, 10, 0));
        solicitacaoRamal.setQuantidadeRamais(38);
        solicitacaoRamal.setTipoImplantacao(ETipoImplantacao.ESCRITORIO);
        solicitacaoRamal.setEnviouEmailExpiracao(Eboolean.F);

        return solicitacaoRamal;
    }

    public static SolicitacaoRamal umaSolicitacaoRamalCanalD2d(Integer id) {
        var solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.setId(id);
        solicitacaoRamal.setCanal(ECanal.D2D_PROPRIO);
        solicitacaoRamal.setSubCanal(SubCanal.builder().id(1).build());
        solicitacaoRamal.setDataCadastro(LocalDateTime.of(2022, 2, 10, 10, 0));
        solicitacaoRamal.setMelhorDataImplantacao(LocalDate.of(2022, 12, 1));
        solicitacaoRamal.setUsuariosSolicitados(List.of(Usuario.builder().id(1).build()));
        solicitacaoRamal.setSituacao(ESituacaoSolicitacao.PENDENTE);
        solicitacaoRamal.setUsuario(Usuario.builder().id(1).nome("teste").build());
        solicitacaoRamal.setTipoImplantacao(ETipoImplantacao.ESCRITORIO);

        return solicitacaoRamal;
    }

    public static List<UsuarioAgenteAutorizadoResponse> umaListaUsuarioAgenteAutorizadoResponse() {
        return List.of(umUsuarioAgenteAutorizadoResponse(1), umUsuarioAgenteAutorizadoResponse(2));
    }

    public static UsuarioAgenteAutorizadoResponse umUsuarioAgenteAutorizadoResponse(Integer id) {
        return UsuarioAgenteAutorizadoResponse.builder()
            .id(id)
            .nome("TESTE")
            .agenteAutorizadoId(11111)
            .email("TESTE@XBRAIN.COM.BR")
            .equipeVendaId(1)
            .build();
    }
}
