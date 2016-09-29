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
public class NoNetworkHandler {
    private MainActivity activity;

    public NoNetworkHandler(MainActivity activity) {
        this.activity = activity;
    }

    public void showNoNetworkView(){
        activity.findViewById(R.id.noNetworkLayout).setVisibility(View.VISIBLE);

        activity.findViewById(R.id.next_day_button).setVisibility(View.GONE);
        activity.findViewById(R.id.prev_day_button).setVisibility(View.GONE);
        activity.findViewById(R.id.list_of_reps).setVisibility(View.GONE);
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

        activity.findViewById(R.id.next_day_button).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.prev_day_button).setVisibility(View.VISIBLE); // TODO was wenn kein tag aber internet weg, und knopf nicht da sein sollte
        activity.findViewById(R.id.list_of_reps).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        activity.restartRepPlanGetter();
    }

    public static boolean isNetworkAvailable(MainActivity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    //TODO Umschalten der Views auch rein
}
