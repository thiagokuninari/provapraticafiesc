package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import lombok.Data;

import java.util.List;

@Data
public class UsuarioMqRequest {

    private String cpf;
    private String nome;
    private String email;
    private CodigoNivel nivel;
    private CodigoDepartamento departamento;
    private CodigoCargo cargo;
    private List<CodigoUnidadeNegocio> unidadesNegocio;
    private CodigoEmpresa empresa;

}
