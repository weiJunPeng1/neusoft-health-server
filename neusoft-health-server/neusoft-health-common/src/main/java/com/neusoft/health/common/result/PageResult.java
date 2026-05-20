package com.neusoft.health.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页查询结果
 * <p>
 * 用于封装所有分页查询的响应结果，包含总数、当前页、每页条数和数据列表。
 * </p>
 *
 * @param <T> 数据列表中的元素类型
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class PageResult<T> {

    /**
     * 总记录数
     */
    private long total;
    /**
     * 当前页码，从1开始
     */
    private int page;
    /**
     * 每页显示条数
     */
    private int pageSize;
    /**
     * 当前页数据列表
     */
    private List<T> list;

    /**
     * 静态工厂方法，创建分页结果对象
     *
     * @param total    总记录数
     * @param page     当前页码
     * @param pageSize 每页条数
     * @param list     数据列表
     * @param <T>      数据类型
     * @return 分页结果对象
     */
    public static <T> PageResult<T> of(long total, int page, int pageSize, List<T> list) {
        return new PageResult<>(total, page, pageSize, list);
    }
}
