package br.com.xbrain.autenticacao.modules.organizacaoempresa.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EHistoricoAcao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EModalidadeEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.ModalidadeEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.repository.OrganizacaoEmpresaHistoricoRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_organizacao_empresa_historico.sql"})
public class OrganizacaoEmpresaHistoricoServiceTest {

    @Autowired
    private OrganizacaoEmpresaHistoricoService historicoService;
    @Autowired
    private OrganizacaoEmpresaHistoricoRepository historicoRepository;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private UsuarioService usuarioService;

    @Test
    public void salvarHistorico_deveChamarRepositorio_quandoReceberOrganizacaoEmpresa() {
        assertThat(historicoRepository.findAllByOrganizacaoEmpresaIdOrderByDataAlteracaoDesc(1))
            .hasSize(1);

        historicoService.salvarHistorico(umaOrganizacaoEmpresaCadastrada(), EHistoricoAcao.EDICAO,
            umUsuarioAutenticado());

        assertThat(historicoRepository.findAllByOrganizacaoEmpresaIdOrderByDataAlteracaoDesc(1))
            .hasSize(2)
            .extracting("situacao", "organizacaoEmpresa.id", "observacao", "usuarioId",
                "usuarioNome")
            .contains(tuple(ESituacaoOrganizacaoEmpresa.A, 1, EHistoricoAcao.EDICAO, 2, "Thiago"));
    }

    private OrganizacaoEmpresa umaOrganizacaoEmpresaCadastrada() {
        return OrganizacaoEmpresa.builder()
            .id(1)
            .razaoSocial("THIAGO TESTE")
            .cnpj("08112392000192")
            .modalidadesEmpresa(List.of(umaModalidadeEmpresaPap(), umaModalidadeEmpresaTelevendas()))
            .nivel(Nivel.builder()
                .codigo(CodigoNivel.RECEPTIVO)
                .build())
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .dataCadastro(LocalDateTime.of(2022, 1, 5, 9, 10, 10))
            .usuarioCadastro(umUsuario())
            .build();
    }

    public static Usuario umUsuario() {
        var usuario = new Usuario();
        usuario.setId(100);
        usuario.setNome("Thiago");
        return usuario;
    }

    public static UsuarioAutenticado umUsuarioAutenticado() {
        var usuarioAutenticado = new UsuarioAutenticado();
        usuarioAutenticado.setId(2);
        usuarioAutenticado.setNome("Thiago");
        return usuarioAutenticado;
    }

    public static ModalidadeEmpresa umaModalidadeEmpresaPap() {
        var modalidadeEmpresa = new ModalidadeEmpresa();
        modalidadeEmpresa.setId(1);
        modalidadeEmpresa.setModalidadeEmpresa(EModalidadeEmpresa.PAP);
        return modalidadeEmpresa;
    }

    public static ModalidadeEmpresa umaModalidadeEmpresaTelevendas() {
        var modalidadeEmpresa = new ModalidadeEmpresa();
        modalidadeEmpresa.setId(2);
        modalidadeEmpresa.setModalidadeEmpresa(EModalidadeEmpresa.TELEVENDAS);
        return modalidadeEmpresa;
    }

}
