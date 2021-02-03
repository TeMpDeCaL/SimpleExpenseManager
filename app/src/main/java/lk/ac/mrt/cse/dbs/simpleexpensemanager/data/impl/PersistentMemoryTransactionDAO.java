package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.DbHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentMemoryTransactionDAO implements TransactionDAO {

    private Context context;
    private DbHandler dbHandler;

    public PersistentMemoryTransactionDAO(Context context){
        this.context = context;
        dbHandler = new DbHandler(this.context);
    }
    /***
     * Log the transaction requested by the user.
     *
     * @param date        - date of the transaction
     * @param accountNo   - account number involved
     * @param expenseType - type of the expense
     * @param amount      - amount involved
     */
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount){
        dbHandler.logTransaction(date,accountNo,expenseType,amount);
    }

    public List<Transaction> getAllTransactionLogs(){
        return dbHandler.getAllTransactionLogs();
    }

    public List<Transaction> getPaginatedTransactionLogs(int limit){
        return dbHandler.getPaginatedTransactionLogs(limit);
    }
}
