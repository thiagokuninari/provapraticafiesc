package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import static br.com.xbrain.autenticacao.modules.horarioacesso.model.QHorarioAcesso.horarioAcesso;
import static br.com.xbrain.autenticacao.modules.horarioacesso.model.QHorarioAtuacao.horarioAtuacao;

public class HorarioAcessoRepositoryImpl implements HorarioAcessoRepositoryCustom {

    public static final ValidacaoException HORARIO_ACESSO_NAO_ENCONTRADO =
        new ValidacaoException("Horário de acesso não encontrado.");

    @Autowired
    private EntityManager entityManager;

    @Override
    public PageImpl<HorarioAcessoResponse> findAll(Pageable pageable, Predicate predicate) {
        var horariosAcesso = new JPAQueryFactory(entityManager)
            .select(horarioAcesso)
            .from(horarioAcesso)
            .where(predicate)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(horarioAcesso.id.asc())
            .fetch()
            .stream()
            .map(HorarioAcessoResponse::of)
            .collect(Collectors.toList());
        horariosAcesso.forEach(horario -> horario.setHorariosAtuacao(
            getHorariosAtuacao(horario.getHorarioAcessoId())));
        return new PageImpl<>(horariosAcesso, pageable, countHorariosAcesso(predicate));
    }

    @Override
    public HorarioAcessoResponse findById(Integer id) {
        var horario = HorarioAcessoResponse.of(Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .select(horarioAcesso)
                .from(horarioAcesso)
                .where(horarioAcesso.id.eq(id))
                .fetchOne())
            .orElseThrow(() -> HORARIO_ACESSO_NAO_ENCONTRADO));
        horario.setHorariosAtuacao(getHorariosAtuacao(horario.getHorarioAcessoId()));
        return horario;
    }

    private List<HorarioAtuacao> getHorariosAtuacao(Integer horarioAcessoId) {
        return new JPAQueryFactory(entityManager)
                .select(horarioAtuacao)
                .from(horarioAtuacao)
                .where(horarioAtuacao.horarioAcesso.id.eq(horarioAcessoId))
                .fetch();
    }

    private long countHorariosAcesso(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(horarioAcesso)
                .from(horarioAcesso)
                .where(predicate)
                .fetchCount();
    }
}
