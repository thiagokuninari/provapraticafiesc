package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model;

import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriadoImportado {

    private String dataFeriado;
    private String diaSemana;
    private String nome;
    private ETipoFeriado tipoFeriado;
    private Uf uf;
    private Cidade cidade;
}
