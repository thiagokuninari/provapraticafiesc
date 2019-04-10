package br.com.xbrain.autenticacao.modules.usuario.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CargoSuperiorRepository {

    @Autowired
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<Integer> getCargosHierarquia(Integer cargoId) {
        return ((List<BigDecimal>) entityManager
                .createNativeQuery(
                        " SELECT DISTINCT CS.FK_CARGO "
                                + "FROM CARGO_SUPERIOR CS "
                                + "START WITH CS.FK_CARGO_SUPERIOR = :_cargoId "
                                + "CONNECT BY NOCYCLE PRIOR CS.FK_CARGO = CS.FK_CARGO_SUPERIOR ")
                .setParameter("_cargoId", cargoId)
                .getResultList())
                .stream()
                .map(BigDecimal::intValue)
                .collect(Collectors.toList());
    }
}
