package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.permissao.dto.CargoDepartamentoFuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import lombok.Data;

import java.util.List;

@Data
public class UsuarioPermissaoResponse {

    private List<CargoDepartamentoFuncionalidadeResponse> permissoesCargoDepartamento;
    private List<FuncionalidadeResponse> permissoesEspeciais;

    public void setPermissoesCargoDepartamento(List<CargoDepartamentoFuncionalidade> lista) {
        this.permissoesCargoDepartamento = CargoDepartamentoFuncionalidadeResponse.convertFrom(lista);
    }

    public void setPermissoesEspeciais(List<Funcionalidade> lista) {
        this.permissoesEspeciais = FuncionalidadeResponse.convertFrom(lista);
    }
}
