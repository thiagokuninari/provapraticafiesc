package br.com.xbrain.autenticacao.modules.cep.service;

import br.com.xbrain.autenticacao.modules.cep.client.ConsultaCepClient;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeUfResponse;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                cidadeService.findByUfNomeAndCidadeNome(cepResponse.getUf(), cepResponse.getCidade()));
        } catch (Exception exception) {
            throw new IntegracaoException(exception, ConsultaCepService.class.getName(), EErrors.ERRO_OBTER_CEP);
        }
    }
}
