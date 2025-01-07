package br.com.xbrain.autenticacao.modules.importacao.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UsuarioImportacao {

    private Integer id;
    private String nome;
    private String email;
    private String email02;
    private String email03;
    private String telefone;
    private String telefone02;
    private String telefone03;
    private String cpf;
    private String rg;
    private String orgaoExpedidor;
    private String loginNetSales;
    private LocalDateTime nascimento;
    private Integer unidadeNegocioId;
    private List<Integer> cidadesId;
    private List<Integer> empresasId;
    private List<Integer> usuariosHierarquiaIds;
    private Integer cargoId;
    private Integer departamentoId;
    private LocalDateTime dataCadastro;
    private Integer usuarioCadastroId;
    private String senha;
    private Eboolean alterarSenha;
    private ESituacao situacao;

}
