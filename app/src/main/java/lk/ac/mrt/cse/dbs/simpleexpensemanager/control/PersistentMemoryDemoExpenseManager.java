package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemoryAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemoryTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;


public class PersistentMemoryDemoExpenseManager extends ExpenseManager {

    private Context context;

    public PersistentMemoryDemoExpenseManager(Context context) {
        this.context=context;
        setup();
    }

    @Override
    public void setup() {


        TransactionDAO persistentMemoryTransactionDAO = new PersistentMemoryTransactionDAO(context);
        setTransactionsDAO(persistentMemoryTransactionDAO);

        AccountDAO persistentMemoryAccountDAO = new PersistentMemoryAccountDAO(context);
        setAccountsDAO(persistentMemoryAccountDAO);

        //Test dummy data
        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
        getAccountsDAO().addAccount(dummyAcct1);
        getAccountsDAO().addAccount(dummyAcct2);


    }
}
