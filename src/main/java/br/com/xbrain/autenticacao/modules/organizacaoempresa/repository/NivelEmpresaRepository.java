package br.com.xbrain.autenticacao.modules.organizacaoempresa.repository;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.NivelEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NivelEmpresaRepository extends JpaRepository<NivelEmpresa, Integer> {

    Optional<NivelEmpresa> findById(Integer id);
}
