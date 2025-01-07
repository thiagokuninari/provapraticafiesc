package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.EFormatoDataHora;
import br.com.xbrain.autenticacao.modules.comum.util.CsvUtils;
import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thymeleaf.util.StringUtils;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioAcessoResponse {

    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private String dataHora;

    public static UsuarioAcessoResponse of(UsuarioAcesso usuarioAcesso) {
        return UsuarioAcessoResponse.builder()
            .id(usuarioAcesso.getUsuario().getId())
            .nome(usuarioAcesso.getUsuario().getNome())
            .cpf(gerarCpfComMascara(usuarioAcesso.getUsuario().getCpf()))
            .email(usuarioAcesso.getUsuario().getEmail())
            .dataHora(DateUtil.formatarDataHora(EFormatoDataHora.DATA_HORA_SEG, usuarioAcesso.getDataCadastro()))
            .build();
    }

    @JsonIgnore
    public static String getCabecalhoCsv() {
        return "ID;"
            + "NOME;"
            + "CPF;"
            + "E-MAIL;"
            + "DATA;"
            + "\n";
    }

    @JsonIgnore
    public String toCsv() {
        return Stream.of(
            this.id.toString(),
            this.nome,
            this.cpf,
            this.email,
            this.dataHora
        ).map(CsvUtils::replaceCaracteres)
            .collect(Collectors.joining(";"));
    }

    @SuppressWarnings("magicnumber")
    private static String gerarCpfComMascara(String cpfUsr) {
        var cpf = Optional.ofNullable(cpfUsr).orElse("");
        if (cpf.length() == 0 || cpf.length() < 11) {
            return cpf + "";
        }
        return StringUtils.concat(cpf.substring(0,3),".",
            cpf.substring(3,6), ".",
            cpf.substring(6,9), "-",
            cpf.substring(9,11));
    }

}
