package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import br.com.xbrain.xbrainutils.DateUtils;
import lombok.Data;

@Data
public class UsuarioHistoricoDto {
    
    private Integer id;
    private String motivo;
    private String observacao;
    private String cadastro;

    public UsuarioHistoricoDto() {
    }
    
    public UsuarioHistoricoDto(UsuarioHistorico usuarioHistorico) {
        this.id = usuarioHistorico.getId();
        this.motivo = usuarioHistorico.getMotivoInativacao().getDescricao();
        this.cadastro = DateUtils.parseLocalDateTimeToString(usuarioHistorico.getDataCadastro());
        this.observacao = usuarioHistorico.getObservacao();
    }    

}
