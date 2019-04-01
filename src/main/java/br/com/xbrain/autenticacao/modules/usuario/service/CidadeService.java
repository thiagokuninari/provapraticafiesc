package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.ClusterizacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCidadeDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoHierarquia;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CidadePredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CidadeService {

    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Cidade não encontrada.");
    private static final Integer MAXIMO = 1000;
    private static final Integer MINIMO = 1001;
    @Getter
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CidadeRepository repository;

    public List<UsuarioCidadeDto> getAllByRegionalId(Integer regionalId) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        CidadePredicate predicate = new CidadePredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return UsuarioCidadeDto.parse(repository.findAllByRegionalId(regionalId, predicate.build()));
    }

    public List<UsuarioCidadeDto> getAllBySubClusterId(Integer subClusterId) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        CidadePredicate predicate = new CidadePredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return UsuarioCidadeDto.parse(repository.findAllBySubClusterId(subClusterId, predicate.build()));
    }

    public List<UsuarioCidadeDto> getAllBySubClustersId(List<Integer> subClustersId) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        CidadePredicate predicate = new CidadePredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return UsuarioCidadeDto.parse(repository.findAllBySubClustersId(subClustersId, predicate.build()));
    }

    public List<UsuarioCidadeDto> getAllByGrupoId(Integer grupoId) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        CidadePredicate predicate = new CidadePredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return UsuarioCidadeDto.parse(repository.findAllByGrupoId(grupoId, predicate.build()));
    }

    public List<UsuarioCidadeDto> getAllByClusterId(Integer clusterId) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        CidadePredicate predicate = new CidadePredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return UsuarioCidadeDto.parse(repository.findAllByClusterId(clusterId, predicate.build()));
    }

    public List<Cidade> getAllCidadeByUf(Integer idUf) {
        return repository.findCidadeByUfId(idUf, new Sort("nome"));
    }

    public List<Cidade> getAllBySubCluster(Integer idSubCluster) {
        return repository.findBySubCluster(idSubCluster);
    }

    public Cidade findByUfNomeAndCidadeNome(String uf, String cidade) {
        CidadePredicate predicate = new CidadePredicate();
        predicate.comNome(cidade);
        predicate.comUf(uf);
        return repository
                .findByPredicate(predicate.build())
                .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public ClusterizacaoDto getClusterizacao(Integer id) {
        return repository.getClusterizacao(id);
    }

    public List<UsuarioResponse> getSupervisoresByHierarquia(String hierarquia, Integer id) {
        List<UsuarioCidadeDto> cidades = null;
        if (!hierarquia.equalsIgnoreCase("CIDADE")) {
            CodigoHierarquia codigoHierarquia = CodigoHierarquia.valueOf(hierarquia);
            cidades = codigoHierarquia.recupaEquipe(id);
        } else {
            return usuarioService.getSupervisoresByCidades(Arrays.asList(id));
        }
        List<Integer> cidadesId = cidades.stream()
                .map(usuarioCidade -> usuarioCidade.getIdCidade())
                .collect(Collectors.toList());
        return recuperarUsuarios(cidadesId);
    }

    public List<UsuarioResponse> recuperarUsuarios(List<Integer> cidadesId) {
        if (cidadesId.size() > MAXIMO) {
            List<Integer> segundaLista = cidadesId.subList(MINIMO, cidadesId.size());
            cidadesId = cidadesId.subList(0, MAXIMO);
            List<UsuarioResponse> listaUsuario = usuarioService.getSupervisoresByCidades(segundaLista);
            List<UsuarioResponse> listaUsuarioComplementar = usuarioService.getSupervisoresByCidades(cidadesId);
            listaUsuario.addAll(listaUsuarioComplementar);
            return listaUsuario;
        }
        return usuarioService.getSupervisoresByCidades(cidadesId);
    }
}
