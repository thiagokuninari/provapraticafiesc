package br.com.xbrain.autenticacao.modules.feeder.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

import static br.com.xbrain.autenticacao.modules.feeder.service.FeederUtil.CODIGOS_CARGOS_VENDEDORES_FEEDER;
import static br.com.xbrain.autenticacao.modules.feeder.service.FeederUtil.CODIGOS_CARGOS_VENDEDORES_FEEDER_E_SOCIO_PRINCIPAL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendedoresFeederFiltros {

    @NotEmpty
    private List<Integer> aasIds;
    @NotNull
    private Boolean comSocioPrincipal;
    private Boolean buscarInativos;

    public List<ESituacao> obterSituacoes() {
        return Objects.nonNull(buscarInativos) && buscarInativos
            ? List.of(ESituacao.A, ESituacao.I, ESituacao.R)
            : List.of(ESituacao.A);
    }

    public Predicate toPredicate(List<Integer> usuariosIds) {
        var predicate = new UsuarioPredicate();

        predicate.comIds(usuariosIds);
        predicate.comCodigosNiveis(List.of(CodigoNivel.AGENTE_AUTORIZADO, CodigoNivel.OPERACAO));
        predicate.comSituacoes(obterSituacoes());

        if (comSocioPrincipal) {
            predicate.comCodigosCargos(CODIGOS_CARGOS_VENDEDORES_FEEDER_E_SOCIO_PRINCIPAL);
        } else {
            predicate.comCodigosCargos(CODIGOS_CARGOS_VENDEDORES_FEEDER);
        }

        return predicate.build();
    }
}
