package br.com.xbrain.autenticacao.modules.permissao.dto;

import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CargoDepartamentoFuncionalidadeResponse {

    private Integer id;
    private String nivelNome;
    private String cargoNome;
    private String departamentoNome;
    private Integer funcionalidadeId;
    private String funcionalidadeNome;
    private String aplicacaoNome;

    public CargoDepartamentoFuncionalidadeResponse() {
    }

    public CargoDepartamentoFuncionalidadeResponse(CargoDepartamentoFuncionalidade objRequest) {
        BeanUtils.copyProperties(convertFrom(objRequest), this);
    }

    public static List<CargoDepartamentoFuncionalidadeResponse> convertFrom(List<CargoDepartamentoFuncionalidade> lista) {
        return !CollectionUtils.isEmpty(lista)
                ? lista.stream()
                .map(CargoDepartamentoFuncionalidadeResponse::convertFrom)
                .collect(Collectors.toList())
                : null;
    }

    public static CargoDepartamentoFuncionalidadeResponse convertFrom(CargoDepartamentoFuncionalidade request) {
        CargoDepartamentoFuncionalidadeResponse response = new CargoDepartamentoFuncionalidadeResponse();
        BeanUtils.copyProperties(request, response);
        response.setNivelNome(request.getCargo().getNivel().getNome());
        response.setCargoNome(request.getCargo().getNome());
        response.setDepartamentoNome(request.getDepartamento().getNome());
        response.setFuncionalidadeId(request.getFuncionalidade().getId());
        response.setFuncionalidadeNome(request.getFuncionalidade().getNome());
        response.setAplicacaoNome(request.getFuncionalidade().getAplicacao().getNome());
        return response;
    }
}
