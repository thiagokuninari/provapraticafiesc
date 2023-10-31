package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.helper.RegionalHelper;
import br.com.xbrain.autenticacao.modules.comum.helper.UfHelper;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.CodigoIbgeRegionalResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidade;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidadePk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.caelum.stella.type.Estado.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubClusterHelper.*;

public class CidadeHelper {

    public static Cidade umaCidadeLondrina() {
        return Cidade.builder()
            .id(1111)
            .nome("LONDRINA")
            .codigoIbge("4113700")
            .uf(new Uf(1, "PARANA", PR.name(), Collections.emptyList()))
            .subCluster(umSubClusterLondrina())
            .build();
    }

    public static Cidade umaCidadeSaoPaulo() {
        return Cidade.builder()
            .id(2222)
            .nome("SAO PAULO")
            .codigoIbge("4113700")
            .uf(new Uf(2, "SAO PAULO", SP.name(), Collections.emptyList()))
            .subCluster(umSubClusterSaoPaulo())
            .build();
    }

    public static Cidade umaCidadeRioDeJaneiro() {
        return Cidade.builder()
            .id(3333)
            .nome("RIO DE JANEIRO")
            .codigoIbge("4113700")
            .uf(new Uf(19, "RIO DE JANEIRO", RJ.name(), Collections.emptyList()))
            .subCluster(umSubClusterRioDeJaneiro())
            .build();
    }

    public static Cidade umaCidadeCapitolio() {
        return Cidade.builder()
            .id(4444)
            .nome("CAPITOLIO")
            .codigoIbge("4113700")
            .uf(new Uf(8, "MINAS GERAIS", MG.name(), Collections.emptyList()))
            .subCluster(umSubClusterRemotoSulMinas())
            .build();
    }

    public static Cidade umaCidadeGama() {
        return Cidade.builder()
            .id(5555)
            .nome("GAMA")
            .codigoIbge("4113700")
            .uf(new Uf(18, "MINAS GERAIS", MG.name(), Collections.emptyList()))
            .subCluster(umSubClusterCoronelFabriciano())
            .build();
    }

    public static UsuarioCidade umUsuarioCidade(Integer usuarioId, Cidade cidade) {
        var usuario = new Usuario(usuarioId);
        usuario.setSituacao(ESituacao.A);
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(usuario.getId(), umaCidadeLondrina().getId()));
        usuarioCidade.setUsuario(usuario);
        usuarioCidade.setCidade(cidade);

        return usuarioCidade;
    }

    public static List<UsuarioCidade> umaListaUsuarioCidadesDeLondrina() {
        return List.of(
            umUsuarioCidade(1, umaCidadeLondrina()),
            umUsuarioCidade(2, umaCidadeLondrina()),
            umUsuarioCidade(3, umaCidadeLondrina()),
            umUsuarioCidade(4, umaCidadeLondrina()),
            umUsuarioCidade(5, umaCidadeLondrina()),
            umUsuarioCidade(6, umaCidadeLondrina()),
            umUsuarioCidade(7, umaCidadeLondrina()),
            umUsuarioCidade(8, umaCidadeLondrina())
        );
    }

    public static List<UsuarioCidade> umaListaUsuarioCidadesDeSaoPaulo() {
        return List.of(
            umUsuarioCidade(1, umaCidadeSaoPaulo()),
            umUsuarioCidade(2, umaCidadeSaoPaulo()),
            umUsuarioCidade(3, umaCidadeSaoPaulo()),
            umUsuarioCidade(4, umaCidadeSaoPaulo()),
            umUsuarioCidade(5, umaCidadeSaoPaulo()),
            umUsuarioCidade(7, umaCidadeSaoPaulo()),
            umUsuarioCidade(8, umaCidadeSaoPaulo())
        );
    }

    public static List<UsuarioCidade> umaListaUsuarioCidadesDeRioDeJaneiro() {
        return List.of(
            umUsuarioCidade(1, umaCidadeRioDeJaneiro()),
            umUsuarioCidade(3, umaCidadeRioDeJaneiro()),
            umUsuarioCidade(4, umaCidadeRioDeJaneiro()),
            umUsuarioCidade(5, umaCidadeRioDeJaneiro()),
            umUsuarioCidade(7, umaCidadeRioDeJaneiro()),
            umUsuarioCidade(8, umaCidadeRioDeJaneiro())
        );
    }

    public static List<UsuarioCidade> umaListaUsuarioCidadesDeCapitolio() {
        return List.of(
            umUsuarioCidade(1, umaCidadeRioDeJaneiro()),
            umUsuarioCidade(3, umaCidadeRioDeJaneiro()),
            umUsuarioCidade(7, umaCidadeRioDeJaneiro())
        );
    }

    public static List<Cidade> umaListaApenasCidades() {
        return umaListaComCidadesEDistritos()
            .stream()
            .filter(cidade -> cidade.getFkCidade() == null)
            .collect(Collectors.toList());
    }

    public static List<Cidade> umaListaApenasDistritos() {
        return umaListaComCidadesEDistritos()
            .stream()
            .filter(cidade -> cidade.getFkCidade() != null)
            .collect(Collectors.toList());
    }

    public static List<Integer> umaListaApenasFkCidadeDosDistritos() {
        return umaListaApenasDistritos()
            .stream()
            .map(Cidade::getFkCidade)
            .distinct()
            .collect(Collectors.toList());
    }

    public static List<Cidade> umaListaComCidadesEDistritos() {
        return List.of(
            cidadeBandeirantes(),
            cidadeBarueri(),
            cidadeBernardinoDeCampos(),
            cidadeCajamar(),
            cidadeCambe(),
            cidadeCampinaDaLagoa(),
            cidadeCascavel(),
            cidadeCosmopolis(),
            cidadeCosmorama(),
            cidadeCuritiba(),
            cidadeFlorianopolis(),
            cidadeLins(),
            cidadeLondrina(),
            cidadeMarilia(),
            cidadeMaringa(),
            distritoAldeia(),
            distritoBelaVistaDoPiquiri(),
            distritoGuaravera(),
            distritoIrere(),
            distritoHerveira(),
            distritoJardimBelval(),
            distritoJardimSilveira(),
            distritoJordanesia(),
            distritoLerroville(),
            distritoMaravilha(),
            distritoPaiquere(),
            distritoPolvilho(),
            distritoSallesDeOliveira(),
            distritoSaoLuiz(),
            distritoWarta()
        );
    }

    public static List<Cidade> listaCidadesDeSaoPaulo() {
        return Stream.concat(
                listaCidadesComUfSaoPauloERegionalSci().stream(),
                listaCidadesPaiSaoPauloComOutraRegional().stream())
            .collect(Collectors.toList());
    }

    public static List<Cidade> listaCidadesDoParanaEDistritosDeLondrina() {
        return Stream.concat(
                listaCidadesDoParana().stream(),
                listaDistritosDeLondrina().stream())
            .collect(Collectors.toList());
    }

    public static List<Cidade> listaDistritosDeLondrinaECampinaDaLagoaECidadeCampinaDaLagoa() {
        var lista = new ArrayList<>(listaDistritosDeLondrina());
        lista.addAll(listaDistritosDeCampinaDaLagoa());
        lista.add(cidadeCampinaDaLagoa());

        return lista;
    }

    public static List<Cidade> listaCidadesDoParana() {
        return List.of(
            cidadeBandeirantes(),
            cidadeCambe(),
            cidadeCampinaDaLagoa(),
            cidadeCascavel(),
            cidadeCuritiba(),
            cidadeLondrina(),
            cidadeMaringa()
        );
    }

    public static List<Cidade> listaDistritosDeLondrina() {
        return List.of(
            distritoGuaravera(),
            distritoIrere(),
            distritoLerroville(),
            distritoMaravilha(),
            distritoPaiquere(),
            distritoSaoLuiz(),
            distritoWarta()
        );
    }

    public static List<Cidade> listaDistritosDeCampinaDaLagoa() {
        return List.of(
            distritoBelaVistaDoPiquiri(),
            distritoHerveira(),
            distritoSallesDeOliveira()
        );
    }

    public static List<Cidade> listaCidadesComUfSaoPauloERegionalSci() {
        return List.of(
            distritoAldeia(),
            cidadeBernardinoDeCampos(),
            cidadeCosmopolis(),
            cidadeCosmorama(),
            distritoJardimBelval(),
            distritoJardimSilveira(),
            distritoJordanesia(),
            cidadeLins(),
            cidadeMarilia(),
            distritoPolvilho()
        );
    }

    public static List<Cidade> listaCidadesPaiSaoPauloComOutraRegional() {
        return List.of(
            cidadeBarueri(),
            cidadeCajamar()
        );
    }

    public static Cidade cidadeBandeirantes() {
        return Cidade.builder()
            .id(3248)
            .nome("BANDEIRANTES")
            .codigoIbge("4102406")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(null)
            .build();
    }

    public static Cidade cidadeBarueri() {
        return Cidade.builder()
            .id(4864)
            .nome("BARUERI")
            .codigoIbge("3505708")
            .uf(UfHelper.ufSaoPaulo())
            .regional(RegionalHelper.novaRegionalRsc())
            .fkCidade(null)
            .build();
    }

    public static Cidade cidadeBernardinoDeCampos() {
        return Cidade.builder()
            .id(4870)
            .nome("BERNARDINO DE CAMPOS")
            .codigoIbge("3506300")
            .uf(UfHelper.ufSaoPaulo())
            .regional(RegionalHelper.novaRegionalRsi())
            .fkCidade(null)
            .build();
    }

    public static Cidade cidadeCajamar() {
        return Cidade.builder()
            .id(4903)
            .nome("CAJAMAR")
            .codigoIbge("3509205")
            .uf(UfHelper.ufSaoPaulo())
            .regional(RegionalHelper.novaRegionalRsc())
            .fkCidade(null)
            .build();
    }

    public static Cidade cidadeCambe() {
        return Cidade.builder()
            .id(3270)
            .nome("CAMBE")
            .codigoIbge("4103701")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(null)
            .build();
    }

    public static Cidade cidadeCampinaDaLagoa() {
        return Cidade.builder()
            .id(3272)
            .nome("CAMPINA DA LAGOA")
            .codigoIbge("4103909")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(null)
            .build();
    }

    public static Cidade cidadeCascavel() {
        return Cidade.builder()
            .id(3287)
            .nome("CASCAVEL")
            .codigoIbge("4104808")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(null)
            .build();
    }

    public static Cidade cidadeCosmopolis() {
        return Cidade.builder()
            .id(4943)
            .nome("COSMOPOLIS")
            .codigoIbge("3512803")
            .uf(UfHelper.ufSaoPaulo())
            .regional(RegionalHelper.novaRegionalRsi())
            .fkCidade(null)
            .build();
    }

    public static Cidade cidadeCosmorama() {
        return Cidade.builder()
            .id(4944)
            .nome("COSMORAMA")
            .codigoIbge("3512902")
            .uf(UfHelper.ufSaoPaulo())
            .regional(RegionalHelper.novaRegionalRsi())
            .fkCidade(null)
            .build();
    }

    public static Cidade cidadeCuritiba() {
        return Cidade.builder()
            .id(3312)
            .nome("CURITIBA")
            .codigoIbge("4106902")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(null)
            .build();
    }

    public static Cidade cidadeFlorianopolis() {
        return Cidade.builder()
            .id(4519)
            .nome("FLORIANOPOLIS")
            .codigoIbge("4205407")
            .uf(UfHelper.ufSantaCatarina())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(null)
            .build();
    }

    public static Cidade cidadeLins() {
        return Cidade.builder()
            .id(5107)
            .nome("LINS")
            .codigoIbge("3527108")
            .uf(UfHelper.ufSaoPaulo())
            .regional(RegionalHelper.novaRegionalRsi())
            .fkCidade(null)
            .build();
    }

    public static Cidade cidadeLondrina() {
        return Cidade.builder()
            .id(5578)
            .nome("LONDRINA")
            .codigoIbge("4113700")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .netUno(Eboolean.F)
            .fkCidade(null)
            .build();
    }

    public static Cidade cidadeMarilia() {
        return Cidade.builder()
            .id(5128)
            .nome("MARILIA")
            .codigoIbge("3529005")
            .uf(UfHelper.ufSaoPaulo())
            .regional(RegionalHelper.novaRegionalRsi())
            .fkCidade(null)
            .build();
    }

    public static Cidade cidadeMaringa() {
        return Cidade.builder()
            .id(3426)
            .nome("MARINGA")
            .codigoIbge("4115200")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(null)
            .build();
    }

    public static Cidade distritoAldeia() {
        return Cidade.builder()
            .id(33618)
            .nome("ALDEIA")
            .uf(UfHelper.ufSaoPaulo())
            .regional(RegionalHelper.novaRegionalRsi())
            .fkCidade(4864)
            .build();
    }

    public static Cidade distritoBelaVistaDoPiquiri() {
        return Cidade.builder()
            .id(30650)
            .nome("BELA VISTA DO PIQUIRI")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(3272)
            .build();
    }

    public static Cidade distritoGuaravera() {
        return Cidade.builder()
            .id(30858)
            .nome("GUARAVERA")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(5578)
            .build();
    }

    public static Cidade distritoHerveira() {
        return Cidade.builder()
            .id(30574)
            .nome("HERVEIRA")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(3272)
            .build();
    }

    public static Cidade distritoIrere() {
        return Cidade.builder()
            .id(30813)
            .nome("IRERE")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(5578)
            .build();
    }

    public static Cidade distritoJardimBelval() {
        return Cidade.builder()
            .id(33252)
            .nome("JARDIM BELVAL")
            .uf(UfHelper.ufSaoPaulo())
            .regional(RegionalHelper.novaRegionalRsi())
            .fkCidade(4864)
            .build();
    }

    public static Cidade distritoJardimSilveira() {
        return Cidade.builder()
            .id(33255)
            .nome("JARDIM SILVEIRA")
            .uf(UfHelper.ufSaoPaulo())
            .regional(RegionalHelper.novaRegionalRsi())
            .fkCidade(4864)
            .build();
    }

    public static Cidade distritoJordanesia() {
        return Cidade.builder()
            .id(33269)
            .nome("JORDANESIA")
            .uf(UfHelper.ufSaoPaulo())
            .regional(RegionalHelper.novaRegionalRsi())
            .fkCidade(4903)
            .build();
    }

    public static Cidade distritoLerroville() {
        return Cidade.builder()
            .id(30732)
            .nome("LERROVILLE")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(5578)
            .build();
    }

    public static Cidade distritoMaravilha() {
        return Cidade.builder()
            .id(30757)
            .nome("MARAVILHA")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(5578)
            .build();
    }

    public static Cidade distritoPaiquere() {
        return Cidade.builder()
            .id(30676)
            .nome("PAIQUERE")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(5578)
            .build();
    }

    public static Cidade distritoPolvilho() {
        return Cidade.builder()
            .id(33302)
            .nome("POLVILHO")
            .uf(UfHelper.ufSaoPaulo())
            .regional(RegionalHelper.novaRegionalRsi())
            .fkCidade(4903)
            .build();
    }

    public static Cidade distritoSallesDeOliveira() {
        return Cidade.builder()
            .id(30780)
            .nome("SALLES DE OLIVEIRA")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(3272)
            .build();
    }

    public static Cidade distritoSaoLuiz() {
        return Cidade.builder()
            .id(30848)
            .nome("SAO LUIZ")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(5578)
            .build();
    }

    public static Cidade distritoWarta() {
        return Cidade.builder()
            .id(30910)
            .nome("WARTA")
            .uf(UfHelper.ufParana())
            .regional(RegionalHelper.novaRegionalRps())
            .fkCidade(5578)
            .build();
    }

    public static CidadeResponse cidadeResponseAldeia() {
        var cidadeResponse = CidadeResponse.of(distritoAldeia());
        cidadeResponse.setCidadePai("BARUERI");

        return cidadeResponse;
    }

    public static CidadeResponse cidadeResponseBarueri() {
        return CidadeResponse.of(cidadeBarueri());
    }

    public static CidadeResponse cidadeResponseJardimBelval() {
        var cidadeResponse = CidadeResponse.of(distritoJardimBelval());
        cidadeResponse.setCidadePai("BARUERI");

        return cidadeResponse;
    }

    public static CidadeResponse cidadeResponseJardimSilveira() {
        var cidadeResponse = CidadeResponse.of(distritoJardimSilveira());
        cidadeResponse.setCidadePai("BARUERI");

        return cidadeResponse;
    }

    public static CidadeResponse cidadeResponseJordanesiaComCidadePai() {
        var cidadeResponse = CidadeResponse.of(distritoJordanesia());
        cidadeResponse.setCidadePai("CAJAMAR");

        return cidadeResponse;
    }

    public static CidadeResponse cidadeResponseLins() {
        return CidadeResponse.of(cidadeLins());
    }

    public static CidadeResponse cidadeResponseLondrina() {
        return CidadeResponse.of(cidadeLondrina());
    }

    public static CidadeResponse cidadeResponseMaringa() {
        return CidadeResponse.of(cidadeMaringa());
    }

    public static CidadeResponse cidadeResponsePolvilhoComCidadePai() {
        var cidadeResponse = CidadeResponse.of(distritoPolvilho());
        cidadeResponse.setCidadePai("CAJAMAR");

        return cidadeResponse;
    }

    public static Map<Integer, CidadeResponse> umMapApenasDistritosComCidadePai() {
        var cidades = umaListaApenasCidades()
            .stream()
            .collect(Collectors.toMap(Cidade::getId, cidade -> cidade));

        return umaListaApenasDistritos()
            .stream()
            .map(CidadeResponse::of)
            .map(cidadeResponse -> CidadeResponse.definirNomeCidadePaiPorCidades(cidadeResponse, cidades))
            .collect(Collectors.toMap(CidadeResponse::getId, cidadeResponse -> cidadeResponse));
    }

    public static List<CodigoIbgeRegionalResponse> umaListaCodigoIbgeRegionalResponse() {
        return List.of(
            codigoIbgeRegionalResponseLondrina(),
            codigoIbgeRegionalResponseMaringa(),
            codigoIbgeRegionalResponseLins()
        );
    }

    public static CodigoIbgeRegionalResponse codigoIbgeRegionalResponseLondrina() {
        return CodigoIbgeRegionalResponse
            .builder()
            .cidadeId(5578)
            .cidadeNome("LONDRINA")
            .codigoIbge("4113700")
            .regionalId(1027)
            .regionalNome("RPS")
            .build();
    }

    public static CodigoIbgeRegionalResponse codigoIbgeRegionalResponseMaringa() {
        return CodigoIbgeRegionalResponse
            .builder()
            .cidadeId(3426)
            .cidadeNome("MARINGA")
            .codigoIbge("4115200")
            .regionalId(1027)
            .regionalNome("RPS")
            .build();
    }

    public static CodigoIbgeRegionalResponse codigoIbgeRegionalResponseLins() {
        return CodigoIbgeRegionalResponse
            .builder()
            .cidadeId(5107)
            .cidadeNome("LINS")
            .codigoIbge("3527108")
            .regionalId(1031)
            .regionalNome("RSI")
            .build();
    }
}
