package org.example.FrameworkUtils.Orm.MineBatis;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.04
 */
public  class ParameterMappingTokenHandler implements TokenHandler {

    private List<ParameterMapping> parameterMappings = new ArrayList<>();


    @Override
    public String handleToken(String content) {
        parameterMappings.add(new ParameterMapping(content));
        return "?";
    }

    public List<ParameterMapping> getParameterMapping() {
        return parameterMappings;
    }
    public void resetParameterMappings(){
        parameterMappings.clear();
    }


}
