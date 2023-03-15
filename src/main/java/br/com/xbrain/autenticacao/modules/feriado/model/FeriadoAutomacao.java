package br.com.xbrain.autenticacao.modules.feriado.model;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "FERIADO_AUTOMACAO")
public class FeriadoAutomacao {

    @Id
    @SequenceGenerator(name = "SEQ_FERIADO_AUTOMACAO", sequenceName = "SEQ_FERIADO_AUTOMACAO")
    @GeneratedValue(generator = "SEQ_FERIADO_AUTOMACAO", strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    @Column(name = "DATA_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    @NotNull
    @Column(name = "SITUACAO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacaoFeriadoAutomacao situacaoFeriadoAutomacao;

    @NotNull
    @Column(name = "USUARIO_CADASTRO_ID", nullable = false)
    private Integer usuarioCadastroId;

    @Column(name = "USUARIO_CADASTRO_NOME")
    private String usuarioCadastroNome;

    public static FeriadoAutomacao of(ESituacaoFeriadoAutomacao situacao, UsuarioAutenticado usuarioAutenticado) {
        return FeriadoAutomacao.builder()
            .dataCadastro(LocalDateTime.now())
            .situacaoFeriadoAutomacao(situacao)
            .usuarioCadastroId(usuarioAutenticado.getId())
            .usuarioCadastroNome(usuarioAutenticado.getNome())
            .build();
    }
}
