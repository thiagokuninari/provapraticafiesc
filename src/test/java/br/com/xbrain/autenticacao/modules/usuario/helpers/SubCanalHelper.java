package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSubCanalId;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;

import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal.*;

public class SubCanalHelper {

    public static Set<SubCanal> umSetSubCanais() {
        return Set.of(umSubCanal(), doisSubCanal());
    }

    public static SubCanal umSubCanal() {
        return SubCanal.builder()
            .id(1)
            .codigo(ETipoCanal.PAP)
            .nome("PAP")
            .situacao(A)
            .novaChecagemCredito(Eboolean.F)
            .build();
    }

    public static SubCanal doisSubCanal() {
        return SubCanal.builder()
            .id(2)
            .codigo(ETipoCanal.PAP_PME)
            .nome("PAP PME")
            .situacao(A)
            .build();
    }

    public static SubCanalDto umSubCanalDto(Integer id, ETipoCanal codigo, String nome) {
        return SubCanalDto.builder()
            .id(id)
            .codigo(codigo)
            .nome(nome)
            .situacao(A)
            .novaChecagemCredito(Eboolean.F)
            .build();
    }

    public static List<UsuarioSubCanalId> umaListaDeUsuarioSubCanalIds() {
        return List.of(
            umUsuarioSubCanalId(10, "USUARIO TESTE PAP", PAP.getId()),
            umUsuarioSubCanalId(20, "USUARIO TESTE PAP PME", PAP_PME.getId()),
            umUsuarioSubCanalId(30, "USUARIO TESTE PAP PREMIUM", PAP_PREMIUM.getId()),
            umUsuarioSubCanalId(40, "USUARIO TESTE INSIDE SALES PME", INSIDE_SALES_PME.getId()),
            umUsuarioSubCanalId(50, "USUARIO TESTE PAP CONDOMINIO", PAP_CONDOMINIO.getId())
        );
    }

    public static UsuarioSubCanalId umUsuarioSubCanalId(Integer id, String nome, Integer subCanalId) {
        return new UsuarioSubCanalId(id, nome, subCanalId);
    }
}
