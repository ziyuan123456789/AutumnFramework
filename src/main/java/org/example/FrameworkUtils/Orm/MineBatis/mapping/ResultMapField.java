package org.example.FrameworkUtils.Orm.MineBatis.mapping;

import lombok.Data;

/**
 * @author ziyuan
 * @since 2024.04
 */
@Data
public class ResultMapField {
    //xxx:数据库字段名
    private String column;
    //xxx:实体类字段名
    private String property;
    private String javaType;
    private String jdbcType;

    public ResultMapField(String column, String property, String jdbcType, String javaType) {
        this.column = column;
        this.property = property;
        this.jdbcType = jdbcType;
        this.javaType = javaType;
    }

}
