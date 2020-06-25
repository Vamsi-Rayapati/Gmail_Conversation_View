package com.example.gmmail;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    public  static final String DB_NAME="Gdata.db";
    public  static final String T_NAME="Mail";
    public  static final String C1="mid";
    public  static final String C2="mfrom";
    public  static final String C3="mfromname";
    public  static final String C4="mto";
    public  static final String C5="mtoname";
    public  static final String C6="subject";
    public  static final String C7="snippet";
    public  static final String C8="timestamp";
    public  static final String C9="cname";
    public  static final String C10="cmail";
    public static  final String C11="pos";
    public  static final String C12="mgroup";

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+T_NAME+" (mid  TEXT PRIMARY KEY,mfrom TEXT,mfromname TEXT,mto TEXT,mtoname TEXT,subject TEXT,snippet TEXT,timestamp TEXT,cname TEXT,cmail TEXT,pos TEXT,mgroup TEXT)");
        db.execSQL("create table Labels (mid TEXT,label TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean insertData(String mid,String mfrom,String mfromname,String mto,String mtoname,String subject,String snippet,String date,String name,String mail,String pos,String mgroup)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(C1,mid);
        contentValues.put(C2,mfrom);
        contentValues.put(C3,mfromname);
        contentValues.put(C4,mto);
        contentValues.put(C5,mtoname);
        contentValues.put(C6,subject);
        contentValues.put(C7,snippet);
        contentValues.put(C8,date);
        contentValues.put(C9,name);
        contentValues.put(C10,mail);
        contentValues.put(C11,pos);
        contentValues.put(C12,mgroup);
        long res=db.insert(T_NAME,null,contentValues);
        if(res==-1)
        {
            return  false;
        }
        else
        {
            return true;
        }
    }
    public Cursor getContacts(Context context)
    {
        SharedPreferences sharedPrefs = context.getSharedPreferences("GMmaild", 0);
        String me=sharedPrefs.getString("gmid","me");
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("select distinct cmail from "+T_NAME+" where length(cmail)>1",null);
        return  res;
    }
    public Cursor getName(Context context,String fid)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("select distinct cname,snippet from "+T_NAME+" where cmail='"+fid+"' LIMIT 1",null);
        return  res;
    }
    public Cursor getChat(Context context,String mailid)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("select subject,snippet,timestamp,pos,mid from "+T_NAME+" where cmail='"+mailid+"'",null);
        return  res;
    }
    public boolean insertLabels(String mid,String Label)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("mid",mid);
        contentValues.put("label",Label);
        long res=db.insert("Labels",null,contentValues);
        if(res==-1)
        {
            return  false;
        }
        else
        {
            return true;
        }

    }
    public Cursor getMails(String lbl)
    {
        Cursor res;
        SQLiteDatabase db=this.getWritableDatabase();
        if(lbl.equals("ALL MAILS"))
        {
            res=db.rawQuery("select Mail.mid,cmail,cname,subject,snippet,timestamp from Mail LEFT JOIN Labels where Mail.mid=Labels.mid",null);
        }
        else
        {
            res=db.rawQuery("select Mail.mid,cmail,cname,subject,snippet,timestamp from Mail LEFT JOIN Labels where Mail.mid=Labels.mid and Labels.label='"+lbl+"'",null);
        }
        return  res;
    }

}
