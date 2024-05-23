package br.com.xbrain.autenticacao.modules.feriado.model;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoImportacao;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.xbrainutils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "FERIADO")
public class Feriado {

    @Id
    @SequenceGenerator(name = "SEQ_FERIADO", sequenceName = "SEQ_FERIADO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_FERIADO", strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DATA_FERIADO", nullable = false)
    private LocalDate dataFeriado;

    @Column(name = "DATA_CADASTRO", updatable = false, nullable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "FERIADO_NACIONAL", nullable = false)
    @Enumerated(EnumType.STRING)
    private Eboolean feriadoNacional;

    @JoinColumn(name = "FK_CIDADE", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_FERIADO_CIDADE"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Cidade cidade;

    @Column(name = "TIPO_FERIADO")
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private ETipoFeriado tipoFeriado;

    @JoinColumn(name = "FK_UF", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_FERIADO_UF"))
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Uf uf;

    @JoinColumn(name = "FK_FERIADO_PAI", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_FERIADO_FERIADO_PAI"))
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Feriado feriadoPai;

    @Column(name = "SITUACAO")
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private ESituacaoFeriado situacao;

    @JoinColumn(name = "FK_USUARIO_CADASTRO", foreignKey = @ForeignKey(name = "FK_FERIADO_USUARIO_CAD"),
        referencedColumnName = "ID", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Usuario usuarioCadastro;

    @JoinColumn(name = "FK_IMPORTACAO", foreignKey = @ForeignKey(name = "FK_FERIADO_IMPORTACAO"),
        referencedColumnName = "ID", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private ImportacaoFeriado importacaoFeriado;

    @PrePersist
    public void setup() {
        nome = nome.toUpperCase();
    }

    public static Feriado of(FeriadoRequest request, Integer usuarioCadastroId) {
        var feriado = new Feriado();
        BeanUtils.copyProperties(request, feriado);
        feriado.setDataFeriado(DateUtils.parseStringToLocalDate(request.getDataFeriado()));
        feriado.setFeriadoNacional(Eboolean.valueOf(request.isFeriadoNacional()));
        if (nonNull(request.getEstadoId())) {
            feriado.setUf(new Uf(request.getEstadoId()));
        }
        if (nonNull(request.getCidadeId())) {
            feriado.setCidade(new Cidade(request.getCidadeId()));
        }
        feriado.setDataCadastro(LocalDateTime.now());
        feriado.setSituacao(ESituacaoFeriado.ATIVO);
        feriado.setUsuarioCadastro(new Usuario(usuarioCadastroId));
        return feriado;
    }

    public static Feriado ofAutomacao(FeriadoAutomacao feriadoAutomacao, ImportacaoFeriado importacaoFeriado) {
        var feriado = new Feriado();
        BeanUtils.copyProperties(feriadoAutomacao, feriado);
        feriado.setDataFeriado(DateUtils.parseStringToLocalDate(feriadoAutomacao.getDataFeriado()));
        feriado.setFeriadoNacional(
            feriadoAutomacao.getTipoFeriado()
                .equals(ETipoFeriado.NACIONAL) ? Eboolean.V : Eboolean.F);
        if (feriadoAutomacao.getUfId() != null) {
            feriado.setUf(new Uf(feriadoAutomacao.getUfId()));
        }
        if (feriadoAutomacao.getCidadeId() != null) {
            feriado.setCidade(new Cidade(feriadoAutomacao.getCidadeId()));
        }
        if (importacaoFeriado.getUsuarioCadastroId() != null) {
            feriado.setUsuarioCadastro(new Usuario(importacaoFeriado.getUsuarioCadastroId()));
        }
        feriado.setDataCadastro(LocalDateTime.now());
        feriado.setSituacao(ESituacaoFeriado.ATIVO);
        feriado.setImportacaoFeriado(importacaoFeriado);
        return feriado;
    }

    public static List<Feriado> ofAutomacao(List<FeriadoAutomacao> feriadosAutomacao, ImportacaoFeriado importacaoFeriado) {
        return feriadosAutomacao
            .stream()
            .map(feriadoAutomacao -> ofAutomacao(feriadoAutomacao, importacaoFeriado))
            .collect(Collectors.toList());
    }

    public static Feriado criarFeriadoFilho(Cidade cidade, Feriado feriadoPai) {
        var feriadoFilho = new Feriado();
        BeanUtils.copyProperties(feriadoPai, feriadoFilho);
        feriadoFilho.setId(null);
        feriadoFilho.setFeriadoPai(feriadoPai);
        feriadoFilho.setCidade(cidade);
        feriadoFilho.setSituacao(ESituacaoFeriado.ATIVO);
        return feriadoFilho;
    }

    public static Feriado ofFeriadoEditado(Feriado feriado, FeriadoRequest request) {
        var feriadoEditado = new Feriado();
        BeanUtils.copyProperties(feriado, feriadoEditado);
        feriadoEditado.setNome(request.getNome());
        feriadoEditado.setDataFeriado(DateUtils.parseStringToLocalDate(request.getDataFeriado()));
        if (nonNull(request.getEstadoId())) {
            feriadoEditado.setUf(new Uf(request.getEstadoId()));
        }
        if (nonNull(request.getCidadeId())) {
            feriadoEditado.setCidade(new Cidade(request.getCidadeId()));
        }
        return feriadoEditado;
    }

    public static Feriado ofFeriadoImportado(FeriadoImportacao feriadoImportacao, Integer usuarioCadastroId) {
        var feriado = new Feriado();
        BeanUtils.copyProperties(feriadoImportacao, feriado);
        feriado.setFeriadoNacional(Eboolean.valueOf(feriadoImportacao.isFeriadoNacional()));
        feriado.setDataCadastro(LocalDateTime.now());
        feriado.setSituacao(ESituacaoFeriado.ATIVO);
        feriado.setUsuarioCadastro(new Usuario(usuarioCadastroId));
        return feriado;
    }

    @JsonIgnore
    public boolean isFeriadoEstadual() {
        return nonNull(tipoFeriado) && tipoFeriado.equals(ETipoFeriado.ESTADUAL);
    }

    public void excluir() {
        situacao = ESituacaoFeriado.EXCLUIDO;
    }

    public void editarFeriadoFilho(Feriado feriadoPai) {
        nome = feriadoPai.getNome();
        dataFeriado = feriadoPai.getDataFeriado();
    }
}
