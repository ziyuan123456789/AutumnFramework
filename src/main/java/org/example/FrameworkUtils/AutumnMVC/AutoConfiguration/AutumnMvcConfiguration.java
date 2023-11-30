package org.example.FrameworkUtils.AutumnMVC.AutoConfiguration;

import org.example.FrameworkUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.Webutils.Request;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
public interface AutumnMvcConfiguration {
     View getMainPage();
     View get404Page();
}
