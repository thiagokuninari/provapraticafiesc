package br.com.xbrain.autenticacao.modules.usuario.enums;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCidadeDto;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumSet;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public enum CodigoHierarquia {

    REGIONAL("Regional") {
        @Override
        public List<UsuarioCidadeDto> recupaEquipe(Integer id) {
            return cidadeService.getAllByRegionalId(id);
        }
    },
    GRUPO("Grupo") {
        @Override
        public List<UsuarioCidadeDto> recupaEquipe(Integer id) {
            return cidadeService.getAllByGrupoId(id);
        }
    },
    CLUSTER("Cluster") {
        @Override
        public List<UsuarioCidadeDto> recupaEquipe(Integer id) {
            return cidadeService.getAllByClusterId(id);
        }
    },
    SUBCLUSTER("sub-cluster") {
        @Override
        public List<UsuarioCidadeDto> recupaEquipe(Integer id) {
            return cidadeService.getAllBySubClusterId(id);
        }
    };

    @Getter
    private String hierarquia;
    @Getter
    @Setter
    public CidadeService cidadeService;

    CodigoHierarquia(String hierarquia) {
        this.hierarquia = hierarquia;
    }

    public abstract List<UsuarioCidadeDto> recupaEquipe(Integer id);

    @Component
    public static class ServiceInjector {
        @Autowired
        private CidadeService cidadeService;

        @PostConstruct
        public void postConstruct() {
            for (CodigoHierarquia hierarquia : EnumSet.allOf(CodigoHierarquia.class)) {
                hierarquia.setCidadeService(cidadeService);
            }
        }
    }

}
