package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SupervisorService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<UsuarioResponse> getAssistentesEVendedoresD2dDaCidadeDoSupervisor(Integer supervisorId) {
        return usuarioRepository.getUsuariosDaMesmaCidadeDoUsuarioId(
                supervisorId,
                Arrays.asList(CodigoCargo.ASSISTENTE_OPERACAO, CodigoCargo.VENDEDOR_OPERACAO),
                ECanal.D2D_PROPRIO);
    }

    public List<UsuarioResponse> getSupervisoresPorAreaAtuacao(AreaAtuacao areaAtuacao,
                                                               List<Integer> areasAtuacaoId) {
        return usuarioRepository.getUsuariosPorAreaAtuacao(
                areaAtuacao,
                areasAtuacaoId,
                CodigoCargo.SUPERVISOR_OPERACAO,
                ECanal.D2D_PROPRIO);
    }
}
