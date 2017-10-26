package br.com.xbrain.autenticacao.modules.autenticacao.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties
public class UsuarioAutenticado {

    private int id;
    private String nome;
    private String email;
    private String cargo;
    private String departamento;
    private String nivel;
    private String cpf;
    private LocalDateTime dataInativacao;

    public UsuarioAutenticado(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.cargo = usuario.getCargo().getCodigo();
        this.departamento = usuario.getDepartamento().getCodigo();
        this.nivel = usuario.getCargo().getNivel().getCodigo();
        this.cpf = usuario.getCpf();
        this.dataInativacao = usuario.getDataInativacao();
    }
}
