package br.com.xbrain.autenticacao.modules.importacaousuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UsuarioImportacaoPlanilha {
    private final List<Integer> unidadesNegociosId = Arrays.asList(1, 2);
    private final List<Integer> empresasId = Arrays.asList(1, 2, 3);

    private Cargo cargo;
    private String nome;
    private String cpf;
    private String email;
    private boolean senhaPadrao;
    private LocalDateTime nascimento;
    private String telefone;
    private String senha;
    private Departamento departamento;
    private Nivel nivel;

    private Integer recuperarSenhaTentativa;
    private Eboolean alterarSenha;
    private LocalDateTime dataCadastro;
    private ESituacao situacao;
    private List<String> motivoNaoImportacao;

    public static Usuario of(UsuarioImportacaoPlanilha usuario) {
        Usuario response = new Usuario();
        BeanUtils.copyProperties(usuario, response);
        response.setRecuperarSenhaTentativa(usuario.getRecuperarSenhaTentativa());

        if (!isTelefoneCelular(usuario.getTelefone())) {
            response.setTelefone(null);
            response.setTelefone02(usuario.getTelefone());
        }

        return response;
    }

    private static boolean isTelefoneCelular(String telefone) {
        return telefone.length() > 14;
    }
}
