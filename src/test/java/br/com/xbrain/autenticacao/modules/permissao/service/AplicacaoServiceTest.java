package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.permissao.enums.CodigoAplicacao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class AplicacaoServiceTest {
    @Autowired
    private AplicacaoService service;

    @Test
    public void getAll_deveRecuperarTodasAplicacoes_seExistiremNoBanco() {
        assertThat(service.getAll()).extracting("id", "nome", "codigo").containsExactlyInAnyOrder(
                tuple(1, "AUTENTICAÇÃO", CodigoAplicacao.AUT),
                tuple(2, "PARCEIROS ONLINE", CodigoAplicacao.POL),
                tuple(3, "VENDAS", CodigoAplicacao.VDS),
                tuple(4, "MAILING", CodigoAplicacao.MLG),
                tuple(5, "SIMULADOR DE PRODUTOS", CodigoAplicacao.SML),
                tuple(6, "FERRAMENTA DE CRIAÇÃO DE PRODUTOS", CodigoAplicacao.FCP),
                tuple(7, "VAREJO", CodigoAplicacao.VAR),
                tuple(8, "INTEGRAÇÃO", CodigoAplicacao.INT),
                tuple(11, "CONEXÃO GESTÃO", CodigoAplicacao.COG),
                tuple(13, "EQUIPE VENDAS", CodigoAplicacao.EVD),
                tuple(14, "CONTATO CRN", CodigoAplicacao.CRN),
                tuple(15, "CHAMADO", CodigoAplicacao.CHM),
                tuple(17, "RELATÓRIOS", CodigoAplicacao.REL),
                tuple(18, "OUVIDORIA", CodigoAplicacao.OUV),
                tuple(19, "BRIEFING", CodigoAplicacao.BRF),
                tuple(20, "CONTROLE", CodigoAplicacao.CTR),
                tuple(21, "COMUNICADO", CodigoAplicacao.CMD)
        );
    }

}