package org.example.FrameworkUtils.Orm.MineBatis.configuration;

import lombok.Data;
import org.example.FrameworkUtils.Orm.MineBatis.mapping.ResultMap;


import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ziyuan
 * @since 2024.04
 */
@Data
//xxx:sql配置封装类,使用Dom4j进行解析
public class Configuration {
    private DataSource dataSource;
    //xxx:封装成map ,因为一个xml里的sql可能有多个
    private Map<String, MappedStatement> mappedStatementMap = new ConcurrentHashMap<>();
    //xxx:ResultMap
    private Map<String, ResultMap> resultMapMap = new ConcurrentHashMap<>();

    private Set<Class<?>> mapperLocations=new HashSet<>();

    public MappedStatement getMappedStatement(String id) {
        return this.mappedStatementMap.get(id);
    }

    public void addMappedStatement(String id, MappedStatement mappedStatement) {
        this.mappedStatementMap.put(id, mappedStatement);
    }

    public ResultMap getResultMap(String id) {
        return this.resultMapMap.get(id);
    }

    public void addResultMap(String id, ResultMap resultMap) {
        this.resultMapMap.put(id, resultMap);
    }
    public void addMapperLocation(String location) {
        try{
            this.mapperLocations.add(Class.forName(location));
        }catch (ClassNotFoundException e){

        }
    }
}
