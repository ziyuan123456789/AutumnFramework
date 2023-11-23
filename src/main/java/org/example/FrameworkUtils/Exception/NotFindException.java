package org.example.FrameworkUtils.Exception;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
public class NotFindException extends RuntimeException{
    public NotFindException(){

    }
    public NotFindException(String s){
        super(s);
    }
}


