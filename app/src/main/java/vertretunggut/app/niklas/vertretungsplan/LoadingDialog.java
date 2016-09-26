package vertretunggut.app.niklas.vertretungsplan;

import android.app.ProgressDialog;

/**
 * Created by nwuensche on 26.09.16.
 */
public class LoadingDialog {
    MainActivity activity;
    ProgressDialog loadingDialog;

    public LoadingDialog(MainActivity activity){
        this.activity = activity;
    }

    public ProgressDialog buildDialog(){
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setTitle("Vertretungsplan");
        loadingDialog.setMessage("Laden...");
        loadingDialog.setIndeterminate(false);
        loadingDialog.show();
        return loadingDialog;
    }

    public void close(){
        loadingDialog.dismiss();
    }
}
