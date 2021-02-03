package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.DbHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentMemoryAccountDAO implements AccountDAO {

    private Context context;
    private DbHandler dbHandler;

    public PersistentMemoryAccountDAO(Context context){
        this.context = context;
        dbHandler = new DbHandler(this.context);
    }

    @Override
    public List<String> getAccountNumbersList(){
        return dbHandler.getAccountNumbersList();
    }

    @Override
    public List<Account> getAccountsList(){
        return dbHandler.getAccountsList();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException{
        return dbHandler.getAccount(accountNo);
    }

    @Override
    public void addAccount(Account account){
        dbHandler.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException{
        dbHandler.removeAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException{
        dbHandler.updateBalance(accountNo,expenseType,amount);
    }

}
