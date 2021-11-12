package br.com.xbrain.autenticacao.modules.horarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoConsultaDto;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.DiaAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.DiaAcessoHistorico;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcessoHistorico;
import br.com.xbrain.autenticacao.modules.horarioacesso.predicate.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.DiaAcessoHistoricoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.DiaAcessoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoHistoricoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HorarioAcessoService {

    public static final ValidacaoException HORARIO_ACESSO_NAO_ENCONTRADO =
        new ValidacaoException("Horário de acesso não encontrado.");

    @Autowired
    private HorarioAcessoRepository repository;
    @Autowired
    private HorarioAcessoHistoricoRepository historicoRepository;
    @Autowired
    private DiaAcessoRepository diaRepository;
    @Autowired
    private DiaAcessoHistoricoRepository diaHistRepository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    public List<HorarioAcessoConsultaDto> getAll(HorarioAcessoFiltros filtros) {
        return repository.findAll(filtros.toPredicate().build())
            .stream()
            .map(HorarioAcessoConsultaDto::of)
            .collect(Collectors.toList());
    }

    public void editHorario(HorarioAcessoRequest request) {
        var horario = repository.findById(request.getId())
            .orElseThrow(() -> HORARIO_ACESSO_NAO_ENCONTRADO);
        var novosHorarios = request.getDiasAcesso()
            .stream()
            .map(DiaAcesso::converFrom)
            .collect(Collectors.toList());

        for (DiaAcesso dia : horario.getDiasAcesso()) {
            diaRepository.delete(dia);
        }
        for (DiaAcesso novo : novosHorarios) {
            novo.setHorarioAcesso(horario);
            diaRepository.save(novo);
        }
        horario.setDiasAcesso(novosHorarios);

        setDadosAlteracao(horario);

        var historico = HorarioAcessoHistorico.criaNovoHistorico(horario);

        repository.save(horario);
        historicoRepository.save(historico);

        horario.getDiasAcesso().stream().forEach(dia -> {
            var hist = DiaAcessoHistorico.criaDiaAcessoHistorico(dia);
            hist.setHorarioAcessoHistorico(historico);
            diaHistRepository.save(hist);
        });
    }

    private void setDadosAlteracao(HorarioAcesso horario) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        horario.setUltimaAlteracao(LocalDateTime.now());
        horario.setUsuarioAlteracao(usuarioAutenticado.getUsuario());
    }

}
