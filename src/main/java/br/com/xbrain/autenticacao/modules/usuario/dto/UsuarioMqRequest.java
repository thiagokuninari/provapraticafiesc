package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UsuarioMqRequest {

    private Integer id;
    private String cpf;
    private String nome;
    private String email;
    private String email02;
    private String telefone;
    private String telefone02;
    private String telefone03;
    private String rg;
    private String loginNetSales;
    private String orgaoExpedidor;
    private ESituacao situacao;
    private LocalDateTime nascimento;
    private CodigoNivel nivel;
    private CodigoDepartamento departamento;
    private CodigoCargo cargo;
    private List<CodigoUnidadeNegocio> unidadesNegocio;
    private List<CodigoEmpresa> empresa;
    private Integer usuarioCadastroId;
    private String exception;

}
