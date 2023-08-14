package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoRamalRequest {

    private Integer id;

    private Integer agenteAutorizadoId;

    @NotNull
    private ECanal canal;

    private Integer subCanalId;

    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime melhorHorarioImplantacao;

    @NotNull
    private Integer quantidadeRamais;

    @NotEmpty
    private String tipoImplantacao;

    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate melhorDataImplantacao;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dataCadastro;

    @NotNull
    private List<Integer> usuariosSolicitadosIds;

    @NotEmpty
    private String telefoneTi;

    @NotEmpty
    private String emailTi;

    private ESituacaoSolicitacao situacao;

    @NotNull
    private Integer equipeId;

}
