package br.com.xbrain.autenticacao.modules.comum.dto;

import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class PageRequest implements Pageable {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE_PAGE = 10;

    private int page;
    private int size;
    private String orderBy;
    private String orderDirection;

    public PageRequest() {
        this.page = DEFAULT_PAGE;
        this.size = DEFAULT_SIZE_PAGE;
        this.orderBy = "id";
        this.orderDirection = "ASC";
    }

    public PageRequest(int page, int size, String orderBy, String orderDirection) {
        this.page = page;
        this.size = size;
        this.orderBy = orderBy;
        this.orderDirection = orderDirection;
    }

    @Override
    public int getOffset() {
        return page * size;
    }

    @Override
    public int getPageNumber() {
        return page;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public Sort getSort() {
        return new Sort(
            Sort.Direction.fromString(this.orderDirection),
            this.orderBy);
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
