package br.com.xbrain.autenticacao.modules.cep.service;

import br.com.xbrain.autenticacao.modules.cep.client.ConsultaCepClient;
import br.com.xbrain.autenticacao.modules.cep.dto.ConsultaCepResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeUfResponse;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ConsultaCepService {

    @Autowired
    private ConsultaCepClient consultaCepClient;

    @Autowired
    private CidadeService cidadeService;

    public CidadeUfResponse consultarCep(String cep) {
        try {
            var cepResponse = consultaCepClient.consultarCep(StringUtil.getOnlyNumbers(cep));
            return CidadeUfResponse.of(
                cidadeService.findByUfNomeAndCidadeNome(cepResponse.getUf(), removerAcentuacao(cepResponse.getCidade())));
        } catch (Exception exception) {
            throw new IntegracaoException(exception, ConsultaCepService.class.getName(), EErrors.ERRO_OBTER_CEP);
        }
    }

    public List<ConsultaCepResponse> consultarCeps(List<String> ceps) {
        try {
            var cepsResponse = ceps
                .stream()
                .map(StringUtil::getOnlyNumbers)
                .map(consultaCepClient::consultarCep)
                .collect(Collectors.toList());

            cepsResponse.forEach(cep -> cep.setCidade(removerAcentuacao(cep.getCidade())));

            return cepsResponse;
        } catch (Exception ex) {
            throw new IntegracaoException(ex, ConsultaCepService.class.getName(), EErrors.ERRO_OBTER_CEP);
        }
    }

    private String removerAcentuacao(String texto) {
        var normalizer = Normalizer.normalize(texto, Normalizer.Form.NFD);
        var pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalizer).replaceAll("");
    }

}
