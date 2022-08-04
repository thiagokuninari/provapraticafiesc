package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.comum.dto.*;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoAgendamentoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCidadeDto;
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
    String API_COLABORADOR_VENDAS = "api/colaboradores-vendas";
    String API_USUARIO_AGENTE_AUTORIZADO = "api/usuarios-agente-autorizado";
    String API_AGENTE_AUTORIZADO_USUARIO = "api/agentes-autorizados-usuario";
    String API_SOCIO = "api/socio";

    // todo mover para colaborador-vendas-api quando finalizado
    @GetMapping(API_USUARIO_AGENTE_AUTORIZADO + "/usuarios-com-d2d/{agenteAutorizadoId}")
    List<UsuarioAgenteAutorizadoResponse> getUsuariosAaAtivoComVendedoresD2D(
        @PathVariable("agenteAutorizadoId") Integer agenteAutorizadoId);

    // todo mover para colaborador-vendas-api quando finalizado
    @PutMapping(API_COLABORADOR_VENDAS + "/limpar-cpf-agente-autorizado")
    void limparCpfAgenteAutorizado(@RequestParam("email") String email);

    // todo mover para colaborador-vendas-api quando finalizado
    @GetMapping(API_AGENTE_AUTORIZADOS_USUARIO + "/recupera-emails")
    List<String> recuperarColaboradoresDoAgenteAutorizado(@RequestParam(name = "cnpj") String cnpj);

    // todo mover para colaborador-vendas-api quando finalizado
    @GetMapping(API_USUARIO_AGENTE_AUTORIZADO + "/{agenteAutorizadoId}/canal/usuario/{usuarioId}")
    List<UsuarioAgenteAutorizadoAgendamentoResponse> getUsuariosByAaIdCanalDoUsuario(
        @PathVariable("agenteAutorizadoId") Integer agenteAutorizadoId,
        @PathVariable("usuarioId") Integer usuarioId);

    @GetMapping(API_AGENTE_AUTORIZADOS_USUARIO + "/superiores/usuario-autenticado")
    List<Integer> getUsuariosIdsSuperioresPol();

    @GetMapping(API_AGENTE_AUTORIZADO_USUARIO + "/subordinados")
    List<Integer> getIdsUsuariosPermitidosDoUsuario(@RequestParam Map request);

    @GetMapping("api/clusters/permitidos")
    List<ClusterDto> getClusters(@RequestParam("grupoId") Integer grupoId);

    @GetMapping("api/grupos/permitidos")
    List<GrupoDto> getGrupos(@RequestParam("regionalId") Integer regionalId);

    @GetMapping("api/regionais/permitidos")
    List<RegionalDto> getRegionais();

    @GetMapping("api/subclusters/permitidos")
    List<SubClusterDto> getSubclusters(@RequestParam("clusterId") Integer clusterId);

    @GetMapping("api/cidades/comunicados")
    List<UsuarioCidadeDto> getCidades(@RequestParam("subclusterId") Integer subclusterId);

    @PutMapping(API_AGENTE_AUTORIZADOS_USUARIO + "/inativar/socio-principal")
    void inativarAntigoSocioPrincipal(@RequestParam("email") String email);

    @PutMapping(API_AGENTE_AUTORIZADOS_USUARIO + "/inativar-email/{idSocioPrincipal}")
    void atualizarEmailSocioPrincipalInativo(@RequestParam("emailInativo") String emailInativo,
                                             @PathVariable("idSocioPrincipal") Integer idSocioPrincipal);

    @PutMapping(API_SOCIO + "/inativar-email")
    void atualizarEmailSocioInativo(@RequestParam("emailAtual") String emailAtual);
}
