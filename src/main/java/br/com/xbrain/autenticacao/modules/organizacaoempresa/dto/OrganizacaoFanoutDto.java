package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizacaoFanoutDto implements Serializable {

    private Integer organizacaoId;
    private CodigoNivel nivel;

    public static OrganizacaoFanoutDto of(OrganizacaoEmpresa organizacao) {
        return OrganizacaoFanoutDto.builder()
            .organizacaoId(organizacao.getId())
            .nivel(Optional.ofNullable(organizacao.getNivel())
                .map(Nivel::getCodigo)
                .orElse(null))
            .build();
    }
}
