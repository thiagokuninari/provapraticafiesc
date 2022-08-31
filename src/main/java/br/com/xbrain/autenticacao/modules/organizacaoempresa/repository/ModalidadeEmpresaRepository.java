package br.com.xbrain.autenticacao.modules.organizacaoempresa.repository;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.ModalidadeEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModalidadeEmpresaRepository extends JpaRepository<ModalidadeEmpresa, Integer> {

    Optional<ModalidadeEmpresa> findById(Integer id);
}
