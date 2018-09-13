package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UsuarioImportacaoRequest {
    //TODO:Deixar dinamico os valores de unidade de negocio e empresas
    private Cargo cargo;
    private String nome;
    private String cpf;
    private String email;
    private boolean senhaPadrao;
    private LocalDateTime nascimento;
    private String telefone;
    private String senha;
    private Eboolean alterarSenha;
    private LocalDateTime dataCadastro;
    private ESituacao situacao;
    private Departamento departamento;
    private List<String> motivoNaoImportacao = new ArrayList<>();
    private final List unidadesNegociosId = Arrays.asList(1, 2);
    private final List empresasId = Arrays.asList(1, 2, 3);

    public UsuarioImportacaoRequest() {
    }

    public UsuarioImportacaoRequest(UsuarioImportacaoRequest usuario) {
        BeanUtils.copyProperties(convertTo(usuario), this);
    }

    public static List<Usuario> convertTo(List<UsuarioImportacaoRequest> usuarios) {
        return usuarios.stream()
                .map(UsuarioImportacaoRequest::convertTo)
                .collect(Collectors.toList());
    }

    public static Usuario convertTo(UsuarioImportacaoRequest usuario) {
        Usuario response = new Usuario();
        BeanUtils.copyProperties(usuario, response);
        return response;
    }
}
