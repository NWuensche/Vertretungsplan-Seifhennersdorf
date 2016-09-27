package vertretunggut.app.niklas.vertretungsplan;

import android.util.Log;

import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by nwuensche on 22.09.16.
 */
public enum DayOfWeek {
    TODAY(0),
    MONDAY (1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    WEEKEND(6),
    ERROR(7);

    private final int dayOfWeek;

    DayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public static DayOfWeek getDayOfWeekOfRepPlan(RepPlanDocumentDecorator repPlan) {
        String Tag = repPlan.getTableTitle();
        StringTokenizer DatumMonat = new StringTokenizer(Tag);

        String DatumVertrungsplan = DatumMonat.nextToken();
        Log.e("tag", Tag);
        Log.e("testVer", DatumVertrungsplan);

        return getParsedWeekday(DatumVertrungsplan);
    }

    private static DayOfWeek getParsedWeekday(String Tag) {
        switch(Tag){
            case"Heute":
                return DayOfWeek.TODAY;
            case "Montag":
                return DayOfWeek.MONDAY;
            case "Dienstag":
                return DayOfWeek.TUESDAY;
            case "Mittwoch":
                return DayOfWeek.WEDNESDAY;
            case "Donnerstag":
                return DayOfWeek.THURSDAY;
            case "Freitag":
                return DayOfWeek.FRIDAY;
            case "Samstag":
                return DayOfWeek.WEEKEND;
            case "Sonntag":
                return DayOfWeek.WEEKEND;
            default:
                Log.e("Error at getDayOfWeek","d");
                return DayOfWeek.ERROR;
                // TODO Exception
        }
    }

    public static DayOfWeek getTodaysDayOfWeek() {
        Calendar sCalendar = Calendar.getInstance();
        String Wochentag = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        return getParsedWeekday(Wochentag);
    }

    public int getDifferenceTo(DayOfWeek compare) {
        return dayOfWeek-compare.getDayOfWeek();
    }
}
