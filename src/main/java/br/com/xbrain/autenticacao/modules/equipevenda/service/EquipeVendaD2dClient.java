package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaSupervisorDto;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "equipeVendaClient",
    url = "${app-config.services.equipe-venda.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface EquipeVendaD2dClient {

    String EQUIPE_VENDAS_ENDPOINT = "api/equipes-vendas";
    String USUARIOS_EQUIPE_ENDPOINT = "api/usuarios-equipe";
    String PAUSA_AGENDADA_ENDPOINT = "api/pausa-agendada";

    @GetMapping(PAUSA_AGENDADA_ENDPOINT + "/verificar-pausa/{username}")
    boolean verificarPausaEmAndamento(@PathVariable("username") String username);

    @GetMapping(EQUIPE_VENDAS_ENDPOINT + "/usuario")
    List<EquipeVendaDto> getUsuario(@RequestParam Map request);

    @GetMapping(EQUIPE_VENDAS_ENDPOINT + "/usuario-equipe")
    List<EquipeVendaUsuarioResponse> getUsuariosPermitidos(@RequestParam("cargos") List<CodigoCargo> cargos);

    @GetMapping(EQUIPE_VENDAS_ENDPOINT + "/usuario-sem-equipe")
    List<Integer> filtrarUsuariosComEquipeByUsuarioIdInOuNaEquipe(@RequestParam("usuariosId") List<Integer> usuariosId,
                                                                  @RequestParam("equipeId") Integer equipeId);

    @GetMapping(USUARIOS_EQUIPE_ENDPOINT + "/select")
    List<SelectResponse> getVendedoresPorEquipe(@RequestParam Map filtros);

    @GetMapping(USUARIOS_EQUIPE_ENDPOINT + "/select/usuarios")
    List<SelectResponse> getUsuariosDaEquipe(@RequestParam Map filtros);

    @GetMapping(EQUIPE_VENDAS_ENDPOINT + "/usuario")
    List<EquipeVendaSupervisorDto> getUsuarioComSupervisor(@RequestParam Map request);

    @GetMapping(EQUIPE_VENDAS_ENDPOINT + "/equipe-por-supervisor/{supervisorId}")
    List<Integer> getEquipeVendaBySupervisorId(@PathVariable("supervisorId") Integer supervisorId);

    @GetMapping(EQUIPE_VENDAS_ENDPOINT + "/sub-canais/{usuarioId}")
    List<Integer> getSubCanaisDaEquipeVendaD2dByUsuarioId(@PathVariable("usuarioId") Integer usuarioId);
}
