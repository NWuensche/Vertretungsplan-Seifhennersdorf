package vertretunggut.app.niklas.vertretungsplan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

/**
 * Created by nwuensche on 22.09.16.
 */
public class GetRepPlan extends AsyncTask<Void, Void, Void> {
    private ProgressDialog loadingDialog;
    private RepPlan parsedRepPlan;
    private boolean firstTimeStarted = true;
    private boolean Leer = false;
    private boolean LeerInhalt = false;
    private String Stunde = "";
    private GridView listview;
    private ArrayAdapter<String> adapter;
    private ActionMenuItemView Datum;
    private MainActivity mainActivity;
    private int currentSite;
    private Document repPlanHTML;


    public GetRepPlan(MainActivity mainActivity, int currentSite) {
        this.mainActivity = mainActivity;
        this.currentSite = currentSite;
        repPlanHTML = null; // TODO nice
        parsedRepPlan = new RepPlan();
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
        Document repPlan = null; // TODO schöner
        
        if(firstTimeStarted) {
            firstTimeStarted = false; // TODO wird ein Thread überhaupt mehr als einmal benutzt?
            repPlan = getTodaysRepPlan();
        }

        if (!repPlanForDayAvailable(repPlan)) {
            parsedRepPlan.add("Nichts");
            Leer = true;
        }

        if(mainActivity.getKlasse().equals("")) {
            LeerInhalt = false;
            Elements Vertretungsplan = getRepPageTable(repPlan); //TODO wirklich mit Argument oder von global nehmen?
            parseAndStoreRepPageTable(Vertretungsplan);
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
                        parsedRepPlan.add(Stunde);
                        int wo2 = 1;
                        for(Element InfoAusgabe : EinzelnZeile){
                            if(wo2>2){
                                parsedRepPlan.add(InfoAusgabe.text());
                            }

                        }
                        wo2++;
                    }
                }


                    Wo++;


            }
                Wo=1;
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
        repPlanHTML = tryToGetRepPlanDocument(currentSite);
        DayOfWeek WochenTagVer = DayOfWeek.getDayOfWeekOfRepPlan(repPlanHTML);
        DayOfWeek WochenTagHeute = DayOfWeek.getTodaysDayOfWeek();

        int Difference = WochenTagHeute.getDifferenceTo(WochenTagVer);
        if (Difference > 0) {
            currentSite = (Difference % 5) + 1;
            repPlanHTML = tryToGetRepPlanDocument(currentSite);
            if (!repPlanForDayAvailable(repPlanHTML)) {
                int firstSite = 1; //TODO schöner?
                currentSite = firstSite;
                repPlanHTML = tryToGetRepPlanDocument(currentSite);
            }
        }
        return repPlanHTML;
    }

    private boolean repPlanForDayAvailable(Document repPlan){
        return !getTableTitleOfRepPage(repPlan).equals("");
    }

    private String getTableTitleOfRepPage(Document repPlan){
        return repPlan.select(".list-table-caption").text();
    }

    private Elements extract(Element line) {
        return line.select("td");
    }

    private Elements getRepPageTable(Document repPlan) {
        return repPlan.select(".list-table tr");
    }

    public void parseAndStoreRepPageTable(Elements table) {
        for (Element currentLine : table) {
            Elements allDataInCurrentLine = extract(currentLine);
            parseAndStoreDataInLine(allDataInCurrentLine);
        }
    }

    private void parseAndStoreDataInLine(Elements allDataInCurrentLine) {
        boolean inFirstRow = true;
        for (Element currentData : allDataInCurrentLine) {
            if(!inFirstRow) {
                parsedRepPlan.add(currentData.text());
            }
            inFirstRow = false;
        }
    }


    @Override
    protected void onPostExecute(Void result) {
        listview = (GridView) mainActivity.findViewById(R.id.gridview_liste);
        Datum = (ActionMenuItemView) mainActivity.findViewById(R.id.Tag);
        Datum.setTitle(getTableTitleOfRepPage(repPlanHTML)); // TODO Get Day here

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
            parsedRepPlan.add("Nichts");

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

        adapter = new ArrayAdapter<String>(mainActivity, R.layout.itemliste, R.id.item_liste, parsedRepPlan.getPreviewList());
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String genauer = parsedRepPlan.getFullTextAt(position);
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