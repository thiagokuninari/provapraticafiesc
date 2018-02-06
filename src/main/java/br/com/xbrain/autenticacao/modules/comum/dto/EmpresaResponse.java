package br.com.xbrain.autenticacao.modules.comum.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class EmpresaResponse {

    private Integer id;
    private String nome;
    private CodigoEmpresa codigo;

    public static EmpresaResponse convertFrom(Empresa empresa) {
        EmpresaResponse empresaResponse = new EmpresaResponse();
        BeanUtils.copyProperties(empresa, empresaResponse);
        return empresaResponse;
    }

}
