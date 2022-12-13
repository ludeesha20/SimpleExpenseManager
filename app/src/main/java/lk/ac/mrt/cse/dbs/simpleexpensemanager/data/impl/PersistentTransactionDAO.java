package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private SQLiteDatabase DataBase;
    private static int Number=0;
    public PersistentTransactionDAO(SQLiteDatabase DataBase){this.DataBase=DataBase;}
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount){
        PersistentAccountDAO temp= new PersistentAccountDAO(DataBase);
        Double balance=0.0;
        try {
            balance = (temp).getAccount(accountNo).getBalance();
        }
        catch(Exception e){}
        if(balance>=amount) {
            int Number = 0;
            if (expenseType == ExpenseType.EXPENSE) {
                Number = 0;
            }
            else {
                Number = 1;
            }
            ContentValues values = new ContentValues();
            //values.put("TransactionID", Number);
            values.put("AccountNo", accountNo);

            values.put("Date", (new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)));


            values.put("ExpenseType", Number);
            values.put("Amount", amount);

            //insert for the transaction table
            DataBase.insert("BankTransaction", null, values);
        }
    }

    public List<Transaction> getAllTransactionLogs() {
        //reading values from sql database
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * from BankTransaction";
        Cursor cursor = DataBase.rawQuery(query,null);

        while (cursor.moveToNext()==true){
            //4th column for Date
            String temp = (cursor.getString(4));
            Date date= new Date();
            try {
                date = (new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(temp));
            }catch(Exception e){}

            //1st column for AccountNo
            String accountNumber = cursor.getString(1);

            //check for the amount is an expense or income, 2nd column
            ExpenseType expenseType=null;
            if(cursor.getInt(2) == 0){
                expenseType = ExpenseType.EXPENSE;
            }
            else if(cursor.getInt(2)== 1){
                expenseType=ExpenseType.INCOME;
            }
            double amount = cursor.getDouble(3);
            Transaction transaction = new Transaction(date,accountNumber,expenseType,amount);
            transactions.add(transaction);
        }
        cursor.close();
        return transactions;

    }




    //show last few(log_limit) transaction logs
    public List<Transaction> getPaginatedTransactionLogs(int log_limit){
        List temp_transaction = this.getAllTransactionLogs();
        int size = temp_transaction.size();
        if (size <= log_limit) {
            return temp_transaction;
        }
        return temp_transaction.subList(size - log_limit, size);

    }

}
