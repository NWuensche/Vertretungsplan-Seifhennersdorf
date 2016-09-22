package vertretunggut.app.niklas.vertretungsplan;

import android.util.Log;

import org.jsoup.nodes.Document;

import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by nwuensche on 22.09.16.
 */
public class Weekdays {

    public static int getWochenTagHeute() {
        Calendar sCalendar = Calendar.getInstance();
        String Wochentag = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        return getWochenTagZahl("Freitag");
    }

    public static int getWochenTagZahl(String Tag){
        switch(Tag){
            case "Montag":
                return 1;
            case "Dienstag":
                return 2;
            case "Mittwoch":
                return 3;
            case "Donnerstag":
                return 4;
            case "Freitag":
                return 5;
            case "Samstag":
                return 6;
            case "Sonntag":
                return 6;
            default:
                Log.e("Fehler bei getWocheZahl","d");
                return -1;
        }
    }

    public static int getWochenTagVertretung(Document repPlan){
        String Tag = repPlan.select(".list-table-caption").text();
        StringTokenizer DatumMonat = new StringTokenizer(Tag);

        String DatumVertrungsplan = DatumMonat.nextToken();
        Log.e("testVer", DatumVertrungsplan);
        return getWochenTagZahl(DatumVertrungsplan);
        //TODO Zurück zu 001, wenn kein Vertretungsplan
    }
}
