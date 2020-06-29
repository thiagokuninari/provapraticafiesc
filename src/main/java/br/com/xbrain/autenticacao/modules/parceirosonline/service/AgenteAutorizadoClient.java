package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoPermitidoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "agenteAutorizadoClient",
        url = "${app-config.services.parceiros-online.url}",
        configuration = FeignSkipBadRequestsConfiguration.class)
public interface AgenteAutorizadoClient {

    String API_AGENTE_AUTORIZADOS_USUARIO = "api/agentes-autorizados-usuario";
    String API_AGENTE_AUTORIZADO = "api/agente-autorizado";
    String API_COLABORADOR_VENDAS = "api/colaboradores-vendas";
    String API_AGENTE_AUTORIZADO_PERMITIDOS = "api/agentes-autorizados-permitidos";
    String API_USUARIO_AGENTE_AUTORIZADO = "api/usuarios-agente-autorizado";

    @GetMapping(API_AGENTE_AUTORIZADO + "/agente-autorizado-por-cnpj")
    AgenteAutorizadoResponse getAaByCpnj(@RequestParam Map request);

    @GetMapping(API_AGENTE_AUTORIZADO + "/id/{idAgenteAutorizado}")
    AgenteAutorizadoResponse getAaById(@PathVariable("idAgenteAutorizado") Integer idAgenteAutorizado);

    @GetMapping(API_AGENTE_AUTORIZADO + "/empresas-matriz-filiais/{usuarioId}")
    List<EmpresaResponse> getEmpresasPermitidas(@PathVariable("usuarioId") Integer usuarioId);

    @GetMapping(API_AGENTE_AUTORIZADO + "/{usuarioId}/exclusivo-pme")
    boolean isExclusivoPme(@PathVariable("usuarioId") Integer usuarioId);

    @GetMapping(API_AGENTE_AUTORIZADO + "/{usuarioId}/estrutura")
    String getEstrutura(@PathVariable("usuarioId") Integer usuarioId);

    @GetMapping(API_USUARIO_AGENTE_AUTORIZADO + "/{agenteAutorizadoId}")
    List<UsuarioAgenteAutorizadoResponse> getUsuariosByAaId(
            @PathVariable("agenteAutorizadoId") Integer agenteAutorizadoId);

    @GetMapping(API_USUARIO_AGENTE_AUTORIZADO + "/{agenteAutorizadoId}/{buscarInativos}")
    List<UsuarioAgenteAutorizadoResponse> getUsuariosByAaId(
            @PathVariable("agenteAutorizadoId") Integer agenteAutorizadoId,
            @PathVariable("buscarInativos") Boolean buscarInativos);

    @GetMapping(API_USUARIO_AGENTE_AUTORIZADO + "/usuarios-com-d2d/{agenteAutorizadoId}")
    List<UsuarioAgenteAutorizadoResponse> getUsuariosAaAtivoComVendedoresD2D(
            @PathVariable("agenteAutorizadoId") Integer agenteAutorizadoId);

    @GetMapping(API_AGENTE_AUTORIZADO_PERMITIDOS + "/{usuarioId}")
    List<Integer> getAasPermitidos(@PathVariable("usuarioId") Integer usuarioId);

    @PutMapping(API_COLABORADOR_VENDAS + "/limpar-cpf-agente-autorizado")
    void limparCpfAgenteAutorizado(@RequestParam("email") String email);

    @GetMapping(API_AGENTE_AUTORIZADOS_USUARIO + "/situacao-agente-autorizado-por-usuario-email")
    boolean existeAaAtivoBySocioEmail(@RequestParam("usuarioEmail") String usuarioEmail);

    @GetMapping(API_AGENTE_AUTORIZADOS_USUARIO + "/situacao-agente-autorizado-por-usuario-id")
    boolean existeAaAtivoByUsuarioId(@RequestParam("usuarioId") Integer usuarioId);

    @GetMapping(API_AGENTE_AUTORIZADOS_USUARIO + "/recupera-emails")
    List<String> recuperarColaboradoresDoAgenteAutorizado(@RequestParam(name = "cnpj") String cnpj);

    @GetMapping(API_AGENTE_AUTORIZADO_PERMITIDOS)
    List<AgenteAutorizadoPermitidoResponse> getAgentesAutorizadosPermitidos();

    @GetMapping(API_USUARIO_AGENTE_AUTORIZADO + "/{agenteAutorizadoId}/canal/usuario/{usuarioId}")
    List<UsuarioAgenteAutorizadoAgendamentoResponse> getUsuariosByAaIdCanalDoUsuario(
            @PathVariable("agenteAutorizadoId") Integer agenteAutorizadoId,
            @PathVariable("usuarioId") Integer usuarioId);
}
