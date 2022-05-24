package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.repository.SubCanalRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class SubCanalServiceTest {
    
    @Autowired
    private SubCanalService service;
    @MockBean
    private SubCanalRepository repository;

    @Test
    public void getAll_deveRetornarTodosSubCanais_quandoSolicitado() {
        when(repository.findAll()).thenReturn(List.of(umSubCanal()));

        assertThat(service.getAll())
            .hasSize(1)
            .containsExactly(new SubCanalDto(1, ETipoCanal.PAP, "PAP", ESituacao.A));
    }

    @Test
    public void getSubCanalById_deveRetornarSubCanalResponse_quandoHouverSubCanal() {
        when(repository.findById(anyInt())).thenReturn(Optional.of(umSubCanal()));

        assertThat(service.getSubCanalById(1))
            .isEqualTo(new SubCanalDto(1, ETipoCanal.PAP, "PAP", ESituacao.A));
    }

    @Test
    public void getSubCanalById_deveLancarException_quandoNaoHouverSubCanal() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.getSubCanalById(100))
            .withMessage("Erro, subcanal não encontrado.");
    }
}
