package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;

import static org.springframework.util.ObjectUtils.isEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CargoRequest {

    private Integer id;
    @NotNull
    private String nome;
    private Nivel nivel;
    private CodigoCargo codigo;
    private ESituacao situacao;

    public static Cargo convertFrom(CargoRequest request) {
        Cargo model = new Cargo();
        BeanUtils.copyProperties(request, model);

        model.setId(!isEmpty(request.getId()) ? request.getId() : null);
        model.setCodigo(!isEmpty(request.getCodigo()) ? request.getCodigo() : null);
        model.setNivel(!isEmpty(request.getNivel()) ? new Nivel(request.getNivel().getId()) : null);
        model.setSituacao(!isEmpty(request.getSituacao()) ? request.getSituacao() : null);

        return model;
    }
}
