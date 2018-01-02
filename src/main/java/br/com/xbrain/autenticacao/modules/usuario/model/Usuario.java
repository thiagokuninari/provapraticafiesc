package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @Column(name = "EMAIL_01", nullable = false, length = 80, unique = true)
    private String email;

    @Email
    @Size(max = 80)
    @Column(name = "EMAIL_02", length = 80)
    private String email02;

    @NotNull
    @Size(min = 3, max = 100)
    @Column(name = "TELEFONE_01")
    private String telefone;

    @Column(name = "TELEFONE_02")
    private String telefone02;

    @Column(name = "TELEFONE_03")
    private String telefone03;

    @NotNull
    @CPF
    @Column(name = "CPF", length = 14, unique = true)
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
    @JoinColumn(name = "FK_UNIDADE_NEGOCIO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_USUARIO_UNID_NEGOCIO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadeNegocio unidadeNegocio;

    @JsonIgnore
    @NotEmpty
    @JoinTable(name = "USUARIO_EMPRESA", joinColumns = {
            @JoinColumn(name = "FK_USUARIO", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "FK_USUARIO_EMPRESA_USUARIO"))}, inverseJoinColumns = {
            @JoinColumn(name = "FK_EMPRESA", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "FK_USUARIO_EMPRESA_EMPRESA"))})
    @ManyToMany
    private List<Empresa> empresas =  new ArrayList<>();

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

    public Usuario() {
    }

    public Usuario(Integer id) {
        this.id = id;
    }

    public List<Integer> getEmpresasId() {
        return empresas != null && Hibernate.isInitialized(empresas)
                ? empresas
                        .stream()
                        .map(Empresa::getId)
                        .collect(Collectors.toList())
                : null;
    }

    public void setEmpresasId(List<Integer> ids) {
        if (ids != null) {
            empresas = ids
                    .stream()
                    .map(Empresa::new)
                    .collect(Collectors.toList());
        }
    }

    public static Usuario parse(UsuarioDto usuarioDto) {
        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(usuarioDto, usuario);
        usuario.setEmpresasId(usuarioDto.getEmpresasId());
        usuario.setUnidadeNegocio(new UnidadeNegocio(usuarioDto.getUnidadeNegocioId()));
        usuario.setCargo(new Cargo(usuarioDto.getCargoId()));
        usuario.setDepartamento(new Departamento(usuarioDto.getDepartamentoId()));
        return usuario;
    }

    public void validarCpf() {
        this.cpf = this.cpf.replaceAll("[.-]", "");
    }
}
