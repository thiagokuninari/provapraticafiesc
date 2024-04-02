package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import org.junit.Test;

import javax.validation.ConstraintViolationException;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.umaConfiguracaoAgendaRequest;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.umaConstraintViolation;
import static org.assertj.core.api.Assertions.assertThatCode;

public class ConfiguracaoAgendaRequestTest {

    @Test
    public void aplicarValidacoes_deveLancarException_quandoTipoConfigCanallENaoPassarCanal() {
        var request = umaConfiguracaoAgendaRequest(ECanal.AGENTE_AUTORIZADO);
        request.setCanal(null);

        var violations = umaConstraintViolation("é obrigatório.", "canal",
            "{javax.validation.constraints.NotNull.message}", ConfiguracaoAgendaRequest.class);

        assertThatCode(request::aplicarValidacoes)
            .isInstanceOf(ConstraintViolationException.class)
            .extracting("constraintViolations").hasSize(1).first()
            .hasToString(violations.toString());
    }

    @Test
    public void aplicarValidacoes_deveLancarException_quandoTipoConfigNivellENaoPassarNivel() {
        var request = umaConfiguracaoAgendaRequest(CodigoNivel.AGENTE_AUTORIZADO);
        request.setNivel(null);

        var violations = umaConstraintViolation("é obrigatório.", "nivel",
            "{javax.validation.constraints.NotNull.message}", ConfiguracaoAgendaRequest.class);

        assertThatCode(request::aplicarValidacoes)
            .isInstanceOf(ConstraintViolationException.class)
            .extracting("constraintViolations").hasSize(1).first()
            .hasToString(violations.toString());
    }

    @Test
    public void aplicarValidacoes_deveLancarException_quandoTipoConfigEstruturalENaoPassarEstrutura() {
        var request = umaConfiguracaoAgendaRequest("ESTRUTURA");
        request.setEstruturaAa(null);

        var violations = umaConstraintViolation("é obrigatório.", "estruturaAa",
            "{org.hibernate.validator.constraints.NotBlank.message}", ConfiguracaoAgendaRequest.class);

        assertThatCode(request::aplicarValidacoes)
            .isInstanceOf(ConstraintViolationException.class)
            .extracting("constraintViolations").hasSize(1).first()
            .hasToString(violations.toString());
    }

    @Test
    public void aplicarValidacoes_deveLancarException_quandoTipoConfigSubcanalENaoPassarSubcanal() {
        var request = umaConfiguracaoAgendaRequest(ETipoCanal.PAP);
        request.setSubcanalId(null);

        var violations = umaConstraintViolation("é obrigatório.", "subcanalId",
            "{javax.validation.constraints.NotNull.message}", ConfiguracaoAgendaRequest.class);

        assertThatCode(request::aplicarValidacoes)
            .isInstanceOf(ConstraintViolationException.class)
            .extracting("constraintViolations").hasSize(1).first()
            .hasToString(violations.toString());
    }

    @Test
    public void validarNivelOperacao_deveLancarException_quandoNivelOperacao() {
        assertThatCode(() -> umaConfiguracaoAgendaRequest(CodigoNivel.OPERACAO).validarNivelOperacao())
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Não é possível criar configurações para esse nível, "
                + "por favor selecione um canal ou subcanal.");
    }
}
