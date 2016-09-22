package vertretunggut.app.niklas.vertretungsplan;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.os.AsyncTask;


import org.jsoup.nodes.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private GridView listview;
    private ArrayAdapter<String> adapter;
    private ActionMenuItemView Datum;
    private boolean buttonRechts = false;
    private boolean Start = true;
    private boolean Leer = false;

    private String Klasse = "";
    private String Stunde = "";

    private Calendar cal = Calendar.getInstance();
    private int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
    private String dayOfMonthStr = String.valueOf(dayOfMonth);

    private int Seite = 1;
    private Document doc = new Document("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + Seite + ".html");
    private ArrayList<String> ssK = new ArrayList<>();
    private ArrayList<String> ss = new ArrayList<>();
    private ArrayAdapter<String> s2;
    private ProgressDialog mProgressDialog;
    private String Tag;
    private boolean LeerInhalt = false;

    private Calendar sCalendar = Calendar.getInstance();
    private String Wochentag = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public int getWochenTagHeute(){
        Calendar sCalendar = Calendar.getInstance();
        String Wochentag = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        return getWochenTagZahl("Freitag");
    }

    public int getWochenTagZahl(String Tag){
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

    public int getWochenTagVertretung(){
        Tag = doc.select(".list-table-caption").text();

        StringTokenizer DatumMonat = new StringTokenizer(Tag);

        String DatumVertrungsplan = DatumMonat.nextToken();
        Log.e("testVer", DatumVertrungsplan);
        return getWochenTagZahl(DatumVertrungsplan);
        //TODO Zurück zu 001, wenn kein Vertretungsplan

    }

    class Test extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

    }


}
