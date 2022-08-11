package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EHistoricoAcao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresaHistorico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizacaoEmpresaHistoricoResponse {

    private String usuarioNome;
    private LocalDateTime dataAlteracao;
    private EHistoricoAcao observacao;

    public static OrganizacaoEmpresaHistoricoResponse of(OrganizacaoEmpresaHistorico historico) {
        var response = new OrganizacaoEmpresaHistoricoResponse();
        BeanUtils.copyProperties(historico, response);
        return response;
    }
}
