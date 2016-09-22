package vertretunggut.app.niklas.vertretungsplan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.os.AsyncTask;
import android.widget.TextView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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

    Test t = new Test();
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
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Vertretungsplan");
            // Set progressdialog message
            mProgressDialog.setMessage("Laden...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            doc = new Document("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + Seite + ".html");
            try {
                doc = Jsoup.connect("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + Seite + ".html").get();
            } catch (IOException e) {
                e.printStackTrace();


            }
            Tag = doc.select(".list-table-caption").text();
            ss = new ArrayList<>();
            ssK.clear();
            ssK = new ArrayList<>();
            if(Start) {
                Start = false;
                int WochenTagVer = getWochenTagVertretung();
                int WochenTagHeute = getWochenTagHeute();
                Log.e("test", dayOfMonthStr);
                //TODO Differenz Zeug nur, wenn nicht auf Button gedrückt

                int Differenz = WochenTagHeute - WochenTagVer;
                Integer Mod = (-4 + 5) % 5;
                Log.e("Modulo", Mod.toString());

                if (Differenz > 0) {
                    Seite = ((Differenz + 5) % 5) + 1;
                    Log.e("test", "" + Seite + "");
                    Log.e("test", Wochentag);
                    doc = new Document("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + Seite + ".html");
                    try {
                        doc = Jsoup.connect("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + Seite + ".html").get();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("testNeuerPlan", "Fehler");


                    }


                    Tag = doc.select(".list-table-caption").text();
                    if (Tag.equals("")) {
                        Seite = 1;
                        doc = new Document("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + Seite + ".html");
                        try {
                            doc = Jsoup.connect("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + Seite + ".html").get();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("testNeuerPlan", "Fehler");


                        }
                    }
                    Tag = doc.select(".list-table-caption").text();
                }
            }

            if (Tag.equalsIgnoreCase("")) {
                ss.add("Nichts");
                Leer = true;


            }


            if(Klasse.equals("")) {
                LeerInhalt = false;
                Elements Vertretungsplan = doc.select(".list-table tr");
                int Wo = 1;
                for (Element Zeile : Vertretungsplan) {
                    Elements EinzelnZeile = Zeile.select("td");

                    for (Element Info : EinzelnZeile) {

                        if(Wo!=1){
                            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                                if(Info.text().length()>6){
                                    ss.add(Info.text().substring(0,4) +"..");
                                    ssK.add(Info.text());
                                }
                                else{
                                    ss.add(Info.text());
                                    ssK.add(Info.text());
                                }
                            }
                            else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                                if(Info.text().length()>3){
                                    ss.add(Info.text().substring(0,1) +"..");
                                    ssK.add(Info.text());
                                }
                                else{
                                    ss.add(Info.text());
                                    ssK.add(Info.text());
                                }
                            }
                        }
                        Wo++;


                    }
                    Wo=1;
                }
            }
            else{
                LeerInhalt = true;
                Elements Vertretungsplan = doc.select(".list-table tr");

                int Wo = 1;
                for (Element Zeile : Vertretungsplan) {
                    Elements EinzelnZeile = Zeile.select("td");

                    for (Element Info : EinzelnZeile) {

                        if (Wo==2 && (Info.text().equals("1")|| Info.text().equals("2") || Info.text().equals("7") || Info.text().equals("8") || Info.text().equals("3") || Info.text().equals("4") || Info.text().equals("5") || Info.text().equals("6") )){
                            Stunde = Info.text();

                        }

                        if(Info.text().toLowerCase().contains(Klasse.toLowerCase()) && !Info.text().toLowerCase().contains("i")&& !Info.text().toLowerCase().contains("0")&& !Info.text().toLowerCase().contains("h4")&& !Info.text().toLowerCase().contains("h1")){
                            LeerInhalt = false;
                            ss.add(Stunde);

                            ssK.add(Stunde);
                            int wo2 = 1;
                            for(Element InfoAusgabe : EinzelnZeile){
                                if(wo2>2){

                                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                                        if(InfoAusgabe.text().length()>6){
                                            ss.add(InfoAusgabe.text().substring(0,4) +"..");
                                            ssK.add(InfoAusgabe.text());
                                        }
                                        else{
                                            ss.add(InfoAusgabe.text());
                                            ssK.add(InfoAusgabe.text());
                                        }
                                    }
                                    else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                                        if(InfoAusgabe.text().length()>3){
                                            ss.add(InfoAusgabe.text().substring(0,1) +"..");
                                            ssK.add(InfoAusgabe.text());
                                        }
                                        else{
                                            ss.add(InfoAusgabe.text());
                                            ssK.add(InfoAusgabe.text());
                                        }
                                    }


                                }
                                wo2++;
                            }
                        }


                        Wo++;


                    }
                    Wo=1;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            listview = (GridView) findViewById(R.id.gridview_liste);
            Datum = (ActionMenuItemView) findViewById(R.id.Tag);
            Datum.setTitle(Tag);
            // Pass the results into ListViewAdapter.java
                /*for(int i = 0; i<ss.size();i++){
                    TextView p = new TextView(MainActivity.this);
                    p.setText(ss.get(i).toString());
                    listview.addView(p);
                }*/

            ActionMenuItemView Rechts = (ActionMenuItemView) findViewById(R.id.nächster_Tag);
            Rechts.setEnabled(true);

            ActionMenuItemView Links = (ActionMenuItemView) findViewById(R.id.vorheriger_tag);
            Links.setEnabled(true);





            if(LeerInhalt && !Leer){

                String genauer = "Es gibt keine Stunde für " +Klasse+" an diesem Tag.";
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View rootView = inflater.inflate(R.layout.genaueritem, null);
                TextView genauerT = (TextView) rootView.findViewById(R.id.genauertextview);
                genauerT.setText(genauer);
                ss.add("Nichts");

                builder.setView(rootView)

                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        .create().show();
            }
            if(Leer){
                String genauer = "Diesen Tag gibt es (noch) keine Vertretungen.";
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View rootView = inflater.inflate(R.layout.genaueritem, null);
                TextView genauerT = (TextView) rootView.findViewById(R.id.genauertextview);
                genauerT.setText(genauer);
                if (buttonRechts) {

                    Rechts.setEnabled(false);
                } else {

                    Links.setEnabled(false);
                }
                builder.setView(rootView)

                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        .create().show();
                Leer = false;
            }
            LeerInhalt = true;

            adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.itemliste, R.id.item_liste, ss);
            // Set the adapter to the ListView
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String genauer = ssK.get(position);


                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                    final View rootView = inflater.inflate(R.layout.genaueritem, null);
                    TextView genauerT = (TextView) rootView.findViewById(R.id.genauertextview);
                    genauerT.setText(genauer);

                    builder.setView(rootView)

                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })

                            .create().show();

                }
            });
            //listview.getItemAtPosition()
            // Close the progressdialog
            mProgressDialog.dismiss();
        }

    }


}
