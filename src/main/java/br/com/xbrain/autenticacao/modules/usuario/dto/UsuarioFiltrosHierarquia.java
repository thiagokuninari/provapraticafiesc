package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import lombok.Data;

import java.util.List;

@Data
public class UsuarioFiltrosHierarquia {

    private List<Integer> usuarioId;

    private CodigoNivel codigoNivel;

    private CodigoDepartamento codigoDepartamento;

    private CodigoCargo codigoCargo;

}
