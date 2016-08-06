package pizza.holmium;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

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

            if( !TheFile.exists() ){
                return String.valueOf(Utils.ErrorType.NO_SUCH_FILE_OR_DIRECTORY);
            }

            if( !TheFile.canRead() ){
                return String.valueOf(Utils.ErrorType.PERMISSION_DENIED);
            }

            File[] FileList;

            try {
                FileList = TheFile.listFiles();
                if( FileList == null ){
                    return String.valueOf(Utils.ErrorType.INVALID_ARGUMENT);
                }
            } catch (SecurityException e){
                return String.valueOf(Utils.ErrorType.PERMISSION_DENIED);
            }

            String Ret = "\n";

            for( File aFile : FileList ){
                if(aFile.isDirectory()){
                    Ret += (String.valueOf(aFile.lastModified()) + "\r" + "Dir\r" + aFile.getName() + File.separator + "\n");
                } else {
                    Ret += (String.valueOf(aFile.lastModified()) + "\r" + aFile.length() + "\r" + aFile.getName() + "\n");
                }

            }

            return Ret;
        }

        @NonNull
        private String GetFileExtension(String Path){
            int SlashPos = Path.lastIndexOf(File.separatorChar);

            int DotPos = Path.lastIndexOf('.');

            if( DotPos < SlashPos || DotPos < 0 || DotPos == Path.length() - 1 ){
                return "";
            } else {
                return Path.substring(DotPos + 1);
            }
        }

        @JavascriptInterface
        public void ExecuteAFile(String Path, String mime){
            Intent i = new Intent();

            i.setAction(android.content.Intent.ACTION_VIEW);

            String MimeType = mime == null ? MimeTypeMap.getSingleton().getMimeTypeFromExtension(GetFileExtension(Path)) : mime;

            i.setDataAndType(Uri.fromFile(new File(Path)), MimeType == null ? "*/*" : MimeType);
            ThisContext.startActivity(i);
        }

        @JavascriptInterface
        public String ReadTextFile(String Path, String Encoding){
            File f = new File(Path);

            if( f.isDirectory() ){
                return null;
            }

            if( !f.canRead() ){
                return null;
            }

            long l = f.length();

            if( l > (long)Integer.MAX_VALUE ){
                return null;
            }

            byte[] b = new byte[(int)l];

            try{
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(f));
                buf.read(b, 0, b.length);
                buf.close();
            }catch (FileNotFoundException e){
                return null;
            } catch (IOException e){
                return null;
            }

            try {
                return new String(b, Encoding == null ? "UTF-8" : Encoding);
            } catch (UnsupportedEncodingException e){
                return null;
            }
        }

        @JavascriptInterface
        public boolean WriteTextFile(String Path, String Content){
            File f = new File(Path);

            if( f.isDirectory() ){
                return false;
            }

            try {
                OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(f, false));
                out.write(Content, 0, Content.length());
                out.close();
            } catch (FileNotFoundException e){
                return false;
            } catch (IOException e){
                return false;
            }
            return true;
        }
    }

    public void LoadContent(WebView wv){
        ThisWebView = wv;

        ThisWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ThisContext);
                alert.setMessage(message);
                alert.setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                    });
                alert.setCancelable(false);
                alert.show();

                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ThisContext);
                alert.setMessage(message);
                alert.setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        });

                alert.setNegativeButton(android.R.string.cancel,
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        });

                alert.setCancelable(false);
                alert.show();

                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                final EditText TextBox = new EditText(ThisContext);
                AlertDialog.Builder alert = new AlertDialog.Builder(ThisContext);
                TextBox.setText(defaultValue);
                alert.setView(TextBox);
                alert.setMessage(message);
                alert.setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm(TextBox.getText().toString());
                            }
                        });

                alert.setNegativeButton(android.R.string.cancel,
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        });

                alert.setCancelable(false);
                alert.show();

                return true;
            }
        });

        wv.addJavascriptInterface(new ExposedInterface(), "pz");
        ThisApp.LoadContent(wv);
    }

}
