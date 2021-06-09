package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import br.com.xbrain.xbrainutils.CsvUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.util.StringUtil.getStringFormatadaCsv;
import static br.com.xbrain.xbrainutils.CpfUtils.getCpfFormatado;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private LocalDateTime dataUltimoAcesso;
    private String loginNetSales;
    private String nivel;
    private String hierarquia;

    public UsuarioCsvResponse(Integer id,
                              String nome,
                              String email,
                              String telefone,
                              String cpf,
                              String cargo,
                              String departamento,
                              String unidadesNegocios,
                              String empresas,
                              ESituacao situacao,
                              LocalDateTime dataUltimoAcesso,
                              String loginNetSales,
                              String nivel) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.cargo = cargo;
        this.departamento = departamento;
        this.unidadesNegocios = removeDuplicadosWmConcat(unidadesNegocios);
        this.empresas = removeDuplicadosWmConcat(empresas);
        this.situacao = situacao;
        this.dataUltimoAcesso = dataUltimoAcesso;
        this.loginNetSales = loginNetSales;
        this.nivel = nivel;
    }

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
                .concat("SITUACAO;")
                .concat("DATA ULTIMO ACESSO;")
                .concat("LOGIN NETSALES;")
                .concat("NIVEL;")
                .concat("HIERARQUIA")
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
                this.unidadesNegocios,
                this.empresas,
                this.situacao.toString(),
                !ObjectUtils.isEmpty(this.dataUltimoAcesso)
                    ? this.dataUltimoAcesso.toString() : "",
                getStringFormatadaCsv(this.loginNetSales),
                getStringFormatadaCsv(this.nivel),
                getStringFormatadaCsv(this.hierarquia)
        ).map(CsvUtils::replaceCaracteres)
                .collect(Collectors.joining(";"));
    }

    public static UsuarioCsvResponse of(UsuarioCsvResponse usuarioCsvResponse,
                                        UsuarioHierarquia usuarioHierarquia) {
        UsuarioCsvResponse usuarioCsvResponseFinal = new UsuarioCsvResponse();
        BeanUtils.copyProperties(usuarioCsvResponse, usuarioCsvResponseFinal);
        usuarioCsvResponseFinal.hierarquia = usuarioHierarquia.getUsuarioSuperior().getNome();
        return usuarioCsvResponseFinal;
    }

    private String removeDuplicadosWmConcat(String input) {
        return !ObjectUtils.isEmpty(input)
                ? Stream.of(input.split(","))
                .distinct()
                .collect(Collectors.joining("."))
                : "";
    }
}
