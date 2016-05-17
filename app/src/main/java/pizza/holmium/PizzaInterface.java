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
    public static final int MAX_CIRCUIT = 10;
    public final String Name;

    protected int LaunchCount;
    protected Context ThisContext = null;
    protected AppList ThisPackage = null;
    protected AppList.AppInfo ThisApp = null;

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
        @JavascriptInterface
        public String GetThisAppName(){
            return Name;
        }

        @JavascriptInterface
        public void Echo(String Text){
            Utils.PromptSomething(Text, ThisContext);
        }

        private class ExposedAppInfo{
            public String InstallationID;
            public String AppName;
            public String LiteralName;
            public String IconPath;
            public boolean IsHide;
            public String Url;
        }

        private ExposedAppInfo[] ToExposedAppInfo(AppList.AppInfo[] Source){
            ExposedAppInfo[] Dest = new ExposedAppInfo[Source.length];

            for(int i = 0; i < Source.length; ++i){
                Dest[i].InstallationID = Source[i].InstallationID;
                Dest[i].AppName = Source[i].AppName;
                Dest[i].LiteralName = Source[i].LiteralName;
                Dest[i].IconPath = Source[i].IconPath;
                Dest[i].IsHide = Source[i].IsHide;
                Dest[i].Url = Source[i].Url;
            }

            return Dest;
        }

        @JavascriptInterface
        public ExposedAppInfo[] GetAppsInfo(String... name){
            return ToExposedAppInfo(ThisPackage.GetAppsInfo(name));
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
