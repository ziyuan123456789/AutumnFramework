package com.autumn.ormstarter.minijpa;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSession;

/**
 * @author ziyuan
 * @since 2024.09
 */
public class MiniJpaCglibFactory {
    @MyAutoWired
    private SqlSession sqlSession;


}
