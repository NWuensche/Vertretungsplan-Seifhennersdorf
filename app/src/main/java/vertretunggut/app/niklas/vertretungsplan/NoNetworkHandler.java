package vertretunggut.app.niklas.vertretungsplan;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

/**
 * Created by nwuensche on 26.09.16.
 */
public class NoNetworkHandler {
    private MainActivity activity;
    private FloatingActionButton prevDay;
    private FloatingActionButton nextDay;


    public NoNetworkHandler(MainActivity activity) {
        this.activity = activity;
        prevDay = (FloatingActionButton) activity.findViewById(R.id.prev_day_button);
        nextDay = (FloatingActionButton) activity.findViewById(R.id.next_day_button);
    }

    public void showNoNetworkView(){
        activity.findViewById(R.id.noNetworkLayout).setVisibility(View.VISIBLE);

        prevDay.hide();
        nextDay.hide();
        activity.findViewById(R.id.layout_of_reps).setVisibility(View.GONE);
        activity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        activity.findViewById(R.id.noNetworkRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.handleNetworkAndStartGetter();
            }
        });
    }

    public void disableNoNetworkView(){
        activity.findViewById(R.id.noNetworkLayout).setVisibility(View.GONE);

        prevDay.show();
        nextDay.show();
        activity.findViewById(R.id.layout_of_reps).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        activity.restartRepPlanGetter();
    }

    public static boolean isNetworkAvailable(MainActivity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
