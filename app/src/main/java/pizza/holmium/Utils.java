package pizza.holmium;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created on 5/14/16.
 */
public class Utils {
    public static class ErrorType {
        static final int INVALID_ARGUMENT = 22;
        static final int PERMISSION_DENIED = 13;
        static final int NO_SUCH_FILE_OR_DIRECTORY = 2;
    }

    public static void PromptSomething(String Text, Context Ctxt){
        AlertDialog.Builder alert = new AlertDialog.Builder(Ctxt);
        alert.setMessage(Text);
        alert.setPositiveButton("OK", null);
        alert.show();
    }
}
