package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.util.CsvUtils;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.util.StringUtil.getCpfFormatado;
import static br.com.xbrain.autenticacao.modules.comum.util.StringUtil.getStringFormatadaCsv;

@Data
@AllArgsConstructor
public class UsuarioCsvResponse {

    private Integer id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private String cargo;
    private String departamento;
    private String unidadesNegocios;
    private String empresas;
    private ESituacao situacao;

    @JsonIgnore
    public static String getCabecalhoCsv() {
        return "CODIGO;"
                .concat("NOME;")
                .concat("EMAIL;")
                .concat("TELEFONE;")
                .concat("CPF;")
                .concat("CARGO;")
                .concat("DEPARTAMENTO;")
                .concat("UNIDADE NEGOCIO;")
                .concat("EMPRESA;")
                .concat("SITUACAO")
                .concat("\n");
    }

    @JsonIgnore
    public String toCsv() {
        return Stream.of(
                this.id.toString(),
                getStringFormatadaCsv(this.nome),
                getStringFormatadaCsv(this.email),
                getStringFormatadaCsv(this.telefone),
                getCpfFormatado(this.cpf),
                getStringFormatadaCsv(this.cargo),
                getStringFormatadaCsv(this.departamento),
                getStringFormatadaCsv(this.unidadesNegocios),
                getStringFormatadaCsv(this.empresas),
                this.situacao.toString()
        ).map(CsvUtils::replaceCaracteres)
                .collect(Collectors.joining(";"));
    }
}
