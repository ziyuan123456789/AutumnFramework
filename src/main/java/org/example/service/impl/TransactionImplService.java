package org.example.service.impl;

import com.autumn.ormstarter.transaction.Isolation;
import com.autumn.ormstarter.transaction.Propagation;
import com.autumn.ormstarter.transaction.annotation.AutumnTransactional;
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
            isolation = Isolation.TRANSACTION_SERIALIZABLE
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
    @AutumnTransactional
    public void transactionRequire() {
        updateMapper.insertUser("2", "2", "2", "2");
        throw new RuntimeException("测试");
    }

    @Override
    @AutumnTransactional
    public void transactionRequireNew() throws SQLException {
        updateMapper.insertUser("2", "2", "2", "2");
        throw new SQLException("测试");
    }

}
