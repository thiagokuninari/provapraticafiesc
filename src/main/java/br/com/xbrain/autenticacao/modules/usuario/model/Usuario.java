package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIO")
@Data
public class Usuario {

    @Id
    @Column(name = "ID")
    @GenericGenerator(
            name = "SEQ_USUARIO",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_USUARIO")})
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_USUARIO")
    private Integer id;

    @NotNull
    @Column(name = "NOME", length = 80, nullable = false)
    private String nome;

    @NotNull
    @Email
    @Size(max = 80)
    @Column(name = "EMAIL", nullable = false, length = 80)
    private String email;

    @Email
    @Size(max = 80)
    @Column(name = "EMAIL_02", length = 80)
    private String email02;

    @NotNull
    @Size(min = 3, max = 100)
    @Column(name = "TELEFONE")
    private String telefone;

    @Column(name = "TELEFONE_02")
    private String telefone02;

    @Column(name = "TELEFONE_03")
    private String telefone03;

    @NotNull
    @CPF
    @Column(name = "CPF", length = 14)
    private String cpf;

    @Size(max = 25)
    @Column(name = "RG", length = 25)
    private String rg;

    @Size(max = 30)
    @Column(name = "ORGAO_EXPEDIDOR", length = 30)
    private String orgaoExpeditor;

    @Size(max = 120)
    @Column(name = "LOGIN_NET_SALES", length = 120)
    private String loginNetSales;

    @Column(name = "NASCIMENTO")
    private LocalDate nascimento;

    @NotNull
    @JoinColumn(name = "FK_CARGO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_USUARIO_CARGO"), nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Cargo cargo;

    @NotNull
    @JoinColumn(name = "FK_DEPARTAMENTO", referencedColumnName = "ID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_USUARIO_DEPART"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Departamento departamento;

    @Column(name = "DATA_CADASTRO", updatable = false, nullable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "DATA_INATIVACAO")
    private LocalDateTime dataInativacao;

    @JsonIgnore
    @Column(name = "SENHA", nullable = false, updatable = false, length = 80)
    private String senha;

    @Column(name = "ALTERAR_SENHA", nullable = false)
    @Enumerated(EnumType.STRING)
    private Eboolean alterarSenha;

    public boolean isNovoCadastro() {
        return id == null;
    }
}
