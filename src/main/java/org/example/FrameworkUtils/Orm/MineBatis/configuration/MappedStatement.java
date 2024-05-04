package org.example.FrameworkUtils.Orm.MineBatis.configuration;

import lombok.Data;

/**
 * @author ziyuan
 * @since 2024.04
 */
//xxx:minebatis-config.xml配置文件 sql语句映射源信息
@Data
public class MappedStatement {
    //xxx:每条sql语句的唯一标识
    private String id;
    //xxx:返回值类型
    private String resultType;
    //xxx:sql语句
    private String sql;
    //xxx:参数类型
    private String parameterType;
    //xxx:crudType
    private String sqlCommandType;
    //xxx:resultMap的唯一标识
    private String resultMapId;
}
