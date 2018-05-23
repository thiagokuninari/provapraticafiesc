package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Configuracao;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

@Data
public class ConfiguracaoResponse {

    private Integer id;
    private Integer ramal;
    private Integer usuarioId;

    public static ConfiguracaoResponse convertFrom(Configuracao configuracao) {
        ConfiguracaoResponse configuracaoResponse = new ConfiguracaoResponse();
        BeanUtils.copyProperties(configuracao, configuracaoResponse);
        if (!ObjectUtils.isEmpty(configuracao.getUsuario())) {
            configuracaoResponse.setUsuarioId(configuracao.getUsuario().getId());
        }
        return configuracaoResponse;
    }

}
