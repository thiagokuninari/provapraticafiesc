package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLogadoResponse {

    private Integer usuarioId;
    private String nome;
    private String email;
    private String fornecedorNome;
    private LocalDateTime dataEntrada;

    public void setDadosResponse(Usuario usuario) {
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.fornecedorNome = usuario.getOrganizacaoEmpresa().getDescricao();
    }
}
