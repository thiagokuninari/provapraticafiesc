package br.com.xbrain.autenticacao.modules.comum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MongoosePage<T> {

    private List<T> docs;
    private Long totalDocs;

    public Page<T> toSpringPage(PageRequest pageRequest) {
        return new PageImpl<>(docs, pageRequest, totalDocs);
    }

    public static <T> MongoosePage<T> empty() {
        return MongoosePage.<T>builder()
            .docs(List.of())
            .totalDocs(0L)
            .build();
    }
}
