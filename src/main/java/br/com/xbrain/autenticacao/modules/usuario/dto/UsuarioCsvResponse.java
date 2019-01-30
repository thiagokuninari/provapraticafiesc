package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.util.CsvUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioCsvResponse {

    private Integer id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private String rg;
    private ESituacao situacao;
    private String cargo;
    private String departamento;
    private String unidadesNegocios;
    private String empresas;

    public UsuarioCsvResponse(Integer id,
                              String nome,
                              String email,
                              String telefone,
                              String cpf,
                              String rg,
                              String cargo,
                              String departamento,
                              String unidadesNegocios,
                              String empresas,
                              ESituacao situacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.rg = rg;
        this.cargo = cargo;
        this.departamento = departamento;
        this.unidadesNegocios = unidadesNegocios;
        this.empresas = empresas;
        this.situacao = situacao;
    }


    @JsonIgnore
    public static String getCabecalhoCsv() {
        return "CODIGO;"
                + "NOME;"
                + "EMAIL;"
                + "TELEFONE;"
                + "CPF;"
                + "RG;"
                + "CARGO;"
                + "DEPARTAMENTO;"
                + "UNIDADE NEGOCIO;"
                + "EMPRESA;"
                + "SITUACAO"
                + "\n";


    }

    @JsonIgnore
    public String toCsv() {
        return Stream.of(
                this.id.toString(),
                this.nome,
                this.email,
                this.telefone,
                this.cpf,
                this.rg,
                this.cargo,
                this.departamento,
                this.unidadesNegocios,
                this.empresas,
                this.situacao.toString()
        ).map(CsvUtils::replaceCaracteres)
                .collect(Collectors.joining(";"));
    }

}
