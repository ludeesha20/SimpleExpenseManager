package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private SQLiteDatabase Data_base;

    public PersistentAccountDAO(SQLiteDatabase Data_base){
        this.Data_base=Data_base;
    }


    //method to get all account number list
    @Override
    public  List<String> getAccountNumbersList(){

        String[] projection = {"AccountNumber"};

        Cursor cursor = Data_base.query(
                "Account",   // The table to query
                projection, null, null, null, null, null);

        List Account_IDs = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemId = cursor.getString(cursor.getColumnIndexOrThrow("AccountNumber"));
            Account_IDs.add(itemId);
        }
        cursor.close();
        return Account_IDs;
    }

    @Override
    public List<Account> getAccountsList(){

        String[] projection = {"AccountNumber", "BankName", "Balance", "AccountHolderName"};

        Cursor cursor = Data_base.query(
                "Account",   // The table to query
                projection, null, null, null, null, null
        );

        List Account_IDs = new ArrayList<>();
        while(cursor.moveToNext()) {
            String accNum = cursor.getString(cursor.getColumnIndexOrThrow("AccountNumber"));
            String accHolderName = cursor.getString(cursor.getColumnIndexOrThrow("AccountHolderName"));
            String bankName = cursor.getString(cursor.getColumnIndexOrThrow("BankName"));
            Long balance = cursor.getLong(cursor.getColumnIndexOrThrow("Balance"));
            Account newAcc = new Account(accNum, bankName, accHolderName, balance);
            Account_IDs.add(newAcc);
        }
        cursor.close();
        return Account_IDs;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException{
        String[] projection = {"AccountNumber", "BankName", "Balance", "AccountHolderName"};

        //select the raws where, accountNo=AccountNumber
        String selection = "AccountNumber" + " = ?";
        String[] selectionArgs = { accountNo };


        Cursor cursor = Data_base.query(
                "Account",   // The table to query
                projection, selection, selectionArgs, null, null, null
        );

        Account newAcc=null;
        while(cursor.moveToNext()) {
            String bankName = cursor.getString(cursor.getColumnIndexOrThrow("BankName"));
            String accNum = cursor.getString(cursor.getColumnIndexOrThrow("AccountNumber"));
            Long balance = cursor.getLong(cursor.getColumnIndexOrThrow("Balance"));
            String accHolderName = cursor.getString(cursor.getColumnIndexOrThrow("AccountHolderName"));
            newAcc = new Account(accNum, bankName, accHolderName, balance);
        }
        cursor.close();
        return newAcc;

    }

    @Override
    public void addAccount(Account account){

        // Create new map of values
        ContentValues values = new ContentValues();
        values.put("AccountNumber", account.getAccountNo());
        values.put("BankName", account.getBankName());
        values.put("AccountHolderName", account.getAccountHolderName());
        values.put("Balance", account.getBalance());

        Data_base.insert("Account", null, values);
    }

    public void removeAccount(String accountNo) throws InvalidAccountException{
        String selection = "AccountNumber" + " = ?";
        String[] selectionArgs = { accountNo };
        int deletedRows = Data_base.delete("Account", selection, selectionArgs);
    }

    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException{
        // updating values for the keys
        Account acc=getAccount(accountNo);
        double presentBalance=acc.getBalance();

        if(expenseType==ExpenseType.EXPENSE) {
            //checking that the balance will be a positive value or zero before completing the transaction
            double newBalance = presentBalance - amount;
            if (newBalance >= 0) {
                ContentValues values = new ContentValues();
                values.put("Balance", newBalance);

                String selection = "AccountNumber" + " = ?";
                String[] selectionArgs = {accountNo};

                int count = Data_base.update("Account", values, selection, selectionArgs);
            }
            //else; don't complete the transaction
        }
        else{
            double newBalance = presentBalance + amount;
            ContentValues values = new ContentValues();
            values.put("Balance", newBalance);

            // Which row to update, based on the title
            String selection = "AccountNumber" + " = ?";
            String[] selectionArgs = {accountNo};

            int count = Data_base.update("Account", values, selection, selectionArgs);
        }
    }


}
