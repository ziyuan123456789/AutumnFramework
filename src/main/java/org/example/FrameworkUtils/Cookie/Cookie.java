package org.example.FrameworkUtils.Cookie;

import lombok.Data;

/**
 * @author ziyuan
 * @since 2023.12
 */
@Data
public class Cookie {
    private String cookieName;
    private String cookieValue;
    private Integer maxAge=3600;
    private String path="/";

    public Cookie(String cookieName, String cookieValue) {
        this.cookieName = cookieName;
        this.cookieValue = cookieValue;
    }

}
