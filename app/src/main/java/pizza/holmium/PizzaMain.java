package pizza.holmium;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class PizzaMain extends AppCompatActivity {
    protected PizzaInterface pzi = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_main);

        WebView mainWebView = (WebView) findViewById(R.id.mainview);

        try {
            WebSettings webSettings = mainWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setAppCacheEnabled(true);

            String User_Agent = "Mozilla/5.0 (Android/" + Build.VERSION.RELEASE + "; Model/" + Build.MODEL +"; Mobile) Webkit Pizza/0.1";
            Log.i(null, "User-Agent:" + User_Agent);
            webSettings.setUserAgentString(User_Agent);
        } catch (Exception e){
            Utils.PromptSomething("Cannot initializing.\n" + e.getMessage(), this);
        }

        Intent intent = getIntent();
        String AppName = intent.getStringExtra("AppName");

        Utils.PromptSomething(Integer.toString(getTaskId()), this);

        pzi = new PizzaInterface(AppName == null ? "appmgr" : AppName, this, intent.getIntExtra("LaunchCount", 0));
        pzi.LoadContent(mainWebView);
    }
}
