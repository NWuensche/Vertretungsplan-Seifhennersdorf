package vertretunggut.app.niklas.vertretungsplan;

import android.support.v7.view.menu.ActionMenuItemView;

/**
 * Created by nwuensche on 26.09.16.
 */
public class RepPlanFrame {
    private MainActivity activity;
    private ActionMenuItemView prevDayButton;
    private ActionMenuItemView nextDayButton;

    public RepPlanFrame(MainActivity activity){
        this.activity = activity;
        prevDayButton = (ActionMenuItemView) activity.findViewById(R.id.vorheriger_tag);
        nextDayButton = (ActionMenuItemView) activity.findViewById(R.id.nächster_Tag);
    }

    public void setUpFrame(String headerTitle){
        ActionMenuItemView head = (ActionMenuItemView) activity.findViewById(R.id.Tag);
        head.setTitle(headerTitle);

        nextDayButton.setEnabled(true);
        prevDayButton.setEnabled(true);
    }

    //TODO richtig rum?
    public void disableLastPressedButton(){
        if (activity.nextDayButtonLastPressed()) {
            prevDayButton.setEnabled(false);
        } else {
            nextDayButton.setEnabled(false);
        }
    }

}
