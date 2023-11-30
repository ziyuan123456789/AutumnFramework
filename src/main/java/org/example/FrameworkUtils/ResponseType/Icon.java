package org.example.FrameworkUtils.ResponseType;

import lombok.Data;

/**
 * @author ziyuan
 * @since 2023.11
 */
@Data
public class Icon {
    private String iconName;
    public Icon(String iconName){
        this.iconName = iconName;
    }

}
