package br.com.xbrain.autenticacao.modules.permissao.helper;

import br.com.xbrain.autenticacao.modules.permissao.model.FuncionalidadeCanal;
import br.com.xbrain.autenticacao.modules.permissao.model.FuncionalidadeCanalPk;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;

public class FuncionalidadeCanalHelper {

    public static FuncionalidadeCanal umaFuncionalidadeCanal(Integer funcionalidadeId, ECanal canal) {
        var funcionalidadeCanalPk = new FuncionalidadeCanalPk();
        funcionalidadeCanalPk.setFuncionalidade(funcionalidadeId);
        funcionalidadeCanalPk.setCanal(canal);
        var funcionalidadeCanal = new FuncionalidadeCanal();
        funcionalidadeCanal.setFuncionalidadeCanalPk(funcionalidadeCanalPk);

        return funcionalidadeCanal;
    }
}
