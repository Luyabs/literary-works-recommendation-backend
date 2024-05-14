package edu.shu.abs.common;

public class Page<T> extends com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> {
    public Page(long current, long size) {
        super(current, size);
        setOptimizeCountSql(false);
    }
}
