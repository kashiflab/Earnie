package pinch.android.earnie.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Patterns;

public class Utils {

    private static ProgressDialog pDialog;

    public static boolean isEmailVerified(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void initpDialog(Context context, String msg) {

        pDialog = new ProgressDialog(context);
        pDialog.setMessage(msg);
        pDialog.setCancelable(false);
    }

    public static void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    public static void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }
}
