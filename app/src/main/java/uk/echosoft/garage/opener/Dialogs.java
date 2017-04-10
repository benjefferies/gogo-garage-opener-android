package uk.echosoft.garage.opener;

import android.app.Activity;
import android.content.*;
import android.support.v7.app.AlertDialog;

class Dialogs {

    static AlertDialog unauthenticated(final Activity activity) {
        return new AlertDialog.Builder(activity)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .setTitle("Unauthenticated")
                .setMessage("You will need to log in")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    static AlertDialog oneTimePin(final Activity activity, final String oneTimePin) {
        return new AlertDialog.Builder(activity)
                .setNeutralButton(uk.echosoft.garage.opener.R.string.button_copy, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ClipData clip = ClipData.newPlainText("One time pin", oneTimePin);
                        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(clip);
                    }
                })
                .setTitle("One Time Pin")
                .setMessage(oneTimePin)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    static AlertDialog signOut(final Activity activity) {
        return new AlertDialog.Builder(activity)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences authentication = activity.getSharedPreferences("authentication", 0);
                        authentication.edit().clear().apply();
                        activity.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setTitle("Sign Out")
                .setMessage("Confirm you want to sign out?")
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }


}
