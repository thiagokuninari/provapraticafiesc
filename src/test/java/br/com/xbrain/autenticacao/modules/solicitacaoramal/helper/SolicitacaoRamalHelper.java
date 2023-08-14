package br.com.xbrain.autenticacao.modules.solicitacaoramal.helper;

import br.com.xbrain.autenticacao.modules.call.dto.RamalResponse;
import br.com.xbrain.autenticacao.modules.call.dto.TelefoniaResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.SocioResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalAtualizarStatusRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ETipoImplantacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao.REJEITADO;

public class SolicitacaoRamalHelper {

    public static SolicitacaoRamalRequest umaSolicitacaoRamalRequest(Integer id, Integer aaId) {
        return SolicitacaoRamalRequest.builder()
            .id(id)
            .quantidadeRamais(38)
            .agenteAutorizadoId(aaId)
            .melhorHorarioImplantacao(LocalTime.of(10, 00))
            .melhorDataImplantacao(LocalDate.of(2019, 01, 25))
            .tipoImplantacao(ETipoImplantacao.ESCRITORIO.getCodigo())
            .emailTi("reanto@ti.com.br")
            .telefoneTi("(18) 3322-2388")
            .usuariosSolicitadosIds(Arrays.asList(100, 101))
            .situacao(ESituacaoSolicitacao.CONCLUIDO)
            .usuariosSolicitadosIds(List.of(2))
            .canal(ECanal.AGENTE_AUTORIZADO)
            .build();
    }

    public static SolicitacaoRamalRequest umaSolicitacaoRamalRequestBlank(Integer id) {
        return SolicitacaoRamalRequest.builder()
            .id(id)
            .quantidadeRamais(38)
            .agenteAutorizadoId(1)
            .melhorHorarioImplantacao(LocalTime.of(10, 00))
            .melhorDataImplantacao(LocalDate.of(2019, 01, 25))
            .tipoImplantacao("    ")
            .emailTi("    ")
            .telefoneTi("    ")
            .usuariosSolicitadosIds(List.of())
            .situacao(ESituacaoSolicitacao.EM_ANDAMENTO)
            .usuariosSolicitadosIds(List.of(3))
            .canal(ECanal.AGENTE_AUTORIZADO)
            .build();
    }

    public static SolicitacaoRamalAtualizarStatusRequest umaSolicitacaoRamalAtualizarRequest() {
        return SolicitacaoRamalAtualizarStatusRequest.builder()
            .idSolicitacao(4)
            .situacao(ESituacaoSolicitacao.ENVIADO)
            .build();
    }
}
