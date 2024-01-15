package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.dto.GrupoDto;
import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
import br.com.xbrain.autenticacao.modules.comum.dto.SubClusterDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCidadeDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "parceirosOnlineClient",
    url = "${app-config.services.parceiros-online.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface ParceirosOnlineClient {

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

}
