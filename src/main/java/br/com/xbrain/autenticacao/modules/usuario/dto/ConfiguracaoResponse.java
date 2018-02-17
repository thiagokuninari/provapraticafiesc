package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Configuracao;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ConfiguracaoResponse {

    private Integer id;
    private Integer ramal;

    public static ConfiguracaoResponse convertFrom(Configuracao configuracao) {
        ConfiguracaoResponse configuracaoResponse = new ConfiguracaoResponse();
        BeanUtils.copyProperties(configuracao, configuracaoResponse);
        return configuracaoResponse;
    }

}
