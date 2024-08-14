package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.EFormatoDataHora;
import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioVendasPapIndiretoResponse {

    private Integer usuarioId;
    private String nome;
    private String cpf;
    private String dataCadastro;
    private String situacao;

    public static UsuarioVendasPapIndiretoResponse of(Usuario usuario) {
        var response = new UsuarioVendasPapIndiretoResponse();

        response.setUsuarioId(usuario.getId());
        response.setNome(usuario.getNome());
        response.setCpf(usuario.getCpf());
        response.setSituacao(usuario.getSituacao().getDescricao());
        response.setDataCadastro(DateUtil.formatarDataHora(EFormatoDataHora.DATA_HORA_SEG, usuario.getDataCadastro()));

        return response;
    }
}
