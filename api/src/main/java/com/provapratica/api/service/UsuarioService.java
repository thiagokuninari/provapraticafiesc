package com.provapratica.api.service;

import com.provapratica.api.comun.CpfUtil;
import com.provapratica.api.domain.Usuario;
import com.provapratica.api.dto.UsuarioRequest;
import com.provapratica.api.dto.UsuarioResponse;
import com.provapratica.api.enums.ENivelUsuario;
import com.provapratica.api.repository.UsuarioRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioResponse save(UsuarioRequest usuarioRequest) {

        validarCpf(usuarioRequest.getCpf());
        validarPadraoEmail(usuarioRequest.getEmail());
        validarCamposObrigatorios(usuarioRequest);
        var usuario = usuarioRepository.save(Usuario.of(usuarioRequest));
        return UsuarioResponse.of(usuario);
    }

    private void validarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new ValidationException("CPF não pode ser nulo ou vazio");
        }

        var cpfSemMascara = CpfUtil.removerCaracteresDoCpf(cpf);

        if (!CpfUtil.isCpfValido(cpfSemMascara)) {
            throw new ValidationException("CPF inválido");
        }

        validarCpfExistente(cpfSemMascara);
    }

    private void validarCpfExistente(String cpfSemMascara) {
        usuarioRepository.findByCpf(cpfSemMascara)
                .ifPresent(usuario -> {
                    throw new ValidationException("CPF já cadastrado para o usuario.");
                });
    }

    private void validarPadraoEmail(String email) {
        var pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.\\+]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)"
                + "|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})$");
        var matcher = pattern.matcher(email);
        if (!matcher.find()) {
            throw new ValidationException("Email inválido.");
        }
    }


    private void validarCamposObrigatorios(UsuarioRequest request) {
        var codigoNivel = request.getNivel().getCodigo();

        if (codigoNivel == ENivelUsuario.PROFESSOR) {
            validarEspecialidade(request.getEspecialidade());
        }

        if (codigoNivel == ENivelUsuario.ESTUDANTE) {
            validarCamposObrigatoriosEstudante(request);
        }
    }

    private static void validarCamposObrigatoriosEstudante (UsuarioRequest request) {
        validarCep(request.getCep());
        validarLogradouro(request.getLogradouro());
        validarNumero(request.getNumero());
        validarBairro(request.getBairro());
        validarEstado(request.getEstado());
        validarCidade(request.getCidade());
        validarWhatsapp(request.getWhatsapp());
    }

    private static void validarEspecialidade(String especialidade) {
        if (especialidade == null || especialidade.isBlank()) {
            throw new IllegalArgumentException("A especialidade é obrigatória para usuários do tipo PROFESSOR.");
        }
    }

    private static void validarCep(String cep) {
        if (cep == null || cep.isBlank()) {
            throw new IllegalArgumentException("O CEP é obrigatório para usuários do tipo ESTUDANTE.");
        }
    }

    private static void validarLogradouro(String logradouro) {
        if (logradouro == null || logradouro.isBlank()) {
            throw new IllegalArgumentException("O logradouro é obrigatório para usuários do tipo ESTUDANTE.");
        }
    }

    private static void validarNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("O número é obrigatório para usuários do tipo ESTUDANTE.");
        }
    }

    private static void validarBairro(String bairro) {
        if (bairro == null || bairro.isBlank()) {
            throw new IllegalArgumentException("O bairro é obrigatório para usuários do tipo ESTUDANTE.");
        }
    }

    private static void validarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("O estado é obrigatório para usuários do tipo ESTUDANTE.");
        }
    }

    private static void validarCidade(String cidade) {
        if (cidade == null || cidade.isBlank()) {
            throw new IllegalArgumentException("A cidade é obrigatória para usuários do tipo ESTUDANTE.");
        }
    }

    private static void validarWhatsapp(String whatsapp) {
        if (whatsapp == null || whatsapp.isBlank()) {
            throw new IllegalArgumentException("O WhatsApp é obrigatório para usuários do tipo ESTUDANTE.");
        }
    }
}
