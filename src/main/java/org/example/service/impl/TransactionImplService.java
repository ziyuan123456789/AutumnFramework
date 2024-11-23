package org.example.service.impl;

import com.autumn.transaction.Isolation;
import com.autumn.transaction.Propagation;
import com.autumn.transaction.annotation.AutumnTransactional;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyService;
import org.example.mapper.UpdateMapper;
import org.example.service.TransactionService;

import java.sql.SQLException;

/**
 * @author ziyuan
 * @since 2024.11
 */
@MyService
public class TransactionImplService implements TransactionService {

    @MyAutoWired
    private UpdateMapper updateMapper;

    @MyAutoWired
    private TransactionService transactionService;

    @Override
    @AutumnTransactional(rollbackFor = Exception.class,
            propagation = Propagation.REQUIRED,
            isolation = Isolation.DEFAULT
    )
    public String transactionTest() throws SQLException {
        updateMapper.insertUser("1", "1", "1", "1");
        try {
            transactionService.transactionRequireNew();
        } catch (Exception e) {

        }
        return "OK";
    }

    @Override
    @AutumnTransactional(propagation = Propagation.REQUIRES_NEW)
    public void transactionRequire() throws SQLException {
        updateMapper.insertUser("2", "2", "2", "2");
        throw new RuntimeException("测试");
    }

    @Override
    @AutumnTransactional(rollbackFor = Exception.class)
    public void transactionRequireNew() throws SQLException {
        updateMapper.insertUser("2", "2", "2", "2");
        throw new RuntimeException("测试");
    }

}
