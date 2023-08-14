package br.com.xbrain.autenticacao.modules.solicitacaoramal.model;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ETipoImplantacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.util.SolicitacaoRamalExpiracaoAdjuster;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.xbrain.xbrainutils.NumberUtils.getOnlyNumbers;

@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@Entity
@Table(name = "SOLICITACAO_RAMAL")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SolicitacaoRamal {

    @Id
    @SequenceGenerator(name = "SEQ_SOLICITACAO_RAMAL", sequenceName = "SEQ_SOLICITACAO_RAMAL", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SOLICITACAO_RAMAL", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_SOLICITACAO_RAMAL_USUARIO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "CANAL")
    private ECanal canal;

    @JoinColumn(name = "FK_SUBCANAL", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_SUBCANAL_SOLICITACAO_RAMAL"))
    @ManyToOne(fetch = FetchType.LAZY)
    private SubCanal subCanal;

    @JsonIgnore
    @NotEmpty
    @JoinTable(name = "SOLICITACAO_RAMAL_USUARIO", joinColumns = {
        @JoinColumn(name = "FK_SOLICITACAO_RAMAL", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_RAMAL_USUARIO"))}, inverseJoinColumns = {
        @JoinColumn(name = "FK_USUARIO", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_USUARIO_RAMAL"))})
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Usuario> usuariosSolicitados;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO")
    private ESituacaoSolicitacao situacao;

    @Column(name = "EQUIPE_ID")
    private Integer equipeId;

    @Column(name = "AGENTE_AUTORIZADO_ID")
    private Integer agenteAutorizadoId;

    @Column(name = "AGENTE_AUTORIZADO_NOME")
    private String agenteAutorizadoNome;

    @Column(name = "AGENTE_AUTORIZADO_CNPJ")
    private String agenteAutorizadoCnpj;

    @Column(name = "TELEFONE_TI")
    private String telefoneTi;

    @Column(name = "EMAIL_TI")
    private String emailTi;

    @Column(name = "MELHOR_HORARIO_IMPLANTACAO", nullable = false)
    private LocalTime melhorHorarioImplantacao;

    @Column(name = "MELHOR_DATA_IMPLANTACAO", nullable = false)
    private LocalDate melhorDataImplantacao;

    @Column(name = "DATA_CADASTRO", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "DATA_FINALIZACAO", nullable = false)
    private LocalDateTime dataFinalizacao;

    @Column(name = "QUANTIDADE_RAMAIS", nullable = false)
    private Integer quantidadeRamais;

    @Column(name = "TIPO_IMPLANTACAO")
    @NotNull
    @Enumerated(EnumType.STRING)
    private ETipoImplantacao tipoImplantacao;

    @Column(name = "ENVIOU_EMAIL_EXPIRACAO")
    @Enumerated(EnumType.STRING)
    private Eboolean enviouEmailExpiracao;

    @Column(name = "DATA_ENVIADO_EMAIL_EXPIRACAO")
    private LocalDateTime dataEnviadoEmailExpiracao;

    public SolicitacaoRamal(Integer id, Integer agenteAutorizadoId, String agenteAutorizadoNome,
                            String agenteAutorizadoCnpj, ECanal canal,
                            ESituacaoSolicitacao situacao, Integer quantidadeRamais,
                            LocalDateTime dataCadastro, LocalDateTime dataFinalizacao, Usuario usuario) {
        this.id = id;
        this.agenteAutorizadoId = agenteAutorizadoId;
        this.agenteAutorizadoNome = agenteAutorizadoNome;
        this.agenteAutorizadoCnpj = agenteAutorizadoCnpj;
        this.canal = canal;
        this.situacao = situacao;
        this.quantidadeRamais = quantidadeRamais;
        this.dataCadastro = dataCadastro;
        this.dataFinalizacao = dataFinalizacao;
        this.usuario = usuario;
    }

    public SolicitacaoRamal(Integer id, ECanal canal, SubCanal subCanal, ESituacaoSolicitacao situacao,
                            Integer quantidadeRamais, LocalDateTime dataCadastro,
                            LocalDateTime dataFinalizacao, Usuario usuario) {
        this.id = id;
        this.usuario = usuario;
        this.canal = canal;
        this.subCanal = subCanal;
        this.situacao = situacao;
        this.dataCadastro = dataCadastro;
        this.dataFinalizacao = dataFinalizacao;
        this.quantidadeRamais = quantidadeRamais;
    }

    public static SolicitacaoRamal convertFrom(SolicitacaoRamalRequest request, Integer usuarioId,
                                               LocalDateTime dataCadastro) {
        var solicitacaoRamal = new SolicitacaoRamal();
        BeanUtils.copyProperties(request, solicitacaoRamal);
        solicitacaoRamal.atualizarDataCadastro(dataCadastro);
        solicitacaoRamal.setUsuario(new Usuario(usuarioId));
        solicitacaoRamal.setSubCanal(Optional.ofNullable(request.getSubCanalId())
            .map(SubCanal::new)
            .orElse(null));
        solicitacaoRamal.setUsuariosSolicitados(request.getUsuariosSolicitadosIds()
            .stream()
            .map(Usuario::new)
            .collect(Collectors.toList()));
        solicitacaoRamal.setTipoImplantacao(ETipoImplantacao.valueOf(request.getTipoImplantacao()));

        return solicitacaoRamal;
    }

    public static SolicitacaoRamal convertFrom(SolicitacaoRamalRequest request, Integer usuarioId,
                                               LocalDateTime dataCadastro, AgenteAutorizadoResponse agenteAutorizado) {
        var solicitacaoRamal = convertFrom(request, usuarioId, dataCadastro);
        solicitacaoRamal.setAgenteAutorizadoCnpj(agenteAutorizado.getCnpj());
        solicitacaoRamal.setAgenteAutorizadoNome(agenteAutorizado.getRazaoSocial());
        solicitacaoRamal.setAgenteAutorizadoId(Integer.parseInt(agenteAutorizado.getId()));

        return solicitacaoRamal;
    }

    public void atualizarDataCadastro(LocalDateTime dataAtual) {
        this.dataCadastro = dataAtual;
        calcularDataFinalizacao();
        atualizarSituacaoParaPendente();
        atualizarEnviouEmailExpiracaoParaFalso();
    }

    private void atualizarSituacaoParaPendente() {
        this.situacao = ESituacaoSolicitacao.PENDENTE;
    }

    private void atualizarEnviouEmailExpiracaoParaFalso() {
        this.enviouEmailExpiracao = Eboolean.F;
    }

    public void editar(SolicitacaoRamalRequest request) {
        this.agenteAutorizadoId = request.getAgenteAutorizadoId();
        this.canal = request.getCanal();
        this.subCanal = new SubCanal(request.getSubCanalId());
        this.melhorHorarioImplantacao = request.getMelhorHorarioImplantacao();
        this.quantidadeRamais = request.getQuantidadeRamais();
        this.tipoImplantacao = ETipoImplantacao.valueOf(request.getTipoImplantacao());
        this.melhorDataImplantacao = request.getMelhorDataImplantacao();
        this.telefoneTi = request.getTelefoneTi();
        this.emailTi = request.getEmailTi();
        atualizaUsuariosSolicitados(request);
    }

    private void atualizaUsuariosSolicitados(SolicitacaoRamalRequest request) {
        this.usuariosSolicitados = request.getUsuariosSolicitadosIds()
            .stream()
            .map(Usuario::new).collect(Collectors.toList());
    }

    public void atualizarNomeECnpjDoAgenteAutorizado(AgenteAutorizadoResponse agenteAutorizado) {
        this.agenteAutorizadoNome = agenteAutorizado.getRazaoSocial();
        this.agenteAutorizadoCnpj = agenteAutorizado.getCnpj();
    }

    public void retirarMascara() {
        this.telefoneTi = getOnlyNumbers(this.telefoneTi);
        this.agenteAutorizadoCnpj = getOnlyNumbers(this.agenteAutorizadoCnpj);
    }

    public void calcularDataFinalizacao() {
        this.dataFinalizacao = LocalDateTime.from(this.dataCadastro.with(new SolicitacaoRamalExpiracaoAdjuster()));
    }
}
