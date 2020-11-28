package club.codeexpert.musica.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/*
public class DbAdapter {
    static DbAdapter instance;
    DbHelper helper;

    public DbAdapter(Context context) {
        helper = new DbHelper(context);
    }

    public static synchronized DbAdapter getInstance(Context context) {
        if (null == instance)
            instance = new DbAdapter(context);
        return instance;
    }

    public static synchronized DbAdapter getInstance() {
        if (null == instance) {
            throw new IllegalStateException(DbAdapter.class.getSimpleName() + " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    public long insert(MusicItem item)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.ID, item.id);
        contentValues.put(DbHelper.TITLE, item.title);
        contentValues.put(DbHelper.LINK, item.link);
        contentValues.put(DbHelper.DURATION, item.duration);
        contentValues.put(DbHelper.VIEWS, item.views);
        contentValues.put(DbHelper.THUMBNAIL, item.thumbnail);

        long id = db.insert(DbHelper.TABLE_NAME, null , contentValues);

        return id;
    }

    public ArrayList<MusicItem> getSongs()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = { DbHelper.ID, DbHelper.TITLE, DbHelper.LINK, DbHelper.DURATION, DbHelper.VIEWS, DbHelper.THUMBNAIL };
        Cursor cursor = db.query(DbHelper.TABLE_NAME,columns,null,null,null,null,null);
        ArrayList<MusicItem> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(DbHelper.ID));
            String title = cursor.getString(cursor.getColumnIndex(DbHelper.TITLE));
            String link = cursor.getString(cursor.getColumnIndex(DbHelper.LINK));
            String duration = cursor.getString(cursor.getColumnIndex(DbHelper.DURATION));
            String views = cursor.getString(cursor.getColumnIndex(DbHelper.VIEWS));
            String thumbnail = cursor.getString(cursor.getColumnIndex(DbHelper.THUMBNAIL));

            list.add(new MusicItem(id, link, title, duration, views, thumbnail, true));
        }

        return list;
    }

    public ArrayList<String> getSongsIds() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DbHelper.ID};
        Cursor cursor = db.query( DbHelper.TABLE_NAME,columns,null,null,null,null,null);
        ArrayList<String> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(DbHelper.ID));
            list.add(id);
        }
        return list;
    }

    public  int delete(String id)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] whereArgs = { id };
        int count = db.delete(DbHelper.TABLE_NAME ,DbHelper.ID + " = ?", whereArgs);
        return  count;
    }

    static class DbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "songs";
        private static final String TABLE_NAME = "songs";
        private static final int DATABASE_Version = 1;
        private static final String ID = "_id";
        private static final String TITLE = "title";
        private static final String LINK = "link";
        private static final String DURATION = "duration";
        private static final String VIEWS = "views";
        private static final String THUMBNAIL = "thumbnail";

        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+ID+" VARCHAR(50), "+TITLE+" VARCHAR(255) ,"+LINK+" VARCHAR(255),"+DURATION+" VARCHAR(255),"+VIEWS+" VARCHAR(255),"+THUMBNAIL+" VARCHAR(255));";

        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
        }

        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

 */