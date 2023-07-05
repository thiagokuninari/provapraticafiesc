package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.UsuarioDtoVendas;
import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.AgenteAutorizadoUsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioRequest;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(name = "agenteAutorizadoNovoClient",
    url = "${app-config.services.agente-autorizado.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface AgenteAutorizadoNovoClient {

    String URL_AGENTE_AUTORIZADO = "api";

    @GetMapping(URL_AGENTE_AUTORIZADO + "/todos-usuarios-dos-aas")
    List<UsuarioDtoVendas> buscarTodosUsuariosDosAas(@RequestParam("aasIds") List<Integer> aasIds,
                                                     @RequestParam("buscarInativos") Boolean buscarInativos);

    @GetMapping(URL_AGENTE_AUTORIZADO + "/subordinados")
    Set<Integer> getIdUsuariosDoUsuario(@RequestParam Map<String, Object> requestParams);

    @GetMapping(URL_AGENTE_AUTORIZADO + "/subordinados")
    List<Integer> getIdsUsuariosPermitidosDoUsuario(@RequestParam Map request);

    @GetMapping(URL_AGENTE_AUTORIZADO + "/agente-autorizado-por-cnpj")
    AgenteAutorizadoResponse getAaByCpnj(@RequestParam Map request);

    @GetMapping(URL_AGENTE_AUTORIZADO + "/id/{agenteAutorizadoId}")
    AgenteAutorizadoResponse getAaById(@PathVariable("agenteAutorizadoId") Integer agenteAutorizadoId);

    @GetMapping(URL_AGENTE_AUTORIZADO + "/empresas-matriz-filiais/{usuarioId}")
    List<EmpresaResponse> getEmpresasPermitidas(@PathVariable("usuarioId") Integer usuarioId);

    @GetMapping(URL_AGENTE_AUTORIZADO + "/{usuarioId}/estrutura")
    String getEstruturaByUsuarioIdAndAtivo(@PathVariable("usuarioId") Integer usuarioId);

    @GetMapping(URL_AGENTE_AUTORIZADO + "/{usuarioId}/estrutura/sem-situacao")
    String getEstruturaByUsuarioId(@PathVariable("usuarioId") Integer usuarioId);

    @GetMapping(URL_AGENTE_AUTORIZADO + "/usuarios-agente-autorizado/{agenteAutorizadoId}/{buscarInativos}")
    List<UsuarioAgenteAutorizadoResponse> getUsuariosByAaId(
        @PathVariable("agenteAutorizadoId") Integer agenteAutorizadoId,
        @PathVariable("buscarInativos") Boolean buscarInativos);

    @GetMapping(URL_AGENTE_AUTORIZADO + "/agentes-autorizados-permitidos/{usuarioId}")
    List<Integer> getAasPermitidos(@PathVariable("usuarioId") Integer usuarioId);

    @GetMapping(URL_AGENTE_AUTORIZADO + "/possui-agente-autorizado-ativo-por-socio-email")
    boolean existeAaAtivoBySocioEmail(@RequestParam("usuarioEmail") String usuarioEmail);

    @GetMapping(URL_AGENTE_AUTORIZADO + "/possui-agente-autorizado-ativo-por-usuario-id")
    boolean existeAaAtivoByUsuarioId(@RequestParam("usuarioId") Integer usuarioId);

    @PostMapping(URL_AGENTE_AUTORIZADO + "/agente-autorizados")
    List<AgenteAutorizadoUsuarioDto> getAgenteAutorizadosUsuarioDtosByUsuarioIds(
        @RequestBody UsuarioRequest request);

    @GetMapping(URL_AGENTE_AUTORIZADO + "/carteira/{usuarioId}/agentes-autorizados")
    List<AgenteAutorizadoResponse> findAgenteAutorizadoByUsuarioId(@PathVariable("usuarioId") Integer usuarioId);

    @GetMapping(URL_AGENTE_AUTORIZADO + "/carteira/usuarios-agentes-autorizados")
    List<AgenteAutorizadoResponse> findAgentesAutorizadosByUsuariosIds(
        @RequestParam("usuariosIds") List<Integer> usuarioId, @RequestParam("incluirAasInativos") Boolean incluirAasInativos);
}
