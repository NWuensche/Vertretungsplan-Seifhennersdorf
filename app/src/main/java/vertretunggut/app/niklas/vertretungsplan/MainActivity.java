package vertretunggut.app.niklas.vertretungsplan;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.jsoup.nodes.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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


}
