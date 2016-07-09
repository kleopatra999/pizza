package pizza.holmium;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created on 5/7/16.
 */
public class PizzaInterface {
    public static final int MAX_CIRCUIT = 10;
    public final String Name;

    protected int LaunchCount;
    protected Context ThisContext = null;
    protected AppList ThisPackage = null;
    protected AppList.AppInfo ThisApp = null;

    protected WebView ThisWebView = null;

    PizzaInterface(String AppName, Context Ctxt, int Count) throws RuntimeException, IllegalArgumentException{
        if( Count > MAX_CIRCUIT ){
            throw new RuntimeException();
        }

        ThisPackage = new AppList(Ctxt);
        AppList.AppInfo[] AppInfos = ThisPackage.GetAppsInfo(AppName);
        if( AppInfos != null ){
            ThisApp = AppInfos[0];
        } else {
            throw new IllegalArgumentException();
        }

        Name = AppName;
        ThisContext = Ctxt;
        LaunchCount = Count;
    }

    private class ExposedInterface{
        private AppList.AppInfo[] AllAppInfos = null;

        @JavascriptInterface
        public void Echo(String Text){
            Utils.PromptSomething(Text, ThisContext);
        }

        /* App Infos % Manipulations */

        @JavascriptInterface
        public String GetThisAppName(){
            return Name;
        }

        @JavascriptInterface
        public int GetAppCount(){
            if( AllAppInfos == null ){
                AllAppInfos = ThisPackage.GetAppsInfo();
            }

            return AllAppInfos.length;
        }

        @JavascriptInterface
        public String GetAppName(int Index){
            if( AllAppInfos == null ){
                AllAppInfos = ThisPackage.GetAppsInfo();
            }

            if( Index > AllAppInfos.length ){
                return null;
            } else {
                return AllAppInfos[Index].AppName;
            }
        }

        @JavascriptInterface
        public String GetAppLiteralname(int Index){
            if( AllAppInfos == null ){
                AllAppInfos = ThisPackage.GetAppsInfo();
            }

            if( Index > AllAppInfos.length ){
                return null;
            } else {
                return AllAppInfos[Index].LiteralName;
            }
        }

        @JavascriptInterface
        public String GetAppUrl(int Index){
            if( AllAppInfos == null ){
                AllAppInfos = ThisPackage.GetAppsInfo();
            }

            if( Index > AllAppInfos.length ){
                return null;
            } else {
                return AllAppInfos[Index].Url;
            }
        }

        @JavascriptInterface
        public boolean GetAppIsHide(int Index){
            if( AllAppInfos == null ){
                AllAppInfos = ThisPackage.GetAppsInfo();
            }

            if( Index > AllAppInfos.length ){
                return true;
            } else {
                return AllAppInfos[Index].IsHide;
            }
        }

        @JavascriptInterface
        public void RunApp(String AppName){
            Intent intent = new Intent(ThisContext, PizzaMain.class);

            intent.putExtra("AppName", AppName);
            if( AppName.equals(Name) ){
                intent.putExtra("LaunchCount", LaunchCount + 1);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            ThisContext.startActivity(intent);
        }

        /* File Handlings */
        @JavascriptInterface
        public String EnumerateDir(String Parent, String Child){
            File TheFile;

            try {
                TheFile = new File(Parent, Child);
            } catch (NullPointerException e){
                return String.valueOf(Utils.ErrorType.INVALID_ARGUMENT);
            }

            File[] FileList;

            try {
                FileList = TheFile.listFiles();
                if( FileList == null ){
                    return String.valueOf(Utils.ErrorType.PERMISSION_DENIED);
                }
            } catch (SecurityException e){
                return String.valueOf(Utils.ErrorType.PERMISSION_DENIED);
            }

            String Ret = "\n";

            for( File aFile : FileList ){
                if(aFile.isDirectory()){
                    Ret += (aFile.getName() + File.separator + "\n");
                } else {
                    Ret += (aFile.getName() + "\n");
                }
            }

            return Ret;
        }

    }

    public void LoadContent(WebView wv){
        ThisWebView = wv;
        wv.addJavascriptInterface(new ExposedInterface(), "pz");
        ThisApp.LoadContent(wv);
    }

}
