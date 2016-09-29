package vertretunggut.app.niklas.vertretungsplan;

import android.support.design.widget.FloatingActionButton;
import android.view.View;

/**
 * Created by nwuensche on 26.09.16.
 */
public class RepPlanFrame {
    private MainActivity activity;
    private FloatingActionButton prevDay;
    private FloatingActionButton nextDay;
    private static boolean nextDayButtonLastPressed = false;

    public RepPlanFrame(MainActivity activity) {
        this.activity = activity;
        prevDay = (FloatingActionButton) activity.findViewById(R.id.prev_day_button);
        nextDay = (FloatingActionButton) activity.findViewById(R.id.next_day_button);

        prevDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                previousDayButtonPressed();
            }
        });

        nextDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                nextDayButtonPressed();
            }
        });
    }

    public void enableMoveButtons() {
        nextDay.show();
        prevDay.show();
    }

    public void setTitle(String headerTitle){
        activity.getSupportActionBar().setTitle(headerTitle);
    }

    public void disableLastPressedButton() {
        if (nextDayButtonLastPressed()) {
            nextDay.hide();
        } else {
            prevDay.hide();
        }
    }

    public boolean nextDayButtonLastPressed() {
        return nextDayButtonLastPressed;
    }

    public void previousDayButtonPressed() {
        activity.decreaseCurrentRepPlanSite();
        nextDayButtonLastPressed = false;

        boolean connected = handleNetworkRight();

        if(connected) {

            activity.restartRepPlanGetter();
        }
    }

    public void nextDayButtonPressed() {

        activity.increaseCurrentRepPlanSite();
        nextDayButtonLastPressed = true;

        boolean connected = handleNetworkRight();

        if(connected){
            activity.restartRepPlanGetter();
        }

    }

    public boolean handleNetworkRight(){
        if(!NoNetworkHandler.isNetworkAvailable(activity)) {
            new NoNetworkHandler(activity).showNoNetworkView();
            return false;
        }
        return true;
    }
}
