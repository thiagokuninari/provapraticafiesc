package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.Data;

import java.util.stream.Collectors;

@Data
public class UsuarioConsultaDto {

    private int id;
    private String nome;
    private String email;
    private String unidadeNegocioNome;
    private String empresaNome;
    private String situacao;

    public UsuarioConsultaDto(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.unidadeNegocioNome = usuario.getUnidadesNegocios().stream()
                .map(UnidadeNegocio::toString).collect(Collectors.joining(", "));
        this.empresaNome = usuario.getEmpresas().stream().map(Empresa::toString).collect(Collectors.joining(", "));
        this.situacao = usuario.getSituacao().toString();
    }

}
