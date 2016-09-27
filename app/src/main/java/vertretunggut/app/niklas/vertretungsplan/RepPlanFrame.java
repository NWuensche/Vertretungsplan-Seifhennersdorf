package vertretunggut.app.niklas.vertretungsplan;

import android.support.v7.view.menu.ActionMenuItemView;

/**
 * Created by nwuensche on 26.09.16.
 */
public class RepPlanFrame {
    private MainActivity activity;
    private ActionMenuItemView prevDayButton;
    private ActionMenuItemView nextDayButton;
    private static RepPlanFrame singleton = null;
    private boolean nextDayButtonLastPressed;

    public RepPlanFrame(MainActivity activity) {
        this.activity = activity;
        prevDayButton = (ActionMenuItemView) activity.findViewById(R.id.vorheriger_tag);
        nextDayButton = (ActionMenuItemView) activity.findViewById(R.id.nächster_Tag);
        nextDayButtonLastPressed = false;
    }

    public void setUpFrame(String headerTitle) {
        ActionMenuItemView head = (ActionMenuItemView) activity.findViewById(R.id.Tag);
        head.setTitle(headerTitle);

        nextDayButton.setEnabled(true);
        prevDayButton.setEnabled(true);
    }

    public void disableLastPressedButton() {
        if (activity.nextDayButtonLastPressed()) {
            nextDayButton.setEnabled(false);
        } else {
            prevDayButton.setEnabled(false);
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
