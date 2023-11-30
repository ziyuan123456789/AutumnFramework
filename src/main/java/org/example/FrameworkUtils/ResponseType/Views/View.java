package org.example.FrameworkUtils.ResponseType.Views;

import lombok.Data;

/**
 * @author ziyuan
 * @since 2023.11
 */
@Data
public class View {
    private String htmlName;
    private String htmlHome;
    public View(String htmlName){
        this.htmlName = htmlName;
    }

    public View(String htmlHome, String htmlName) {
        this.htmlHome = htmlHome;
        this.htmlName = htmlName;
    }
}
