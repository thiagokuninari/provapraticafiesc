package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeeder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAaTipoFeederDto {

    private List<Integer> usuariosIds;
    private Integer usuarioCadastroId;
    private ETipoFeeder tipoFeeder;
}
