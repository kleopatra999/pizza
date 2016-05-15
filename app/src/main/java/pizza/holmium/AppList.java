package pizza.holmium;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.BaseColumns;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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
        FirstRow.put(URL, "https://raw.githubusercontent.com/holmium/test/master/1.html");

        if( Database.insert(TABLE_NAME, null, FirstRow) < 0 ){
            /* TODO: Error handling */
        }

        ContentValues SecRow = new ContentValues();

        SecRow.put(I_ID, "1");
        SecRow.put(APP_NAME, "appmgr2");
        SecRow.put(LITERAL_NAME, "AppManager2");
        SecRow.put(LOAD_METHOD, 1);
        SecRow.put(IS_HIDE, 1);
        SecRow.put(URL, "https://raw.githubusercontent.com/holmium/dnsforwarder/5/StatisticTemplate.html");

        if( Database.insert(TABLE_NAME, null, SecRow) < 0 ){
            /* TODO: Error handling */
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase Database,int OldVersion,int NewVersion){
        Database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(Database);
    }
}

class ContentDownloader extends AsyncTask<String, Void, String> {
    protected final WebView wv;

    ContentDownloader(WebView uiwv){
        wv = uiwv;
    }

    protected String doInBackground(String... Urls){ /* Actually only the first arg will be used. */
        String Result = "";
        try {
            /* TODO: Time Coutrol */
            URL url = new URL(Urls[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Result += line;
            }
            reader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result;
    }

    protected void onPostExecute(String Result) {
        wv.loadData(Result,"text/html", null);
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
        public String Url;

        public void LoadContent(WebView wv){
            File CacheDir = new File(ItnStrg, "appcache" + File.separator + AppName);
            CacheDir.mkdirs();
            wv.getSettings().setAppCachePath(CacheDir.getPath());
            if(LoadMethod == LoadMethods.NORMAL_HTTP){
                //TODO:Implement this
            } else {
                new ContentDownloader(wv).execute(Url);
            }
        }
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

    public AppInfo[] GetAppsInfo(String... AppNames){
        ArrayList<AppInfo> AppList = null;
        Cursor Query;
        String[] QueryKeys = {
                SqlHelper.I_ID,
                SqlHelper.APP_NAME,
                SqlHelper.LITERAL_NAME,
                SqlHelper.LOAD_METHOD,
                SqlHelper.IS_HIDE,
                SqlHelper.URL
        };
        String WhereClause = AppNames == null ? null : SqlHelper.APP_NAME + " = ?";

        Query = Database.query(SqlHelper.TABLE_NAME, QueryKeys, WhereClause, AppNames, null, null, null, null);

        if( Query != null && Query.moveToFirst() ){
            AppList = new ArrayList<AppInfo>();
            AppInfo Itr = null;

            do{
                Itr = new AppInfo();
                Itr.InstallationID = Query.getString(0);
                Itr.AppName = Query.getString(1);
                Itr.LiteralName = Query.getString(2);
                Itr.LoadMethod = Query.getInt(3) == 0 ? LoadMethods.NORMAL_HTTP : LoadMethods.ORDINARY_HTML;
                Itr.IsHide = Query.getInt(4) == 0 ? false : true;
                Itr.Url = Query.getString(5);

                AppList.add(Itr);
            } while(Query.moveToNext());

        }
        Query.close();
        Object a[] = AppList.toArray(new AppInfo[0]);
        return AppList == null ? null : AppList.toArray(new AppInfo[0]);
    }
}