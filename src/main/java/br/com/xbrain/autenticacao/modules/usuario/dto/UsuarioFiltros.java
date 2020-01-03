package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;

@Data
public class UsuarioFiltros {

    private String nome;
    private String emailUsuario;
    private String cpf;
    private String cnpjAa;
    private Integer regionalId;
    private Integer grupoId;
    private Integer clusterId;
    private Integer unidadeNegocioId;
    private Integer subClusterId;
    private Integer nivelId;
    private Integer departamentoId;
    private Integer cargoId;
    private ECanal canal;
    private List<ESituacao> situacoes;
    private Integer organizacaoId;

    @JsonIgnore
    public UsuarioPredicate toPredicate() {
        return new UsuarioPredicate()
            .comCpf(cpf)
            .comNome(nome)
            .comEmail(emailUsuario)
            .comCanal(canal)
            .comSituacoes(situacoes)
            .comGrupo(grupoId)
            .comCluster(clusterId)
            .comRegional(regionalId)
            .comSubCluster(subClusterId)
            .comUnidadeNegocio(unidadeNegocioId)
            .comOrganizacaoId(organizacaoId)
            .comNivel(!ObjectUtils.isEmpty(nivelId)
                ? Collections.singletonList(nivelId) : null)
            .comCargo(!ObjectUtils.isEmpty(cargoId)
                ? Collections.singletonList(cargoId) : null)
            .comDepartamento(!ObjectUtils.isEmpty(departamentoId)
                ? Collections.singletonList(departamentoId) : null);
    }
}
