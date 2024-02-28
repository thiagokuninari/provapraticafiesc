package br.com.xbrain.autenticacao.modules.cep.service;

import br.com.xbrain.autenticacao.modules.cep.client.ConsultaCepClient;
import br.com.xbrain.autenticacao.modules.cep.dto.ConsultaCepResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeUfResponse;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultaCepService {

    private final CidadeService cidadeService;
    private final ConsultaCepClient consultaCepClient;

    public CidadeUfResponse consultarCep(String cep) {
        try {
            var cepResponse = consultaCepClient.consultarCep(StringUtil.getOnlyNumbers(cep));
            var response =  CidadeUfResponse.of(
                cidadeService.findByUfNomeAndCidadeNome(cepResponse.getUf(), removerAcentuacao(cepResponse.getCidade())));
            response.setBairro(cepResponse.getBairro());
            response.setLogradouro(cepResponse.getNomeCompleto());
            response.setCepUnicoPorCidade(cepResponse.getCepUnicoPorCidade());
            return response;
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
