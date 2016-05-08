package pizza.holmium;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class PizzaMain extends AppCompatActivity {

    private void PromptSomething(String text){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(text);
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_main);

        WebView mainWebView = (WebView) findViewById(R.id.mainview);

        try {
            WebSettings webSettings = mainWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            System.out.print(Build.VERSION.RELEASE);

            String User_Agent = "Mozilla/5.0 (Android/" + Build.VERSION.RELEASE + "; Model/" + Build.MODEL +"; Mobile) Webkit Pizza/0.1";
            Log.i(null, "User-Agent:" + User_Agent);
            webSettings.setUserAgentString(User_Agent);
        } catch (Exception e){
            PromptSomething("Cannot initializing.\n" + e.getMessage());
        }
    }
}
