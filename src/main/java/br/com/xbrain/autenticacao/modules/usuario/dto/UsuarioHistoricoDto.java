package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
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
        this.cadastro = DateUtil.dateTimeToString(usuarioHistorico.getDataCadastro());
        this.observacao = usuarioHistorico.getObservacao();
    }    

}
