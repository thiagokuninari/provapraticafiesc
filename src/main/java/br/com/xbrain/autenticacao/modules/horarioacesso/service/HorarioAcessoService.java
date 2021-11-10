package br.com.xbrain.autenticacao.modules.horarioacesso.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoDiaDto;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcessoDia;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoRepository;

public class HorarioAcessoService {

    @Autowired
    private HorarioAcessoRepository repository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    public HorarioAcesso editaAcesso(Integer acessoId, List<HorarioAcessoDiaDto> horarios) {
        var horarioAcesso = repository
            .findById(acessoId)
            .orElseThrow(() -> new ValidacaoException("Horário de Acesso não encontrado."));
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        var novos = horarios.stream()
            .map(HorarioAcessoDia::converFrom)
            .collect(Collectors.toList());
        
        horarioAcesso.setDias(novos);
        horarioAcesso.setDataUltimaAlteracao(LocalDateTime.now());
        horarioAcesso.setUsuarioAlteracao(usuarioAutenticado.getUsuario());
        
        return horarioAcesso;
    }
}
