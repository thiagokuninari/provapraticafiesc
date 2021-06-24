package br.com.xbrain.autenticacao.modules.feeder.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.I;
import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.R;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.AGENTE_AUTORIZADO_SOCIO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendedoresFeederResponse {

    private int id;
    private String nome;
    private String situacao;
    private String nivelCodigo;

    public static VendedoresFeederResponse of(Usuario usuario) {
        var response = new VendedoresFeederResponse();

        BeanUtils.copyProperties(usuario, response);
        response.setNome(obterNome(usuario));
        response.setSituacao(usuario.getSituacao().toString());
        response.setNivelCodigo(usuario.getCargo().getNivel().getCodigo().name());

        return response;
    }

    private static String obterNome(Usuario usuario) {
        return validarSocioPrincipal(validarSituacao(usuario.getNome(), usuario.getSituacao()), usuario.getCargoCodigo());
    }

    private static String validarSituacao(String nome, ESituacao situacao) {
        return I.equals(situacao)
            ? nome.concat(" (INATIVO)")
            : R.equals(situacao)
            ? nome.concat(" (REALOCADO)")
            : nome;
    }

    private static String validarSocioPrincipal(String nome, CodigoCargo cargoCodigo) {
        if (Objects.nonNull(nome) && AGENTE_AUTORIZADO_SOCIO.equals(cargoCodigo)) {
            return nome.concat(" (SÓCIO PRINCIPAL)");
        }
        return nome;
    }
}
