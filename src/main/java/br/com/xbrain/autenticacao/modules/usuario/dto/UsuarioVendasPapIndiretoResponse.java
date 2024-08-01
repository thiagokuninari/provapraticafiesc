package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioDtoVendas;
import br.com.xbrain.autenticacao.modules.comum.enums.EFormatoDataHora;
import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
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
    private Integer agenteAutorizadoId;
    private String cnpjAa;
    private String razaoSocialAa;
    private String dataCadastro;
    private String situacao;
    private String dataSaidaCnpj;

    public static UsuarioVendasPapIndiretoResponse of(UsuarioResponse usuario, UsuarioDtoVendas aaUsuario) {
        var response = new UsuarioVendasPapIndiretoResponse();

        response.setUsuarioId(aaUsuario.getId());
        response.setCnpjAa(aaUsuario.getAgenteAutorizadoCnpj());
        response.setRazaoSocialAa(aaUsuario.getAgenteAutorizadoRazaoSocial());
        response.setAgenteAutorizadoId(aaUsuario.getAgenteAutorizadoId());
        response.setNome(usuario.getNome());
        response.setCpf(usuario.getCpf());
        response.setDataCadastro(DateUtil.formatarDataHora(EFormatoDataHora.DATA_HORA_SEG, usuario.getDataCadastro()));
        response.setDataSaidaCnpj(DateUtil.formatarDataHora(EFormatoDataHora.DATA_HORA_SEG, usuario.getDataSaidaCnpj()));
        response.setSituacao(response.getDataSaidaCnpj().isBlank()
            ? usuario.getSituacao().getDescricao()
            : "REMANEJADO");

        return response;
    }
}
