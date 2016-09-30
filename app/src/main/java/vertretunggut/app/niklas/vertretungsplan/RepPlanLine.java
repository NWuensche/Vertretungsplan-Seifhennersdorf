package vertretunggut.app.niklas.vertretungsplan;

import android.widget.RelativeLayout;

/**
 * Created by nwuensche on 30.09.16.
 */
public class RepPlanLine {
    String hour;
    String schoolClass;
    String teacher;
    String room;
    String type;
    String message;
    String subject;

    public RepPlanLine(String hour, String teacher, String subject, String room, String schoolClass, String type, String message) { // TODO Nur line übergeben
        this.hour = hour;
        this.teacher = teacher;
        this.subject = subject;
        this.room = room;
        this.schoolClass = schoolClass;
        this.type = type;
        this.message = message;
    } // TODO Fange auch keine Stunde ab


}
