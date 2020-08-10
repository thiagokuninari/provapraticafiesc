package br.com.xbrain.autenticacao.modules.feeder.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SituacaoAlteracaoUsuarioFeederDto {

    private Integer usuarioId;
    private ESituacao situacaoAlterada;
    private Integer usuarioAlteracaoId;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dataAlteracao;
    private String observacao;
}
