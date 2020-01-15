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
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
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

    public UsuarioAcessoResponse(UsuarioAcesso usuarioAcesso) {
        BeanUtils.copyProperties(usuarioAcesso, this);
    }

    public static UsuarioAcessoResponse of(UsuarioAcesso usuarioAcesso) {
        var data = LocalDateTime.now();
        if (!ObjectUtils.isEmpty(usuarioAcesso.getDataCadastro())) {
            data = usuarioAcesso.getDataCadastro();
        } else {
            data = usuarioAcesso.getDataLogout();
        }
        return UsuarioAcessoResponse.builder()
            .id(usuarioAcesso.getUsuario().getId())
            .nome(usuarioAcesso.getUsuario().getNome())
            .cpf(usuarioAcesso.getUsuario().getCpf())
            .email(usuarioAcesso.getUsuario().getEmail())
            .dataHora(DateUtil.formatarDataHora(EFormatoDataHora.DATA_HORA, data))
            .build();
    }

    @JsonIgnore
    public static String getCabecalhoCsv() {
        return "ID;"
            + "NOME;"
            + "CPF;"
            + "EMAIL;"
            + "DATA/HORA LOGIN;"
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

}
