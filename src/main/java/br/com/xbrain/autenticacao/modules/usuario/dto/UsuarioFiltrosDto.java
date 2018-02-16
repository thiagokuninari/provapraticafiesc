package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UsuarioFiltrosDto {

    private List<Integer> empresasIds = new ArrayList<>();
    private List<Integer> unidadesNegocioIds = new ArrayList<>();
    private Eboolean ativo;
    private List<Integer> usuariosIds = new ArrayList<>();
    private List<CodigoNivel> codigoNivelList = new ArrayList<>();
    private List<CodigoCargo> codigoCargoList = new ArrayList<>();
    private List<CodigoDepartamento> codigoDepartamentoList = new ArrayList<>();
    private List<Integer> cidadesIds = new ArrayList<>();

}
