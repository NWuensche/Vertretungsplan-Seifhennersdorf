package vertretunggut.app.niklas.vertretungsplan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nwuensche on 22.09.16.
 */
public class GetRepPlan extends AsyncTask<Void, Void, Void> {
    private ProgressDialog loadingDialog;
    private ArrayList<String> ss = new ArrayList<>();
    private ArrayList<String> ssK = new ArrayList<>();
    private boolean firstTimeStarted = true;
    private boolean Leer = false;
    private boolean LeerInhalt = false;
    private String Stunde = "";
    private GridView listview;
    private ArrayAdapter<String> adapter;
    private ActionMenuItemView Datum;
    private MainActivity mainActivity;
    private int currentSite;
    private Document repPlan;


    public GetRepPlan(MainActivity mainActivity, int currentSite) {
        this.mainActivity = mainActivity;
        this.currentSite = currentSite;
        repPlan = null; // TODO nice
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startLoadingDialog();
    }

    private void startLoadingDialog(){
        loadingDialog = new ProgressDialog(mainActivity);
        loadingDialog.setTitle("Vertretungsplan");
        loadingDialog.setMessage("Laden...");
        loadingDialog.setIndeterminate(false);
        loadingDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        ss = new ArrayList<>();
        ssK = new ArrayList<>();
        Document repPlan = null; // TODO schöner
        
        if(firstTimeStarted) {
            firstTimeStarted = false; // TODO wird ein Thread überhaupt mehr als einmal benutzt?
            repPlan = getTodaysRepPlan();
        }

        if (!repPlanForDayAvailable(repPlan)) {
            ss.add("Nichts");
            Leer = true;
        }


        if(mainActivity.getKlasse().equals("")) {
            LeerInhalt = false;
            Elements Vertretungsplan = repPlan.select(".list-table tr");
            int Wo = 1;
            for (Element Zeile : Vertretungsplan) {
                Elements EinzelnZeile = Zeile.select("td");

                for (Element Info : EinzelnZeile) {

                    if(Wo!=1){
                        if(mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                            if(Info.text().length()>6){
                                ss.add(Info.text().substring(0,4) +"..");
                                ssK.add(Info.text());
                            }
                            else{
                                ss.add(Info.text());
                                ssK.add(Info.text());
                            }
                        }
                        else if(mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
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
            Elements Vertretungsplan = repPlan.select(".list-table tr");

            int Wo = 1;
            for (Element Zeile : Vertretungsplan) {
                Elements EinzelnZeile = Zeile.select("td");

                for (Element Info : EinzelnZeile) {

                    if (Wo==2 && (Info.text().equals("1")|| Info.text().equals("2") || Info.text().equals("7") || Info.text().equals("8") || Info.text().equals("3") || Info.text().equals("4") || Info.text().equals("5") || Info.text().equals("6") )){
                        Stunde = Info.text();

                    }

                    if(Info.text().toLowerCase().contains(mainActivity.getKlasse().toLowerCase()) && !Info.text().toLowerCase().contains("i")&& !Info.text().toLowerCase().contains("0")&& !Info.text().toLowerCase().contains("h4")&& !Info.text().toLowerCase().contains("h1")){
                        LeerInhalt = false;
                        ss.add(Stunde);

                        ssK.add(Stunde);
                        int wo2 = 1;
                        for(Element InfoAusgabe : EinzelnZeile){
                            if(wo2>2){

                                if(mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                                    if(InfoAusgabe.text().length()>6){
                                        ss.add(InfoAusgabe.text().substring(0,4) +"..");
                                        ssK.add(InfoAusgabe.text());
                                    }
                                    else{
                                        ss.add(InfoAusgabe.text());
                                        ssK.add(InfoAusgabe.text());
                                    }
                                }
                                else if(mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
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

    private Document tryToGetRepPlanDocument(int SiteNumber){
        Document doc = null; // TODO No null!

        try {
            doc = Jsoup.connect("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + SiteNumber + ".html").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    public Document getTodaysRepPlan(){
        repPlan = tryToGetRepPlanDocument(currentSite);
        DayOfWeek WochenTagVer = DayOfWeek.getDayOfWeekOfRepPlan(repPlan);
        DayOfWeek WochenTagHeute = DayOfWeek.getTodaysDayOfWeek();

        int Differenz = WochenTagHeute.getDifferenceTo(WochenTagVer);

        if (Differenz > 0) {
            currentSite = (Differenz % 5) + 1;
            repPlan = tryToGetRepPlanDocument(currentSite);
            if (!repPlanForDayAvailable(repPlan)) {
                int firstSite = 1; //TODO schöner?
                currentSite = firstSite;
                repPlan = tryToGetRepPlanDocument(currentSite);
            }
        }
        return repPlan;
    }

    private boolean repPlanForDayAvailable(Document repPlan){
        return !getDayOfRepPlan(repPlan).equals("");
    }

    private String getDayOfRepPlan(Document repPlan){
        return repPlan.select(".list-table-caption").text();
    }


    @Override
    protected void onPostExecute(Void result) {
        listview = (GridView) mainActivity.findViewById(R.id.gridview_liste);
        Datum = (ActionMenuItemView) mainActivity.findViewById(R.id.Tag);
        Datum.setTitle(getDayOfRepPlan(repPlan)); // TODO Get Day here

        ActionMenuItemView Rechts = (ActionMenuItemView) mainActivity.findViewById(R.id.nächster_Tag);
        Rechts.setEnabled(true);

        ActionMenuItemView Links = (ActionMenuItemView) mainActivity.findViewById(R.id.vorheriger_tag);
        Links.setEnabled(true);

        if(LeerInhalt && !Leer){

            String genauer = "Es gibt keine Stunde für " + mainActivity.getKlasse() +" an diesem Tag.";
            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
            LayoutInflater inflater = mainActivity.getLayoutInflater();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
            LayoutInflater inflater = mainActivity.getLayoutInflater();
            final View rootView = inflater.inflate(R.layout.genaueritem, null);
            TextView genauerT = (TextView) rootView.findViewById(R.id.genauertextview);
            genauerT.setText(genauer);
            if (mainActivity.getButtonRechts()) {

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

        adapter = new ArrayAdapter<String>(mainActivity, R.layout.itemliste, R.id.item_liste, ss);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String genauer = ssK.get(position);


                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                LayoutInflater inflater = mainActivity.getLayoutInflater();
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
        loadingDialog.dismiss();
    }

}