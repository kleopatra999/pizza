package pizza.holmium;

import android.os.AsyncTask;
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

            String User_Agent = "Mozilla/5.0 (Android/" + Build.VERSION.RELEASE + "; Model/" + Build.MODEL +"; Mobile) Webkit Pizza/0.1";
            Log.i(null, "User-Agent:" + User_Agent);
            webSettings.setUserAgentString(User_Agent);
        } catch (Exception e){
            PromptSomething("Cannot initializing.\n" + e.getMessage());
        }

        new ContentDownloader(mainWebView).execute("https://raw.githubusercontent.com/holmium/dnsforwarder/5/StatisticTemplate.html");
    }
}
