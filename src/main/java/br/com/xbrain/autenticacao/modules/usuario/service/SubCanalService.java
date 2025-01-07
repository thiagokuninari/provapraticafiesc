package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalCompletDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalHistoricoResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanalHistorico;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.SubCanalHistoricoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.SubCanalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubCanalService {

    public static final List<Integer> FUNC_CONSULTAR_INDICACAO_PREMIUM = List.of(3062);
    public static final List<Integer> FUNC_CONSULTAR_INDICACAO_INSIDE_SALES_PME = List.of(3071);
    private static final ValidacaoException SUBCANAL_NAO_ENCONTRADO =
        new ValidacaoException("Erro, subcanal não encontrado.");

    private final SubCanalRepository repository;
    private final SubCanalHistoricoRepository subCanalHistoricoRepository;
    private final UsuarioService usuarioService;
    private final AutenticacaoService autenticacaoService;

    public List<SubCanalDto> getAll() {
        return repository.findAll()
            .stream()
            .map(SubCanalDto::of)
            .collect(Collectors.toList());
    }

    public Page<SubCanalCompletDto> getAllConfiguracoes(PageRequest pageRequest, SubCanalFiltros filtros) {
        return repository.findAll(filtros.toPredicate().build(), pageRequest)
            .map(SubCanalCompletDto::of);
    }

    public SubCanalDto getSubCanalById(Integer id) {
        return SubCanalDto.of(findById(id));
    }

    public SubCanalCompletDto getSubCanalCompletById(Integer id) {
        return SubCanalCompletDto.of(findById(id));
    }

    private SubCanal findById(Integer id) {
        return repository.findById(id)
            .orElseThrow(() -> SUBCANAL_NAO_ENCONTRADO);
    }

    public Set<SubCanalDto> getSubCanalByUsuarioId(Integer usuarioId) {
        return usuarioService.buscarSubCanaisPorUsuarioId(usuarioId)
            .stream()
            .map(SubCanalDto::of)
            .collect(Collectors.toSet());
    }

    public void adicionarPermissaoIndicacaoPremium(Usuario usuario) {
        if (usuario.isNivelOperacao() && usuario.hasSubCanalPapPremium()) {
            var permissaoPapPremium = usuarioService.getPermissoesEspeciaisDoUsuario(
                usuario.getId(),
                usuario.getUsuarioCadastro().getId(),
                FUNC_CONSULTAR_INDICACAO_PREMIUM);

            usuarioService.salvarPermissoesEspeciais(permissaoPapPremium);
        }
    }

    public void removerPermissaoIndicacaoPremium(Usuario usuario) {
        if (!usuario.isNovoCadastro() && usuario.isNivelOperacao()) {
            usuarioService.removerPermissoesEspeciais(FUNC_CONSULTAR_INDICACAO_PREMIUM, List.of(usuario.getId()));
        }
    }

    public void adicionarPermissaoIndicacaoInsideSalesPme(Usuario usuario) {
        if (usuario.isNivelOperacao() && usuario.hasSubCanalInsideSalesPme()) {
            var permissaoInsideSalesPme = usuarioService.getPermissoesEspeciaisDoUsuario(
                usuario.getId(),
                usuario.getUsuarioCadastro().getId(),
                FUNC_CONSULTAR_INDICACAO_INSIDE_SALES_PME);

            usuarioService.salvarPermissoesEspeciais(permissaoInsideSalesPme);
        }
    }

    public void removerPermissaoIndicacaoInsideSalesPme(Usuario usuario) {
        if (!usuario.isNovoCadastro() && usuario.isNivelOperacao()) {
            usuarioService.removerPermissoesEspeciais(FUNC_CONSULTAR_INDICACAO_INSIDE_SALES_PME, List.of(usuario.getId()));
        }
    }

    public void editar(SubCanalCompletDto request) {
        var usuarioAutenticado = this.autenticacaoService.getUsuarioAutenticado();
        this.validarUsuarioAdm(usuarioAutenticado);

        var subCanal = this.findById(request.getId());
        var subCanalHistorico = SubCanalHistorico.of(subCanal, request, usuarioAutenticado);
        repository.save(subCanal.editar(request));

        subCanalHistorico.setSubCanal(subCanal);
        this.subCanalHistoricoRepository.save(subCanalHistorico);
    }

    private void validarUsuarioAdm(UsuarioAutenticado usuarioAutenticado) {
        if (!usuarioAutenticado.isXbrain()) {
            throw new PermissaoException("O usuário logado não possuí permissão para acessar essa funcionalidade.");
        }
    }

    public Eboolean isNovaChecagemCreditoD2d(Integer id) {
        return findById(id).getNovaChecagemCredito();
    }

    public Eboolean isNovaChecagemViabilidadeD2d(Integer id) {
        return findById(id).getNovaChecagemViabilidade();
    }

    public Page<SubCanalHistoricoResponse> getHistorico(Integer id, PageRequest pageable) {
        return subCanalHistoricoRepository.findBySubCanal_Id(id, pageable)
            .map(SubCanalHistoricoResponse::of);
    }

    public Eboolean isRealizarEnriquecimentoEnd(Integer id) {
        return findById(id).getRealizarEnriquecimentoEnd();
    }
}
