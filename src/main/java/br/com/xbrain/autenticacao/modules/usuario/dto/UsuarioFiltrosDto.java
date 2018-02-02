package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import lombok.Data;

import java.util.List;

@Data
public class UsuarioFiltrosDto {

    private List<Integer> empresasIds;
    private List<Integer> unidadesNegocioIds;
    private List<Integer> usuariosAAsNacionais;
    private List<CodigoNivel> codigoNivelList;
    private List<CodigoCargo> codigoCargoList;
    private List<CodigoDepartamento> codigoDepartamentoList;
    private List<Integer> cidadesIds;
}
