package vertretunggut.app.niklas.vertretungsplan;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.view.menu.ActionMenuItemView;
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
                // Click action
                previousDayButtonPressed();
            }
        });

        nextDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Click action
                nextDayButtonPressed();
            }
        });
    }

    public void enableAllButtons() {
        nextDay.setEnabled(true);
        prevDay.setEnabled(true);
    }

    public void setTitle(String headerTitle){
        /*ActionMenuItemView head = (ActionMenuItemView) activity.findViewById(R.id.Tag);
        head.setTitle(headerTitle);*/
        activity.getSupportActionBar().setTitle(headerTitle);
    }

    public void disableLastPressedButton() {
        if (nextDayButtonLastPressed()) {
            nextDay.setEnabled(false);
        } else {
            prevDay.setEnabled(false);
        }
    }

    public boolean nextDayButtonLastPressed() {
        return nextDayButtonLastPressed;
    }

    public void previousDayButtonPressed() {
        activity.decreaseCurrentRepPlanSite();
        nextDayButtonLastPressed = false;
        activity.restartRepPlanGetter();
    }

    public void nextDayButtonPressed() {
        activity.increaseCurrentRepPlanSite();
        nextDayButtonLastPressed = true;
        activity.restartRepPlanGetter();
    }
}
