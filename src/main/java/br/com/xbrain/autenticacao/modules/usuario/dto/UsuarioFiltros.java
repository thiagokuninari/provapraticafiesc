package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UsuarioFiltros {

    private Integer id;
    private String nome;
    private String emailUsuario;
    private String cpf;
    private String cnpjAa;
    private Integer regionalId;
    private Integer ufId;
    private List<Integer> unidadeNegocioIds;
    private Integer nivelId;
    private Integer departamentoId;
    private Integer cargoId;
    private ECanal canal;
    private Integer subCanalId;
    private List<ESituacao> situacoes;
    private Integer organizacaoId;
    private List<Integer> excluiIds;
    private List<CodigoCargo> codigosCargos;

    @JsonIgnore
    public UsuarioPredicate toPredicate() {
        return new UsuarioPredicate()
            .comCpf(cpf)
            .comNome(nome)
            .comEmail(emailUsuario)
            .comCanal(canal)
            .comSubCanal(subCanalId)
            .comSituacoes(situacoes)
            .comRegional(regionalId)
            .comUf(ufId)
            .comUnidadeNegocio(unidadeNegocioIds)
            .comOrganizacaoEmpresaId(organizacaoId)
            .comNivel(!ObjectUtils.isEmpty(nivelId)
                ? Collections.singletonList(nivelId) : null)
            .comCargo(!ObjectUtils.isEmpty(cargoId)
                ? Collections.singletonList(cargoId) : null)
            .comDepartamento(!ObjectUtils.isEmpty(departamentoId)
                ? Collections.singletonList(departamentoId) : null)
            .excluiIds(excluiIds)
            .comCodigosCargos(codigosCargos)
            .comId(id);
    }
}
