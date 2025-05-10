package com.provapratica.api.domain;

import com.provapratica.api.comun.CpfUtil;
import com.provapratica.api.dto.UsuarioRequest;
import com.provapratica.api.enums.ENivelUsuario;
import com.provapratica.api.enums.EStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USUARIO")
public class Usuario {

    @Id
    @SequenceGenerator(name = "SEQ_USUARIO", sequenceName = "SEQ_USUARIO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USUARIO")
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CPF", nullable = false)
    private String cpf;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DATA_NASCIMENTO", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private EStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_NIVEL", referencedColumnName = "ID", foreignKey = @ForeignKey(name = "FK_USUARIO_NIVEL"), nullable = false)
    private Nivel nivel;

    @Column(name = "ESPECIALIDADE")
    private String especialidade;

    @Column(name = "CEP")
    private String cep;

    @Column(name = "LOGRADOURO")
    private String logradouro;

    @Column(name = "NUMERO")
    private String numero;

    @Column(name = "BAIRRO")
    private String bairro;

    @Column(name = "ESTADO")
    private String estado;

    @Column(name = "CIDADE")
    private String cidade;

    @Column(name = "TELEFONE_CELULAR")
    private String telefoneCelular;

    @Column(name = "WHATSAPP")
    private String whatsapp;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "SENHA", nullable = false)
    private String senha;

    public static Usuario of(UsuarioRequest usuarioRequest) {
        var usuario = Usuario.builder()
                .cpf(CpfUtil.removerCaracteresDoCpf(usuarioRequest.getCpf()))
                .nome(usuarioRequest.getNome())
                .dataNascimento(usuarioRequest.getDataNascimento())
                .status(EStatus.ATIVO)
                .nivel(usuarioRequest.getNivel())
                .email(usuarioRequest.getEmail());

        if (usuarioRequest.getNivel().getCodigo() == ENivelUsuario.PROFESSOR) {
            usuario.especialidade(usuarioRequest.getEspecialidade());
        } else if (usuarioRequest.getNivel().getCodigo() == ENivelUsuario.ESTUDANTE) {
            usuario.cep(usuarioRequest.getCep())
                    .logradouro(usuarioRequest.getLogradouro())
                    .numero(usuarioRequest.getNumero())
                    .bairro(usuarioRequest.getBairro())
                    .estado(usuarioRequest.getEstado())
                    .cidade(usuarioRequest.getCidade())
                    .telefoneCelular(usuarioRequest.getTelefoneCelular())
                    .whatsapp(usuarioRequest.getWhatsapp());
        }
        return usuario.build();
    }
}