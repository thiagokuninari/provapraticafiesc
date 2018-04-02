package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.Collections;

@Data
public class UsuarioFiltros {

    private String nome;
    private String cpf;
    private Integer regionalId;
    private Integer grupoId;
    private Integer clusterId;
    private Integer unidadeNegocioId;
    private Integer subClusterId;
    private Integer nivelId;
    private Integer departamentoId;
    private Integer cargoId;

    @JsonIgnore
    public UsuarioPredicate toPredicate() {
        return new UsuarioPredicate()
                .comCpf(cpf)
                .comNome(nome)
                .comGrupo(grupoId)
                .comCluster(clusterId)
                .comRegional(regionalId)
                .comSubCluster(subClusterId)
                .comUnidadeNegocio(unidadeNegocioId)
                .comNivel(!ObjectUtils.isEmpty(nivelId)
                        ? Collections.singletonList(nivelId) : null)
                .comCargo(!ObjectUtils.isEmpty(cargoId)
                        ? Collections.singletonList(cargoId) : null)
                .comDepartamento(!ObjectUtils.isEmpty(departamentoId)
                        ? Collections.singletonList(departamentoId) : null);
    }
}
