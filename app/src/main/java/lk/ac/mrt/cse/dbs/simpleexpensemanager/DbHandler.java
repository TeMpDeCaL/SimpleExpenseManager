package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DbHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "180553V";

    // Table Names
    private static final String TABLE_ACCOUNT = "accounts";
    private static final String TABLE_TRANSACTION = "transactions";

    // Common column names
    private static final String KEY_ACCNO = "accountNo";

    //Accounts Table - column names
    private static final String KEY_BNAME = "bankName";
    private static final String KEY_ACCHNAME = "accountHolderName";
    private static final String KEY_BAL = "balance";


    //TRANSACTION Table - column names
    private static final String KEY_EXPTYPE = "expenseType";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_DATE = "date";


    // Table Create Statements
    //ACCOUNT TABLE
    private static final String CREATE_TABLE_ACCOUNT = "CREATE TABLE " + TABLE_ACCOUNT
            + "("
            + KEY_ACCNO + " TEXT,"
            + KEY_BNAME + " TEXT,"
            + KEY_ACCHNAME + " TEXT,"
            + KEY_BAL + " REAL"
            + ")";

    //TRANSACTION TABLE
    private static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE " + TABLE_TRANSACTION
            + "("
            + KEY_ACCNO + " TEXT,"
            + KEY_EXPTYPE + " TEXT,"
            + KEY_DATE + " TEXT,"
            + KEY_AMOUNT + " REAL"
            + ")";
    

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ACCOUNT);
        db.execSQL(CREATE_TABLE_TRANSACTION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);

        onCreate(db);
    }

    public void addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ACCNO, account.getAccountNo());
        values.put(KEY_BNAME, account.getBankName());
        values.put(KEY_ACCHNAME, account.getAccountHolderName());
        values.put(KEY_BAL, account.getBalance());

        long newRowId  = db.insert(TABLE_ACCOUNT, null, values);

        db.close();
    }

    public List<String> getAccountNumbersList(){
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> accountList = new ArrayList<String>();

        String selectQuery  = "SELECT "+KEY_ACCNO+" FROM "+ TABLE_ACCOUNT;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                accountList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        return accountList;
    }

    public List<Account> getAccountsList(){
        SQLiteDatabase db = this.getWritableDatabase();
        List<Account> accountList = new ArrayList<Account>();

        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Account account = new Account(null,null,null,0);
                account.setAccountNo(cursor.getString(0));
                account.setBankName(cursor.getString(1));
                account.setAccountHolderName(cursor.getString(2));
                account.setBalance(cursor.getDouble(3));
                accountList.add(account);
            } while (cursor.moveToNext());
        }

        return accountList;
    }

    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        Account account = new Account(null,null,null,0);

        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT + " WHERE " + KEY_ACCNO +"="+'"'+accountNo+'"';
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            account.setAccountNo(cursor.getString(0));
            account.setBankName(cursor.getString(1));
            account.setAccountHolderName(cursor.getString(2));
            account.setBalance(cursor.getDouble(3));
        }else throw new InvalidAccountException("Invalid Account number");

        return account;
    }

    public void removeAccount(String accountNo) throws InvalidAccountException{
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT + " WHERE " + KEY_ACCNO +"="+'"'+accountNo+'"';
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            db.delete(TABLE_ACCOUNT, KEY_ACCNO+" = ?",new String[]{accountNo});
            db.close();
        }else throw new InvalidAccountException("Invalid Account number");
    }

    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException{
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT + " WHERE " + KEY_ACCNO +"="+'"'+accountNo+'"';
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            double currentBalance = cursor.getDouble(3);
            switch (expenseType) {
                case EXPENSE:
                    double newEBalance = currentBalance - amount;
                    ContentValues valueE = new ContentValues();
                    valueE.put(KEY_BAL,newEBalance);
                    db.update(TABLE_ACCOUNT, valueE,KEY_ACCNO + " = ?",new String[] { String.valueOf(accountNo) });
                    break;
                case INCOME:
                    double newIBalance = currentBalance + amount;
                    ContentValues valueI = new ContentValues();
                    valueI.put(KEY_BAL,newIBalance);
                    db.update(TABLE_ACCOUNT, valueI,KEY_ACCNO + " = ?",new String[] { String.valueOf(accountNo) });
                    break;
            }
        }else throw new InvalidAccountException("Invalid Account number");

    }

    public List<Transaction> getAllTransactionLogs(){
        SQLiteDatabase db = this.getWritableDatabase();
        List<Transaction> transList = new ArrayList<Transaction>();

        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                //Default Date to construct a Transaction Object
                String defaultDate = "2021-02-03T00:00:00-0000";
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = dateFormat.parse(defaultDate);
                    Transaction trans = new Transaction(date,null,null,0);
                    trans.setAccountNo(cursor.getString(0));
                    if ( cursor.getString(1) == "EXPENSE"){
                        trans.setExpenseType(ExpenseType.EXPENSE);
                    }else{
                        trans.setExpenseType(ExpenseType.INCOME);
                    }
                    try {
                        trans.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(2)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    trans.setAmount(cursor.getDouble(3));
                    transList.add(trans);
                    ;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        return transList;
    }

    public List<Transaction> getPaginatedTransactionLogs(int limit){
        int i =0;
        SQLiteDatabase db = this.getWritableDatabase();
        List<Transaction> transList = new ArrayList<Transaction>();

        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                //Default Date to construct a Transaction Object
                String defaultDate = "2021-02-03";
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = dateFormat.parse(defaultDate);
                    Transaction trans = new Transaction(date,null,null,0);
                    trans.setAccountNo(cursor.getString(0));
                    if ( cursor.getString(1) == "EXPENSE"){
                        trans.setExpenseType(ExpenseType.EXPENSE);
                    }else{
                        trans.setExpenseType(ExpenseType.INCOME);
                    }
                    try {
                        trans.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(2)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    trans.setAmount(cursor.getDouble(3));
                    transList.add(trans);
                    i++;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext() && i<limit);
        }

        return transList;
    }

    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ACCNO, accountNo);
        if ( expenseType==ExpenseType.EXPENSE){
            values.put(KEY_EXPTYPE, "EXPENSE");
        }else{
            values.put(KEY_EXPTYPE, "INCOME");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        values.put(KEY_DATE, dateFormat.format(date));


        values.put(KEY_AMOUNT, amount);

        long newRowId  = db.insert(TABLE_TRANSACTION, null, values);

        db.close();
    }

}