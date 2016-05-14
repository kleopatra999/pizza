package pizza.holmium;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created on 5/14/16.
 */
public class Utils {
    public static void PromptSomething(String Text, Context Ctxt){
        AlertDialog.Builder alert = new AlertDialog.Builder(Ctxt);
        alert.setMessage(Text);
        alert.setPositiveButton("OK", null);
        alert.show();
    }
}
