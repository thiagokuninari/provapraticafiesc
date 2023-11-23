package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.EMPRESARIAL;
import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.RESIDENCIAL;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioMsoAnalistaClaroPessoal;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioConsultaDtoTest  {

    @Test
    @SuppressWarnings("LineLength")
    public void convertFrom_deveMontarObjetoUsuarioConsultaDto_seSolicitado() {
        var usuario = umUsuarioMsoAnalistaClaroPessoal();
        usuario.setTiposFeeder(Set.of(ETipoFeederMso.EMPRESARIAL, ETipoFeederMso.RESIDENCIAL));
        usuario.setUnidadesNegocios(List.of(new UnidadeNegocio(1)));
        usuario.setEmpresas(List.of(new Empresa(1)));

        assertThat(UsuarioConsultaDto.convertFrom(usuario))
            .extracting("nome", "email", "tiposFeeder")
            .containsExactly("MSO ANALISTA ADM CLARO PESSOAL", "MSO_ANALISTAADM_CLAROMOVEL_PESSOAL@NET.COM.BR", Set.of(EMPRESARIAL, RESIDENCIAL));
    }
}
