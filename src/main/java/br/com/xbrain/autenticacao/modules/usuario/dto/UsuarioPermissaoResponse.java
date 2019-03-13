package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.permissao.dto.CargoDepartamentoFuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UsuarioPermissaoResponse {

    private List<CargoDepartamentoFuncionalidadeResponse> permissoesCargoDepartamento;
    private List<FuncionalidadeResponse> permissoesEspeciais;

    public static UsuarioPermissaoResponse of(
            List<CargoDepartamentoFuncionalidade> cargoDepartamentoFuncionalidades,
            List<Funcionalidade> funcionalidades) {
        return UsuarioPermissaoResponse
                .builder()
                .permissoesCargoDepartamento(CargoDepartamentoFuncionalidadeResponse
                        .convertFrom(cargoDepartamentoFuncionalidades))
                .permissoesEspeciais(FuncionalidadeResponse.convertFrom(funcionalidades))
                .build();
    }
}
