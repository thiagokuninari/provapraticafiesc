package br.com.xbrain.autenticacao.modules.parceirosonline.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeeder;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgenteAutorizadoFiltro {

    private Integer id;
    private List<Integer> ids;
    private String razaoSocial;
    private String cnpj;
    private List<Integer> situacoesIds;
    private Integer situacaoId;
    private String nomeFantasia;
    private String email;
    private String telefone;
    private List<Integer> contratosTiposIds;
    private List<Integer> unidadesNegociosIds;
    private Integer grupoId;
    private Integer regionalId;
    private Integer clusterId;
    private Integer subClusterId;
    private Integer usuarioProprietario;
    private List<Integer> usuariosDaCarteira;

    private List<Integer> cidadesIds;
    private Integer usuarioId;
    private List<Integer> cidadesIdsVisualizacaoPorCidades;
    private List<Integer> estadosIds;
    private Eboolean nacional;
    private Integer discadoraId;
    private Boolean possuiDiscadora;
    private Eboolean possuiPermissaoFeeder;
    private ETipoFeeder tipoFeeder;
    private List<ETipoFeeder> tiposFeeder;

}
