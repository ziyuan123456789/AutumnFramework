package org.example.FrameworkUtils.Orm.MineBatis.mapping;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.04
 */
@Data
public class ResultMap {
    private String id;
    private String type;
    private boolean isDisable;
    private List<ResultMapField> fields = new ArrayList<>();

    public ResultMap(String id, String type,boolean isDisable) {
        this.id = id;
        this.type = type;
        this.isDisable=isDisable;
    }

    public ResultMap() {
    }

    public void addField(ResultMapField field) {
        this.fields.add(field);
    }


}
