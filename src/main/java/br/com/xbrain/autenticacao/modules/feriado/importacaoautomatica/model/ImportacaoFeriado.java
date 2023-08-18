package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "IMPORTACAO_FERIADO")
public class ImportacaoFeriado {

    @Id
    @SequenceGenerator(name = "SEQ_IMPORTACAO_FERIADO", sequenceName = "SEQ_IMPORTACAO_FERIADO")
    @GeneratedValue(generator = "SEQ_IMPORTACAO_FERIADO", strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    @Column(name = "DATA_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    @NotNull
    @Column(name = "SITUACAO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacaoFeriadoAutomacao situacaoFeriadoAutomacao;

    @NotNull
    @Column(name = "USUARIO_CADASTRO_ID")
    private Integer usuarioCadastroId;

    @Column(name = "USUARIO_CADASTRO_NOME")
    private String usuarioCadastroNome;

    @Column(name = "DESCRICAO")
    private String descricao;

    public static ImportacaoFeriado of(ESituacaoFeriadoAutomacao situacao, UsuarioAutenticado usuarioAutenticado) {
        return ImportacaoFeriado.builder()
            .dataCadastro(LocalDateTime.now())
            .situacaoFeriadoAutomacao(situacao)
            .usuarioCadastroId(usuarioAutenticado != null ? usuarioAutenticado.getId() : null)
            .usuarioCadastroNome(usuarioAutenticado != null ? usuarioAutenticado.getNome() : null)
            .build();
    }

    public void gerarDescricao(String descricao) {
        this.descricao = this.descricao.concat("-").concat(descricao);
    }
}
