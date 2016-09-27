package vertretunggut.app.niklas.vertretungsplan;

import android.app.ProgressDialog;

/**
 * Created by nwuensche on 26.09.16.
 */
public class LoadingDialog implements DialogBuilder{
    MainActivity activity;
    ProgressDialog loadingDialog;

    public LoadingDialog(MainActivity activity) {
        this.activity = activity;
    }

    public void buildDialog() {
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setTitle("Vertretungsplan");
        loadingDialog.setMessage("Laden...");
        loadingDialog.setIndeterminate(false);
        loadingDialog.show();
    }

    public void close() {
        loadingDialog.dismiss();
    }
}
