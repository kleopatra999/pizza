package pizza.holmium;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

import java.io.File;
import java.util.ArrayList;

/**
 * Created on 5/7/16.
 */
class SqlHelper extends SQLiteOpenHelper {
    static final String DATABASE_FILE_NAME = "appsinfo";
    static final String TABLE_NAME = "apps";

    static final String I_ID = "iid";
    static final String APP_NAME = "appname";
    static final String LITERAL_NAME = "literalname";
    static final String LOAD_METHOD = "loadmethod";
    static final String IS_HIDE = "ishide";
    static final String URL = "url";

    static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    BaseColumns._ID + " INTEGER PRIMARY KEY," +
                    I_ID + " TEXT," + /* Installation ID */
                    APP_NAME + " TEXT NOT NULL," +
                    LITERAL_NAME + " TEXT NOT NULL," +
                    LOAD_METHOD + " INTEGER," + /* 0 for normal http load, 1 for ordinary html content */
                    IS_HIDE + " INTEGER," + /* 0 for no-hide, 1 for hide */
                    URL + " TEXT" +
                    ");";

    static final String FIRST_RUN =
            "INSERT INTO " + TABLE_NAME + "("+ BaseColumns._ID +", iid, appname, literalname, loadmethod, ishide)" +
            "VALUE (0, '0', 'appmng', 'AppManager', 1, 1, '');";

    SqlHelper(Context Ctxt){
        super(Ctxt, DATABASE_FILE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase Database) {
        Database.execSQL(SQL_CREATE_TABLE);

        ContentValues FirstRow = new ContentValues();

        FirstRow.put(BaseColumns._ID, 0);
        FirstRow.put(I_ID, "0");
        FirstRow.put(APP_NAME, "appmgr");
        FirstRow.put(LITERAL_NAME, "AppManager");
        FirstRow.put(LOAD_METHOD, 1);
        FirstRow.put(IS_HIDE, 1);
        FirstRow.put(URL, "https://raw.githubusercontent.com/holmium/dnsforwarder/5/StatisticTemplate.html");

        if( Database.insert(TABLE_NAME, null, FirstRow) < 0 ){
            /* TODO: Error handling */
        }
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

    public enum LoadMethods {
        NORMAL_HTTP, ORDINARY_HTML
    }

    public class AppInfo{
        public String InstallationID;
        public String AppName;
        public String LiteralName;
        public String IconPath;
        public LoadMethods LoadMethod;
        public boolean IsHide;
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

    public AppInfo[] GetAppList(String AppName){
        Cursor Query;

        Query = Database.query(SqlHelper.TABLE_NAME, );
        return null;
    }
}