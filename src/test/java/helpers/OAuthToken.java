package helpers;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto.EquipeTecnicaSupervisionadasResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.EquipeVendasSupervisionadasResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OAuthToken {

    @JsonProperty("access_token")
    private String accessToken;

    private String login;

    private String nome;

    private String email;

    private String usuarioId;

    private List<String> authorities;

    private List<String> aplicacoes;

    private String errorDescription;

    private String alterarSenha;

    private String nivel;

    private String nivelCodigo;

    private String cargo;

    private String cargoCodigo;

    private String departamento;

    private String departamentoCodigo;

    private Integer cargoId;

    private Integer departamentoId;

    private String cpf;

    private List<Integer> empresas;

    private List<Integer> unidadesNegocios;

    private List<String> empresasNome;

    private List<EquipeVendasSupervisionadasResponse> equipesSupervisionadas;

    private List<EquipeTecnicaSupervisionadasResponse> equipesTecnicasSupervisionadas;

    private List<Integer> agentesAutorizados;

    private List<SelectResponse> sites;
}
