package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriadoAutomacaoFiltros {

    private Integer ano;
    private String estado;
    private String cidade;
    private List<Cidade> cidades;
    private Integer ufId;
    private Integer cidadeId;
    private List<Integer> cidadesIds;
}
