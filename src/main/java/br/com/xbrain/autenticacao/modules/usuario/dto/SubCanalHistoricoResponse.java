package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.EAcao;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanalHistorico;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCanalHistoricoResponse {

    private Integer id;

    private ETipoCanal codigo;

    private String nome;

    private ESituacao situacao;

    private Eboolean novaChecagemCreditoAntiga;

    private Eboolean novaChecagemViabilidadeAntiga;

    private Eboolean novaChecagemCreditoNova;

    private Eboolean novaChecagemViabilidadeNova;

    private EAcao acao;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataAcao;

    private Integer usuarioAcaoId;

    private String usuarioAcaoNome;

    public static SubCanalHistoricoResponse of(SubCanalHistorico historico) {
        var response = new SubCanalHistoricoResponse();
        BeanUtils.copyProperties(historico, response);
        return response;
    }
}
