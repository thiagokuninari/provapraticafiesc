package br.com.xbrain.autenticacao.modules.organizacaoempresa.rabbitmq;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaUpdateDto;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoFanoutDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class OrganizacaoEmpresaMqSenderTest {

    @Autowired
    private OrganizacaoEmpresaMqSender mqSender;
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendUpdateNomeSucess_deveEnviarParaFila_seNaoOcorrerErro() {
        var dto = new OrganizacaoEmpresaUpdateDto("organizacao nome", "nome atualizado", 1);
        mqSender.sendUpdateNomeSucess(dto);
        verify(rabbitTemplate).convertAndSend("organizacao-empresa-atualizacao-nome.queue", dto);
    }

    @Test
    public void sendOrganizacaoInativada_deveEnviarParaFila_seNaoOcorrerErro() {
        var dto = OrganizacaoFanoutDto.builder()
            .organizacaoId(1)
            .nivel(CodigoNivel.XBRAIN)
            .build();

        mqSender.sendOrganizacaoInativada(dto);
        verify(rabbitTemplate).convertAndSend("organizacao-inativada.fanout", "", dto);
    }
}
