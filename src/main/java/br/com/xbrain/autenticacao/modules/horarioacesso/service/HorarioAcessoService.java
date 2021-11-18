package br.com.xbrain.autenticacao.modules.horarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.DiaAcessoResponse;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoConsultaDto;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.DiaAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.DiaAcessoHistorico;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcessoHistorico;
import br.com.xbrain.autenticacao.modules.horarioacesso.predicate.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.DiaAcessoHistoricoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.DiaAcessoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoHistoricoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HorarioAcessoService {

    public static final ValidacaoException HORARIO_ACESSO_NAO_ENCONTRADO =
        new ValidacaoException("Horário de acesso não encontrado.");

    @Autowired
    private HorarioAcessoRepository repository;
    @Autowired
    private HorarioAcessoHistoricoRepository historicoRepository;
    @Autowired
    private DiaAcessoRepository diaAcessoRepository;
    @Autowired
    private DiaAcessoHistoricoRepository diaAcessoHistoricoRepository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    public List<HorarioAcessoConsultaDto> getAll(HorarioAcessoFiltros filtros) {
        var horarios = repository.findAll(filtros.toPredicate().build())
            .stream()
            .map(HorarioAcessoConsultaDto::of)
            .collect(Collectors.toList());
        horarios.forEach(h -> h.setDiasAcesso(diaAcessoRepository
            .findByHorarioAcessoId(h.getId())));
        return horarios;
    }

    public List<HorarioAcessoConsultaDto> getHistorico(Integer horarioAcessoId) {
        var historicos = historicoRepository.findByHorarioAcessoId(horarioAcessoId)
            .stream()
            .map(HorarioAcessoConsultaDto::of)
            .collect(Collectors.toList());
        historicos.forEach(h -> h.setDiasAcessoHist(diaAcessoHistoricoRepository
            .findByHorarioAcessoHistoricoId(h.getId())));
        return historicos;
    }

    public HorarioAcessoConsultaDto save(HorarioAcessoRequest request) {
        HorarioAcesso horario = new HorarioAcesso();

        if (Objects.isNull(request.getId())) {
            horario = HorarioAcesso.converFrom(request);
        } else {
            horario = repository.findById(request.getId())
                .orElseThrow(() -> HORARIO_ACESSO_NAO_ENCONTRADO);
            diaAcessoRepository.deleteByHorarioAcessoId(horario.getId());
        }
        horario.setDadosAlteracao(autenticacaoService.getUsuarioAutenticado().getUsuario());
        repository.save(horario);

        var historico = HorarioAcessoHistorico.criaNovoHistorico(horario);
        historicoRepository.save(historico);
        criaDiasAcesso(request.getDiasAcesso(), horario, historico);
    
        return HorarioAcessoConsultaDto.of(horario);
    }

    private void criaDiasAcesso(List<DiaAcessoResponse> response, HorarioAcesso horario, HorarioAcessoHistorico historico) {
        if (!ObjectUtils.isEmpty(response)) {
            var diasAcesso = response.stream()
                .map(DiaAcesso::converFrom)
                .collect(Collectors.toList());

            diasAcesso.forEach(dia -> {
                dia.setHorarioAcesso(horario);
                diaAcessoRepository.save(dia);
                diaAcessoHistoricoRepository.save(DiaAcessoHistorico
                        .criaDiaAcessoHistorico(dia, historico));
            });
        }
    }

}
