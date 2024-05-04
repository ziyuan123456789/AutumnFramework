package org.example.FrameworkUtils.Orm.MineBatis.configuration;

import lombok.Data;
import org.example.FrameworkUtils.Orm.MineBatis.ParameterMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.04
 */
@Data
public class BoundSql {

    //xxx:sql原型
    private String originalSqlText;
    //xxx:解析后的jdbcsql
    private String jdbcsqlText;
    //xxx:参数集合
    private List<ParameterMapping> paramsParameterMappingList = new ArrayList<>();

    public BoundSql(String originalSqlText, String jdbcsqlText) {
        this.originalSqlText = originalSqlText;
        this.jdbcsqlText = jdbcsqlText;
    }

    public BoundSql() {

    }

}
