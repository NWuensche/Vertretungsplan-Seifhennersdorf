package vertretunggut.app.niklas.vertretungsplan;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by nwuensche on 26.09.16.
 */
public class LoadingDialog implements DialogBuilder{
    MainActivity activity;

    public LoadingDialog(MainActivity activity) {
        this.activity = activity;
    }

    public void buildDialog() {
        ProgressBar loadingCircle = (ProgressBar) activity.findViewById(R.id.progress_bar);
        int color = Color.parseColor("#ADD8E6");

        loadingCircle.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        activity.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.layout_of_reps).setVisibility(View.GONE);
    }

    public void close() {
        activity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        activity.findViewById(R.id.layout_of_reps).setVisibility(View.VISIBLE);

    }
}
