package org.example.FrameworkUtils.ResponseType.Views;

import lombok.Data;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
@Data
public class View {
    private String htmlName;
    public View(String htmlName){
        this.htmlName = htmlName;
    }
}
