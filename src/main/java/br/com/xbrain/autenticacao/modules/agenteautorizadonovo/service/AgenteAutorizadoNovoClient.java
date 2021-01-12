package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(name = "agenteAutorizadoClient",
    url = "${app-config.services.agente-autorizado.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface AgenteAutorizadoNovoClient {

    @GetMapping("api/subordinados")
    Set<Integer> getIdUsuariosDoUsuario(@RequestParam Map<String, Object> requestParams);

    @GetMapping("api/agente-autorizado-por-cnpj")
    AgenteAutorizadoResponse getAaByCpnj(@RequestParam Map request);

    @GetMapping("api/id/{agenteAutorizadoId}")
    AgenteAutorizadoResponse getAaById(@PathVariable("agenteAutorizadoId") Integer agenteAutorizadoId);

    @GetMapping("api/empresas-matriz-filiais/{usuarioId}")
    List<EmpresaResponse> getEmpresasPermitidas(@PathVariable("usuarioId") Integer usuarioId);

    @GetMapping("api/{usuarioId}/estrutura")
    String getEstrutura(@PathVariable("usuarioId") Integer usuarioId);

    @GetMapping("api/{agenteAutorizadoId}/{buscarInativos}")
    List<UsuarioAgenteAutorizadoResponse> getUsuariosByAaId(
        @PathVariable("agenteAutorizadoId") Integer agenteAutorizadoId,
        @PathVariable("buscarInativos") Boolean buscarInativos);

    @GetMapping("api/agentes-autorizados-permitidos/{usuarioId}")
    List<Integer> getAasPermitidos(@PathVariable("usuarioId") Integer usuarioId);

    @GetMapping("api/possui-agente-autorizado-ativo-por-socio-email")
    boolean existeAaAtivoBySocioEmail(@RequestParam("usuarioEmail") String usuarioEmail);

    @GetMapping("api/possui-agente-autorizado-ativo-por-usuario-id")
    boolean existeAaAtivoByUsuarioId(@RequestParam("usuarioId") Integer usuarioId);
}
