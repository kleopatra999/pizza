package pizza.holmium;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.BaseColumns;

import java.io.File;
import java.util.ArrayList;

/**
 * Created on 5/7/16.
 */
class SqlHelper extends SQLiteOpenHelper {
    static final String DATABASE_FILE_NAME = "appsinfo";
    static final String TABLE_NAME = "apps";
    static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    BaseColumns._ID + " INTEGER PRIMARY KEY," +
                    "iid INTEGER," +
                    "appname TEXT NOT NULL" +
                    ");";

    SqlHelper(Context Ctxt){
        super(Ctxt, DATABASE_FILE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase Database) {
        Database.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase Database,int OldVersion,int NewVersion){
        Database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(Database);
    }
}

public class AppList{
    public final String ItnStrgString;
    public final File ItnStrg;

    public final String ExtnStrgString;
    public final File ExtnStrg;

    private SqlHelper SqlHpl;
    private SQLiteDatabase Database;

    public class AppInfo{
        public String Name;
        public String IconPath;
        public Boolean Hide;
    }

    AppList(Context Ctxt){
        ItnStrg = Ctxt.getFilesDir();
        ItnStrgString = ItnStrg.getPath();

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            ExtnStrg = Ctxt.getExternalFilesDir(null);
            ExtnStrgString = ExtnStrg.getPath();
        } else {
            ExtnStrg = null;
            ExtnStrgString = null;
        }

        SqlHpl = new SqlHelper(Ctxt);
        Database = SqlHpl.getWritableDatabase();

    }

    public AppInfo[] GetAllApps(){
        ArrayList ai = new ArrayList();

        return null;
    }
}