package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Subnivel;
import br.com.xbrain.autenticacao.modules.usuario.repository.SubnivelRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SubnivelServiceTest {

    @InjectMocks
    private SubnivelService service;
    @Mock
    private SubnivelRepository repository;

    @Test
    public void getSubniveisSelect_deveRetornarListaDeSelectResponse_quandoSolicitado() {
        when(repository.findByNivelIdAndSituacao(1, A)).thenReturn(umaListaDeSubniveis());

        assertThat(service.getSubniveisSelect(1))
            .extracting(SelectResponse::getValue, SelectResponse::getLabel)
            .containsExactly(tuple(1, "BACKOFFICE"),
                tuple(2, "BACKOFFICE CENTRALIZADO"),
                tuple(3, "BACKOFFICE DE QUALIDADE"),
                tuple(4, "BACKOFFICE SUPORTE DE VENDAS"));

        verify(repository).findByNivelIdAndSituacao(1, A);
    }

    @Test
    public void getFuncionalidadesIds_deveRetornarListaDeFuncionalidadesIds_quandoSolicitado() {
        when(repository.findAll()).thenReturn(umaListaDeSubniveis());

        assertThat(service.getFuncionalidadesIds())
            .isEqualTo(List.of(1, 2, 3, 4));

        verify(repository).findAll();
    }

    @Test
    public void findByIdIn_deveRetornarSetDeSubniveis_quandoSolicitado() {
        when(repository.findByIdIn(Set.of(1))).thenReturn(umSetDeSubniveisComUmSubnivel());

        assertThat(service.findByIdIn(Set.of(1)))
            .extracting(Subnivel::getId, Subnivel::getNome)
            .containsExactly(tuple(1, "BACKOFFICE"));

        verify(repository).findByIdIn(Set.of(1));
    }

    @Test
    public void getSubnivelFuncionalidadesIds_deveRetornarListaDeIds_quandoSolicitadoComUmaLista() {
        assertThat(service.getSubnivelFuncionalidadesIds(umaListaDeSubniveis()))
            .isEqualTo(List.of(1, 2, 3, 4));
    }

    @Test
    public void getSubnivelFuncionalidadesIds_deveRetornarListaDeIds_quandoSolicitadoComUmSet() {
        assertThat(service.getSubnivelFuncionalidadesIds(umSetDeSubniveis()))
            .containsExactlyInAnyOrder(2, 3);
    }
}
