package org.example.Bean;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ziyuan
 * @since 2023.11
 */
@Data
public class User {
    private int userID;
    private String username;
    private String role;
    private String password;
    private String salt;
    private String telephone;
    private LocalDateTime regTime;
    private String enabled;
}
