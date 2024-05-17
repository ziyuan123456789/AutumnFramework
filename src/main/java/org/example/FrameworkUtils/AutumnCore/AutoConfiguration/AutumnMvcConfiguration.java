package org.example.FrameworkUtils.AutumnCore.AutoConfiguration;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;

/**
 * @author ziyuan
 * @since 2023.11
 */
public interface AutumnMvcConfiguration {
     View getMainPage();
     View get404Page();
}
