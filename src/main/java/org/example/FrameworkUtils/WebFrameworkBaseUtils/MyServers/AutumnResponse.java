package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.Cookie.Cookie;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;

/**
 * @author ziyuan
 * @since 2024.05
 */
public interface AutumnResponse {
    AutumnResponse setView(View view);
    AutumnResponse setCookie(Cookie cookie);
    AutumnResponse setCode(int code);
    AutumnResponse setResponseText(String responseText);
    void outputMessage();

    void outputErrorMessage(String title, String text, int code);

    void outputHtml();
}
