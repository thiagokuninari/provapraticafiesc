package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.model.*;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CidadeTest {

    @Test
    public void getNomeComUf_deveRetornarNomeComUf_quandoUfNaoNull() {
        assertThat(umaCidade(1).getNomeComUf())
            .isEqualTo("Londrina - PR");
    }

    @Test
    public void getNomeComUf_deveRetornarNome_quandoUfNull() {
        var cidade = umaCidade(1);
        cidade.setUf(null);
        assertThat(cidade.getNomeComUf())
            .isEqualTo("Londrina");
    }

    @Test
    public void getRegionalId_deveRetornarRegionalId_quandoRegionalNaoNull() {
        assertThat(umaCidade(1).getRegionalId())
            .isEqualTo(3);
    }

    @Test
    public void getRegionalId_deveRetornarNull_quandoRegionalNull() {
        var cidade = umaCidade(1);
        cidade.setRegional(null);
        assertThat(cidade.getRegionalId())
            .isNull();
    }

    @Test
    public void getRegionalNome_deveRetornarNomeDaRegional_quandoRegionalNaoNull() {
        assertThat(umaCidade(1).getRegionalNome())
            .isEqualTo("Regional");
    }

    @Test
    public void getRegionalNome_deveRetornarNull_quandoRegionalNull() {
        var cidade = umaCidade(1);
        cidade.setRegional(null);
        assertThat(cidade.getRegionalNome())
            .isNull();
    }

    @Test
    public void getGrupoId_deveRetornarGrupoId_quandoSolicitado() {
        assertThat(umaCidade(1).getGrupoId())
            .isEqualTo(4);
    }

    @Test
    public void getGrupoNome_deveRetornarGrupoNome_quandoSolicitado() {
        assertThat(umaCidade(1).getGrupoNome())
            .isEqualTo("Grupo");
    }

    @Test
    public void getClusterId_deveRetornarClusterId_quandoSolicitado() {
        assertThat(umaCidade(1).getClusterId())
            .isEqualTo(5);
    }

    @Test
    public void getClusterNome_deveRetornarClusterNome_quandoSolicitado() {
        assertThat(umaCidade(1).getClusterNome())
            .isEqualTo("Cluster");
    }

    @Test
    public void getSubClusterId_deveRetornarSubClusterId_quandoSolicitado() {
        assertThat(umaCidade(1).getSubClusterId())
            .isEqualTo(6);
    }

    @Test
    public void getSubClusterNome_deveRetornarSubClusterNome_quandoSolicitado() {
        assertThat(umaCidade(1).getSubClusterNome())
            .isEqualTo("SubCluster");
    }

    @Test
    public void getIdUf_deveRetornarUfId_quandoUfNaoNull() {
        assertThat(umaCidade(1).getIdUf())
            .isEqualTo(2);
    }

    @Test
    public void getIdUf_deveRetornarNull_quandoUfNull() {
        var cidade = umaCidade(1);
        cidade.setUf(null);
        assertThat(cidade.getIdUf())
            .isNull();
    }

    @Test
    public void getNomeUf_deveRetornarUfNome_quandoUfNaoNull() {
        assertThat(umaCidade(1).getNomeUf())
            .isEqualTo("Paraná");
    }

    @Test
    public void getNomeUf_deveRetornarNull_quandoUfNull() {
        var cidade = umaCidade(1);
        cidade.setUf(null);
        assertThat(cidade.getNomeUf())
            .isNull();
    }

    @Test
    public void of_deveRetornarListaCidade_quandoSolicitado() {
        assertThat(Cidade.of(List.of(1, 2)))
            .extracting("id")
            .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    public void of_deveRetornarListaIds_quandoSolicitado() {
        assertThat(Cidade.convertFrom(Set.of(
            umaCidade(1),
            umaCidade(2),
            umaCidade(3))))
            .isEqualTo(Set.of(1, 2, 3));
    }

    @Test
    public void getCodigoUf_deveRetornarCodigoUf_quandoUfNaoNull() {
        assertThat(umaCidade(1).getCodigoUf())
            .isEqualTo("PR");
    }

    @Test
    public void getCodigoUf_naoDeveRetornarCodigoUfNulo_quandoUfNull() {
        var cidade = umaCidade(1);
        cidade.setUf(null);
        assertThat(cidade.getCodigoUf())
            .isEqualTo(null);
    }

    private Cidade umaCidade(Integer cidadeId) {
        return Cidade.builder()
            .id(cidadeId)
            .nome("Londrina")
            .uf(umaUf())
            .regional(umaRegional())
            .subCluster(umSubCluster())
            .build();
    }

    private Regional umaRegional() {
        return Regional.builder()
            .id(3)
            .nome("Regional")
            .build();
    }

    private Uf umaUf() {
        return Uf.builder()
            .id(2)
            .uf("PR")
            .nome("Paraná")
            .build();
    }

    private SubCluster umSubCluster() {
        return SubCluster.builder()
            .id(6)
            .nome("SubCluster")
            .cluster(umCluster())
            .build();
    }

    private Cluster umCluster() {
        return Cluster.builder()
            .id(5)
            .nome("Cluster")
            .grupo(umGrupo())
            .build();
    }

    private Grupo umGrupo() {
        return Grupo.builder()
            .id(4)
            .nome("Grupo")
            .build();
    }
}
