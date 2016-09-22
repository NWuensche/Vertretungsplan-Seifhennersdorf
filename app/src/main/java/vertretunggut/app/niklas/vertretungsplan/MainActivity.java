package vertretunggut.app.niklas.vertretungsplan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.Calendar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


}
