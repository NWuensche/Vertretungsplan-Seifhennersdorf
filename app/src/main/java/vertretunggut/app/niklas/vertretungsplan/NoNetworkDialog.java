package vertretunggut.app.niklas.vertretungsplan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

/**
 * Created by nwuensche on 26.09.16.
 */
public class NoNetworkDialog implements DialogBuilder{
    public MainActivity activity;

    public NoNetworkDialog(MainActivity activity) {
        this.activity = activity;
    }


    public void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View rootView = activity.getLayoutInflater().inflate(R.layout.internetfehler, null);
        builder.setView(rootView)
                .setPositiveButton("Erneut versuchen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.handleNetworkAndStartGetter();
                    }
                })
                .setNegativeButton("Beenden", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.exit(0);
                    }
                })
                .create()
                .show();
    }

    public static boolean isNetworkAvailable(MainActivity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
