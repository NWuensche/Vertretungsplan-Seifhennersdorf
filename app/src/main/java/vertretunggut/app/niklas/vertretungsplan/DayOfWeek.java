package vertretunggut.app.niklas.vertretungsplan;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by nwuensche on 22.09.16.
 */
public enum DayOfWeek {
    MONDAY(1),
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
        String title = repPlan.getTableTitle();
        int DatumVertrungsplan = getParsedWeekDayNumber(title);

        return getParsedWeekday(DatumVertrungsplan);
    }

    private static int getParsedWeekDayNumber(String titleTable) {
        StringTokenizer itemsOfTitle = new StringTokenizer(titleTable);
        itemsOfTitle.nextToken(); // Ignore shown Weekday, because "Heute" creates problems
        String date = itemsOfTitle.nextToken();

        Date parsedDate = parseStringToDate(date);

        Calendar c = Calendar.getInstance();
        c.setTime(parsedDate);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        return dayOfWeek;
    }

    private static Date parseStringToDate(String date){
        Date dateParser = null; // TODO besser

        try {
            dateParser = new SimpleDateFormat("dd.MM.yyyy").parse(date);
        }
        catch(ParseException e) {
            e.printStackTrace();
        }

        return dateParser;
    }

    private static DayOfWeek getParsedWeekday(int dayOfWeek) {
        // Caution: 1st day is Sunday
        switch(dayOfWeek){
            case 2:
                return DayOfWeek.MONDAY;
            case 3:
                return DayOfWeek.TUESDAY;
            case 4:
                return DayOfWeek.WEDNESDAY;
            case 5:
                return DayOfWeek.THURSDAY;
            case 6:
                return DayOfWeek.FRIDAY;
            case 7:
                return DayOfWeek.WEEKEND;
            case 1:
                return DayOfWeek.WEEKEND;
        }
        return null; // TODO besser
    }

    public static DayOfWeek getTodaysDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return getParsedWeekday(dayOfWeek);
    }

    public int getDifferenceTo(DayOfWeek compare) {
        return dayOfWeek-compare.getDayOfWeek();
    }
}
