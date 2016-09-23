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

        if(!mainActivityHasSchoolClass()) {
            LeerInhalt = false;
            Elements Vertretungsplan = getRepPageTable(repPlan); //TODO wirklich mit Argument oder von global nehmen?
            parseAndStoreRepPageTable(Vertretungsplan);
        }
        else {
            LeerInhalt = true;
            Elements Vertretungsplan = repPlan.select(".list-table tr");

            int Wo = 1;
            for (Element Zeile : Vertretungsplan) {
                Elements EinzelnZeile = extract(Zeile);
                String lesson = "";
                for (Element data : EinzelnZeile) {
                    if(dataIsLesson(Wo, data.text())){
                        lesson = data.text();
                    }
                    if(dataHasValidFormat(data.text())){
                        LeerInhalt = false;
                        parsedRepPlan.add(lesson);
                    }
                }
                    Wo++;
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

    private boolean mainActivityHasSchoolClass(){
        return !mainActivity.getSchoolClass().equals("");
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

    private boolean dataIsLesson(int row, String data){
        if(row == 2){
            int lesson = tryToParseInt(data);
            return (row >= 1) && (row <= 8);
        }
        return false;

    }

    private int tryToParseInt(String data){
        try{
            return Integer.parseInt(data);
        }
        catch(NumberFormatException e){
            return 0; // TODO Besser
        }
    }

    private boolean dataHasValidFormat(String data){
        data = data.toLowerCase();
        //TODO Why?
        return data.contains(mainActivity.getSchoolClass().toLowerCase()) && !data.contains("i") && !data.contains("0")&& !data.contains("h4") && !data.contains("h1");

    }


    @Override
    protected void onPostExecute(Void result) {
        ActionMenuItemView Links = (ActionMenuItemView) mainActivity.findViewById(R.id.vorheriger_tag);
        ActionMenuItemView Rechts = (ActionMenuItemView) mainActivity.findViewById(R.id.nächster_Tag);

        setUpFrame(Links, Rechts);

        if(!repPlanContainsDate() && !repPlanContainsContent()){
            String title = "Diesen Tag gibt es (noch) keine Vertretungen.";
            buildDialog(title);

            disableLastPressedButton(Links, Rechts);
            Leer = false;
        }
        else if(repPlanContainsDate() && !repPlanContainsContent()){
            String title = "Es gibt keine Stunde für " + mainActivity.getSchoolClass() +" an diesem Tag.";
            buildDialog(title);
            parsedRepPlan.add("Nichts");
        }
        LeerInhalt = true; // TODO Wozu? Für nächsten Thread?

        setUpRepPlanInFrame();
        closeLoadingDialog();
    }

    private void setUpFrame(ActionMenuItemView leftButton, ActionMenuItemView rightButton) {

        ActionMenuItemView date = (ActionMenuItemView) mainActivity.findViewById(R.id.Tag);
        date.setTitle(getTableTitleOfRepPage(repPlanHTML));

        leftButton.setEnabled(true);
        rightButton.setEnabled(true);
    }

    private boolean repPlanContainsDate(){
        return !Leer;
    }

    private boolean repPlanContainsContent(){
        return !LeerInhalt;
    }

    private void buildDialog(String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.genaueritem, null);
        TextView genauerT = (TextView) rootView.findViewById(R.id.genauertextview);
        genauerT.setText(title);

        builder.setView(rootView)

                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .create().show();
    }

    private void disableLastPressedButton(ActionMenuItemView leftButton, ActionMenuItemView rightButton){
        if (mainActivity.getButtonRechts()) {
            rightButton.setEnabled(false);
        } else {
            leftButton.setEnabled(false);
        }
    }

    private void setUpRepPlanInFrame(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity, R.layout.itemliste, R.id.item_liste, parsedRepPlan.getPreviewList());
        GridView listview = (GridView) mainActivity.findViewById(R.id.gridview_liste);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = parsedRepPlan.getFullTextAt(position);
                buildDialog(title);
            }
        });
    }

    private void closeLoadingDialog(){
        loadingDialog.dismiss();
    }
}