package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.xbrainutils.CsvUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
@AllArgsConstructor
public class SiteCsvResponse {

    private String nome;
    private ETimeZone timeZone;
    private Integer discadoraId;
    private String discadoraText;
    private ESituacao situacao;

    public static List<SiteCsvResponse> of(List<Site> sites) {
        return sites.stream().map(SiteCsvResponse::of).collect(Collectors.toList());
    }

    private static SiteCsvResponse of(Site site) {
        return SiteCsvResponse.builder()
            .nome(site.getNome())
            .timeZone(site.getTimeZone())
            .discadoraId(site.getDiscadoraId())
            .situacao(site.getSituacao())
            .build();
    }

    public static String ofCsv(List<SiteCsvResponse> lista) {
        return SiteCsvResponse.getCabecalhoCsv()
            .concat(SiteCsvResponse.getLinhasCsv(lista));
    }

    public static String getCabecalhoCsv() {
        return "NOME;"
            .concat("FUSO HORARIO;")
            .concat("DISCADORA;")
            .concat("SITUACAO;")
            .concat("\n");
    }

    @JsonIgnore
    public static String getLinhasCsv(List<SiteCsvResponse> lista) {
        return !CollectionUtils.isEmpty(lista)
            ? lista.stream()
            .map(SiteCsvResponse::toCsv)
            .collect(Collectors.joining("\n"))
            : "Registros não encontrados."
            .concat("\n");
    }

    public static String toCsv(SiteCsvResponse site) {
        return Stream.of(
            Optional.ofNullable(site.getNome()).orElse(""),
            Optional.ofNullable(site.getTimeZone()).map(ETimeZone::getDescricao).orElse(""),
            Optional.ofNullable(site.getDiscadoraText()).orElse(""),
            Optional.ofNullable(site.getSituacao()).map(ESituacao::getDescricao).orElse(""))
            .map(CsvUtils::replaceCaracteres)
            .collect(Collectors.joining(";"));
    }
}
