package pizza.holmium;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * Created on 5/7/16.
 */
public class PizzaInterface {
    public final String Name;

    protected int LaunchCount;
    protected Context ThisContext = null;
    protected AppList ThisPackage = null;
    protected AppList.AppInfo ThisApp = null;

    PizzaInterface(String AppName, Context Ctxt, int Count) throws IllegalArgumentException{
        Name = AppName;
        ThisPackage = new AppList(Ctxt);
        ThisContext = Ctxt;
        LaunchCount = Count;
        AppList.AppInfo[] AppInfos = ThisPackage.GetAppsInfo(Name);
        if( AppInfos != null ){
            ThisApp = AppInfos[0];
        } else {
            throw new IllegalArgumentException();
        }
    }

    private class ExposedInterface{
        @JavascriptInterface
        public String GetThisAppName(){
            return Name;
        }

        @JavascriptInterface
        public void Echo(String Text){
            Utils.PromptSomething(Text, ThisContext);
        }

        @JavascriptInterface
        public AppList.AppInfo[] GetAppsInfo(String... name){
            return ThisPackage.GetAppsInfo(name);
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

    }

    public void LoadContent(WebView wv){
        wv.addJavascriptInterface(new ExposedInterface(), "pz");
        ThisApp.LoadContent(wv);
    }


}
