package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
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

    private String agenteAutorizadoCnpj;

    @NotNull
    private LocalTime melhorHorarioImplantacao;

    @NotNull
    private Integer quantidadeRamais;

    @NotNull
    private LocalDate melhorDataImplantacao;

    private LocalDateTime dataCadastro;

    private ESituacao situacao;

    @NotNull
    private List<Integer> usuariosSolicitadosIds;

    @NotEmpty
    private String telefoneTi;

    @NotEmpty
    private String emailTi;

    public static SolicitacaoRamal convertFrom(SolicitacaoRamalRequest request) {
        SolicitacaoRamal solicitacaoRamal = new SolicitacaoRamal();
        solicitacaoRamal.setUsuario(new Usuario(request.usuarioId));

        solicitacaoRamal.setUsuariosSolicitados(request.getUsuariosSolicitadosIds()
                .stream()
                .map(Usuario::new).collect(Collectors.toList()));

        BeanUtils.copyProperties(request, solicitacaoRamal);

        return solicitacaoRamal;
    }

}
