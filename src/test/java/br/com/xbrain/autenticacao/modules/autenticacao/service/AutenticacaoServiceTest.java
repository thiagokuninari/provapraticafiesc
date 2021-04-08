package br.com.xbrain.autenticacao.modules.autenticacao.service;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class AutenticacaoServiceTest {

    @InjectMocks
    private AutenticacaoService autenticacaoService;
    @Mock
    private TokenStore tokenStore;
    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    public void forcarLogoutGeradorLeads_deveChamarTokenStore_quandoUsuarioGeradorLeads() {
        when(tokenStore.findTokensByClientIdAndUserName(any(), any()))
            .thenReturn(List.of(new DefaultOAuth2AccessToken("token")));
        var umUsuarioGeradorLeads = Usuario.builder()
            .cargo(Cargo.builder()
                .id(96)
                .codigo(CodigoCargo.GERADOR_LEADS)
                .build())
            .id(12345)
            .nome("USUARIO GERADOR LEADS")
            .email("GERADORLEADS@GMAIL.COM")
            .build();

        autenticacaoService.forcarLogoutGeradorLeads(umUsuarioGeradorLeads);
        verify(tokenStore, times(1)).removeAccessToken(any());
    }

    @Test
    public void forcarLogoutGeradorLeads_deveNaoChamarTokenStore_quandoUsuarioNaoGeradorLeads() {
        var umUsuario = Usuario.builder()
            .cargo(Cargo.builder()
                .codigo(CodigoCargo.GERENTE_OPERACAO)
                .build())
            .id(54321)
            .nome("THOMAS")
            .email("THOMAS@GMAIL.COM")
            .build();

        autenticacaoService.forcarLogoutGeradorLeads(umUsuario);
        verify(tokenStore, never()).removeAccessToken(any());
    }
}
