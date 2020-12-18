package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetLoginLogoutHojeRequest {

    private Set<Integer> usuariosIds;
    private Integer page;
    private Integer size;
    private String orderBy;
    private String orderDirection;

    public static GetLoginLogoutHojeRequest of(
        Optional<? extends Collection<Integer>> usuariosIds,
        PageRequest pageRequest) {
        var request = new GetLoginLogoutHojeRequest();
        BeanUtils.copyProperties(pageRequest, request);
        request.setUsuariosIds(usuariosIds.map(Set::copyOf).orElse(null));
        return request;
    }
}
