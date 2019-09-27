package com.hamidelmamoun.to_do.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ToDoDBHelper.db";
    public static final String CONTACTS_TABLE_NAME = "todo";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE "+CONTACTS_TABLE_NAME +
                        "(id INTEGER PRIMARY KEY, task TEXT, dateStr INTEGER, comment TEXT, priority TEXT, finished INTEGER, account TEXT, accountType INTEGER)"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+CONTACTS_TABLE_NAME);
        onCreate(db);
    }



    private long getDate(String day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        try {
            date = dateFormat.parse(day);
        } catch (ParseException e) {}
        return date.getTime();
    }


    /**
     *
     * @param task
     * @param dateStr
     * @param priority
     * @param comment
     * @param accountType 0=No 1=Facebook 2=Firebase
     * @return
     */
    public boolean insertContact  (String task, String dateStr, String priority, String comment, String accountId, int accountType)
    {
        Date date;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task", task);
        contentValues.put("dateStr", getDate(dateStr));
        contentValues.put("priority", priority);
        contentValues.put("comment", comment);
        contentValues.put("finished", 0);
        contentValues.put("account",accountId);
        contentValues.put("accountType",accountType);
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateContact (String id, String task, String dateStr, String priority, String comment, String accountId, int accountType)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("task", task);
        contentValues.put("dateStr", getDate(dateStr));
        contentValues.put("priority", priority);
        contentValues.put("comment", comment);
        contentValues.put("finished", 0);
        contentValues.put("account",accountId);
        contentValues.put("accountType",accountType);

        db.update(CONTACTS_TABLE_NAME, contentValues, "id = ? ", new String[] { id } );
        return true;
    }

    public boolean setTaskFinished(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("finished", 1);

        db.update(CONTACTS_TABLE_NAME, contentValues, "id = ? ", new String[] { id } );
        return true;
    }

    public boolean deleteTask(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CONTACTS_TABLE_NAME,  "id = ? ", new String[]{id}) > 0;
    }

    public Cursor getData(Context context){
        String accountId = SPHelper.getAccountId(context);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from "+CONTACTS_TABLE_NAME+" WHERE account = ? order by id desc", new String[]{accountId});
        return res;

    }

    public Cursor getDataSpecific(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from "+CONTACTS_TABLE_NAME+" WHERE id = '"+id+"' order by id desc", null);
        return res;

    }



    public Cursor getDataFinished(Context context){
        String accountId = SPHelper.getAccountId(context);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from "+CONTACTS_TABLE_NAME+
                " WHERE  finished =  ? AND account = ? order by id desc", new String[]{"1", accountId});
        return res;

    }

    public Cursor getOverdueData(Context context){
        String accountId = SPHelper.getAccountId(context);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from "+CONTACTS_TABLE_NAME+
                " WHERE date(datetime(dateStr / 1000 , 'unixepoch', 'localtime')) < date('now', 'localtime') AND finished = ? AND account = ? order by id desc", new String[]{"0", accountId});
        return res;

    }


    public Cursor getDataToday(Context context){
        String accountId = SPHelper.getAccountId(context);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from "+CONTACTS_TABLE_NAME+
                " WHERE date(datetime(dateStr / 1000 , 'unixepoch', 'localtime')) = date('now', 'localtime') AND finished =  ? AND account = ? order by id desc", new String[]{"0", accountId});
        return res;

    }


    public Cursor getDataTomorrow(Context context){
        String accountId = SPHelper.getAccountId(context);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from "+CONTACTS_TABLE_NAME+
                " WHERE date(datetime(dateStr / 1000 , 'unixepoch', 'localtime')) = date('now', '+1 day', 'localtime') AND finished = ? AND account = ? order by id desc", new String[]{"0", accountId});
        return res;

    }


    public Cursor getDataUpcoming(Context context){
        String accountId = SPHelper.getAccountId(context);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from "+CONTACTS_TABLE_NAME+
                " WHERE date(datetime(dateStr / 1000 , 'unixepoch', 'localtime')) > date('now', '+1 day', 'localtime') AND finished = ? AND account = ? order by id desc", new String[]{"0", accountId});
        return res;
    }



}