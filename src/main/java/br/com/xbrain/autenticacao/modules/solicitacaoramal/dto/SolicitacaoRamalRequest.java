package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoRamalRequest {

    private Integer id;

    @NotNull
    private Integer usuarioId;

    @NotNull
    private Integer agenteAutorizadoId;

    @NotNull
    private String agenteAutorizadoNome;

    @NotNull
    private LocalTime melhorHorarioImplantacao;

    @NotNull
    private Integer quantidadeRamais;

    @NotNull
    private LocalDate melhorDataImplantacao;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataCadastro;

    private ESituacao situacao;

    public static SolicitacaoRamal convertFrom(SolicitacaoRamalRequest request) {
        SolicitacaoRamal solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.setUsuario(new Usuario(request.usuarioId));

        BeanUtils.copyProperties(request, solicitacaoRamal);

        return solicitacaoRamal;
    }

}
