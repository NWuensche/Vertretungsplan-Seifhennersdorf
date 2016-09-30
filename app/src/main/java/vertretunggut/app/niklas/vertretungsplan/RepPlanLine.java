package vertretunggut.app.niklas.vertretungsplan;

import android.widget.RelativeLayout;

/**
 * Created by nwuensche on 30.09.16.
 */
public class RepPlanLine {
    private String hour;
    private String teacher;
    private String subject;
    private String room;
    private String schoolClass;
    private String type;
    private String message;

    public RepPlanLine(String hour, String teacher, String subject, String room, String schoolClass, String type, String message) { // TODO Nur line übergeben
        this.hour = hour;
        this.teacher = teacher;
        this.subject = subject;
        this.room = room;
        this.schoolClass = schoolClass;
        this.type = type;
        this.message = message;
    } // TODO Fange auch keine Stunde ab

    public String getHour() {
        return hour;
    }

    public String getSchoolClass() {
        return schoolClass;
    }

    public String getRoom() {
        return room;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getSubject() {
        return subject;
    }

    public boolean isEmpty() {
        return hour.equals("") && teacher.equals("") && subject.equals("") && room.equals("") && schoolClass.equals("") && type.equals("") && message.equals("");
    }
}
