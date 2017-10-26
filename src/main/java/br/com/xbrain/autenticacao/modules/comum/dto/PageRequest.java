package br.com.xbrain.autenticacao.modules.comum.dto;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageRequest implements Pageable {

    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LIMIT = 102;

    private int offset;
    private int limit;
    private String orderBy;
    private String orderDirection;

    public PageRequest() {
        this.offset = DEFAULT_OFFSET;
        this.limit = DEFAULT_LIMIT;
        this.orderBy = "id";
        this.orderDirection = "ASC";
    }

    public PageRequest(int offset, int limit, String orderBy, String orderDirection) {
        this.offset = offset;
        this.limit = limit;
        this.orderBy = orderBy;
        this.orderDirection = orderDirection;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }

    @Override
    public int getPageNumber() {
        return offset;
    }

    @Override
    public int getPageSize() {
        return limit;
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