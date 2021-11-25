package br.com.xbrain.autenticacao.modules.horarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioHistorico;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAtuacaoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioHistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class HorarioAcessoService {

    @Autowired
    private HorarioAcessoRepository repository;
    @Autowired
    private HorarioAtuacaoRepository atuacaoRepository;
    @Autowired
    private HorarioHistoricoRepository historicoRepository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    public PageImpl<HorarioAcessoResponse> getHorariosAcesso(PageRequest pageable, HorarioAcessoFiltros filtros) {
        return repository.findAll(pageable, filtros.toPredicate().build());
    }

    public PageImpl<HorarioAcessoResponse> getHistoricos(PageRequest pageable, Integer horarioAcessoId) {
        return historicoRepository.findByHorarioAcessoId(pageable, horarioAcessoId);
    }

    public HorarioAcessoResponse getHorarioAcesso(Integer id) {
        return repository.findById(id);
    }

    public HorarioAcesso save(HorarioAcessoRequest request) {
        HorarioAcesso horarioAcesso = new HorarioAcesso();

        if (isNull(request.getId())) {
            horarioAcesso = HorarioAcesso.of(request);
        } else {
            horarioAcesso = HorarioAcesso.of(repository.findById(request.getId()));
            desreferenciaHorarioAtuacao(horarioAcesso);
        }
        horarioAcesso.setDadosAlteracao(autenticacaoService.getUsuarioAutenticado().getUsuario());

        horarioAcesso = repository.save(horarioAcesso);

        var historico = HorarioHistorico.of(horarioAcesso);
        historico = historicoRepository.save(historico);

        criaHorariosAcesso(request.getHorariosAtuacao()
                .stream()
                .map(HorarioAtuacao::of)
                .collect(Collectors.toList()),
            horarioAcesso, 
            historico);
        
        return horarioAcesso;
    }

    private void desreferenciaHorarioAtuacao(HorarioAcesso horarioAtuacao) {
        var horariosAtuacao = atuacaoRepository.findByHorarioAcessoId(horarioAtuacao.getId());
        horariosAtuacao.forEach(atuacao -> atuacao.setHorarioAcesso(null));
        horariosAtuacao.forEach(atuacao -> atuacaoRepository.save(atuacao));
    }

    public void criaHorariosAcesso(List<HorarioAtuacao> horariosAtuacao,
                                    HorarioAcesso horarioAcesso,
                                    HorarioHistorico horarioHistorico) {
        try {
            horariosAtuacao.forEach(atuacao -> {
                atuacao.setHorarioAcesso(horarioAcesso);
                atuacao.setHorarioHistorico(horarioHistorico);
                atuacaoRepository.save(atuacao);
            });
        } catch (Exception ex) {
            throw ex;
        }
    }
}
