package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.comum.util.CnpjUtil;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ETipoImplantacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SolicitacaoRamalResponse {

    private Integer id;
    private Integer quantidadeRamais;
    private ECanal canal;
    private ETipoCanal subCanalCodigo;
    private Integer subCanalId;
    private String tipoImplantacao;
    private ESituacaoSolicitacao situacao;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataCadastro;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataHoraExpiracao;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime melhorHorarioImplantacao;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate melhorDataImplantacao;
    private String solicitante;
    private String agenteAutorizadoCnpj;
    private String agenteAutorizadoNome;
    private String telefoneTi;
    private String emailTi;
    private Integer agenteAutorizadoId;
    private List<SolicitacaoRamalColaboradorResponse> colaboradores;

    public static SolicitacaoRamalResponse convertFrom(SolicitacaoRamal solicitacaoRamal) {
        var response = new SolicitacaoRamalResponse();
        response.solicitante = solicitacaoRamal.getUsuario().getNome();
        response.colaboradores = response.getColaboradores(solicitacaoRamal);
        response.subCanalCodigo = Optional.ofNullable(solicitacaoRamal.getSubCanal())
            .map(SubCanal::getCodigo)
            .orElse(null);
        response.subCanalId = Optional.ofNullable(solicitacaoRamal.getSubCanal())
            .map(SubCanal::getId)
            .orElse(null);
        response.dataHoraExpiracao = solicitacaoRamal.getDataFinalizacao();
        response.setTipoImplantacao(Optional.ofNullable(solicitacaoRamal.getTipoImplantacao())
            .map(ETipoImplantacao::getDescricao)
            .orElse(""));
        BeanUtils.copyProperties(solicitacaoRamal, response);
        response.agenteAutorizadoCnpj = CnpjUtil.formataCnpj(solicitacaoRamal.getAgenteAutorizadoCnpj());

        return response;
    }

    private List<SolicitacaoRamalColaboradorResponse> getColaboradores(SolicitacaoRamal solicitacaoRamal) {
        return !ObjectUtils.isEmpty(solicitacaoRamal.getUsuariosSolicitados())
            ? solicitacaoRamal.getUsuariosSolicitados()
            .stream()
            .map(SolicitacaoRamalColaboradorResponse::convertFrom)
            .collect(Collectors.toList())
            : Collections.emptyList();
    }
}
