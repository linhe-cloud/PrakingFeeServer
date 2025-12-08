package com.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private Long total;

    /** 当前页数据 */
    private List<T> rows;

    /** 当前页码 */
    private Long current;

    /** 每页大小 */
    private Long size;

    /** 总页数 */
    private Long pages;

    /** 是否有上一页 */
    private Boolean hasPrevious;

    /** 是否有下一页 */
    private Boolean hasNext;

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> build(List<T> rows, Long total, Long current, Long size) {
        PageResult<T> result = new PageResult<>();
        result.setRows(rows);
        result.setTotal(total);
        result.setCurrent(current);
        result.setSize(size);
        result.setPages((total + size - 1) / size); // 计算总页数
        result.setHasPrevious(current > 1);
        result.setHasNext(current < result.getPages());
        return result;
    }
}
