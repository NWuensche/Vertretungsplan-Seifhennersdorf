package vertretunggut.app.niklas.vertretungsplan;

import android.widget.Toast;

/**
 * Created by nwuensche on 26.09.16.
 */
public class NothingThereToast implements DialogBuilder {
    private MainActivity activity;
    private String text;

    public NothingThereToast(MainActivity activity, String text) {
        this.activity = activity;
        this.text = text;
    }

    @Override
    public void buildDialog() {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }
}
