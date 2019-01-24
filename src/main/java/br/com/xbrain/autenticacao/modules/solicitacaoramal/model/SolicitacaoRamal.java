package br.com.xbrain.autenticacao.modules.solicitacaoramal.model;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@Entity
@Table(name = "SOLICITACAO_RAMAL")
public class SolicitacaoRamal {

    @Id
    @SequenceGenerator(name = "SEQ_SOLICITACAO_RAMAL", sequenceName = "SEQ_SOLICITACAO_RAMAL", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SOLICITACAO_RAMAL", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_SOLICITACAO_RAMAL_USUARIO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

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
    private ESituacao situacao;

    @Column(name = "AGENTE_AUTORIZADO_ID", nullable = false)
    private Integer agenteAutorizadoId;

    @Column(name = "AGENTE_AUTORIZADO_NOME", nullable = false)
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

    @Column(name = "QUANTIDADE_RAMAIS", nullable = false)
    private Integer quantidadeRamais;

    public void atualizarDataCadastro() {
        this.dataCadastro = LocalDateTime.now();
        atualizarSituacaoParaPendente();
    }

    private void atualizarSituacaoParaPendente() {
        this.situacao = ESituacao.PD;
    }

    public void editar(SolicitacaoRamalRequest request) {
        this.usuario = new Usuario(request.getUsuarioId());
        this.agenteAutorizadoId = request.getAgenteAutorizadoId();
        this.agenteAutorizadoNome = request.getAgenteAutorizadoNome();
        this.agenteAutorizadoCnpj = request.getAgenteAutorizadoCnpj();
        this.melhorHorarioImplantacao = request.getMelhorHorarioImplantacao();
        this.quantidadeRamais = request.getQuantidadeRamais();
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

}
