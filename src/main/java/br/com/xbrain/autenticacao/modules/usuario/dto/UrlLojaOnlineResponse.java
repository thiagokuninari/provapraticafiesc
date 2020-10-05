package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlLojaOnlineResponse {

    private String urlLojaBase;
    private String urlLojaProspect;
    private String urlLojaProspectNextel;
    private String cupomLojaOnline;

    public static UrlLojaOnlineResponse of(Usuario usuario) {
        var urls = new UrlLojaOnlineResponse();
        BeanUtils.copyProperties(usuario, urls);
        urls.setCupomLojaOnline(usuario.getCupomLoja());
        return urls;
    }
}

