package br.com.xbrain.autenticacao.modules.horarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcessoDia;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcessoHistorico;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoDiaRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoHistoricoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class HorarioAcessoService {

    public static final ValidacaoException HORARIO_ACESSO_NAO_ENCONTRADO =
        new ValidacaoException("Horário de acesso não encontrado.");

    @Autowired
    private HorarioAcessoRepository repository;
    @Autowired
    private HorarioAcessoHistoricoRepository historicoRepository;
    @Autowired
    private HorarioAcessoDiaRepository diaRepository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    public void editHorario(HorarioAcessoRequest request) {
        var horario = repository.findById(request.getId())
            .orElseThrow(() -> HORARIO_ACESSO_NAO_ENCONTRADO);
        var novosHorarios = request.getDiasAcesso()
            .stream()
            .map(HorarioAcessoDia::converFrom)
            .collect(Collectors.toList());

        for (HorarioAcessoDia dia : horario.getDias()) {
            diaRepository.delete(dia);
        }
        for (HorarioAcessoDia novo : novosHorarios) {
            novo.setHorarioAcesso(horario);
            diaRepository.save(novo);
        }
        horario.setDias(novosHorarios);

        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        horario.setDataUltimaAlteracao(LocalDateTime.now());
        horario.setUsuarioAlteracao(usuarioAutenticado.getUsuario());

        var historico = HorarioAcessoHistorico.builder()
            .dataUltimaAlteracao(horario.getDataUltimaAlteracao())
            .usuarioAlteracao(horario.getUsuarioAlteracao())
            .horarioAcesso(horario)
            .diasAcesso(horario.getDias())
            .build();

        repository.save(horario);
        historicoRepository.save(historico);

        /**
         * implementar fluxo para salvar novos dias-acesso-hist
         * utilizando o historico novo
         */
    }

}
