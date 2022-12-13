package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

public class PersistentDemoExpenseManager extends ExpenseManager{
    private Context context;
    public PersistentDemoExpenseManager(Context context) {
        this.context=context;
        this.setup();
    }

    @Override
    public void setup()  {

        SQLiteDatabase DataBase = context.openOrCreateDatabase("200410K", Context.MODE_PRIVATE, null);
        //creating new 2 databases if not exist for bank account details and transaction details
        String sqlQ1 = "CREATE TABLE IF NOT EXISTS Account(AccountNumber VARCHAR PRIMARY KEY, BankName VARCHAR, AccountHolderName VARCHAR, Balance REAL);";
        String sqlQ2 = "CREATE TABLE IF NOT EXISTS BankTransaction(TransactionID INTEGER PRIMARY KEY Autoincrement, AccountNo VARCHAR, ExpenseType_1_Expense INT, Amount REAL, Date DATE, FOREIGN KEY (AccountNO) REFERENCES Account(AccountNumber));";

        DataBase.execSQL(sqlQ1);
        DataBase.execSQL(sqlQ2);

        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(DataBase);
        setTransactionsDAO(persistentTransactionDAO);

        AccountDAO persistentAccountDAO = new PersistentAccountDAO(DataBase);
        setAccountsDAO(persistentAccountDAO);

    }
}
