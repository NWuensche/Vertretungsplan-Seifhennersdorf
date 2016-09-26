package vertretunggut.app.niklas.vertretungsplan;

import android.os.AsyncTask;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by nwuensche on 22.09.16.
 */
public class GetRepPlan extends AsyncTask<Void, Void, Void> {
    private LoadingDialog loadingDialog;
    private RepPlan parsedRepPlan;
    private boolean Leer = false;
    private boolean LeerInhalt = false;
    private MainActivity mainActivity;
    private int currentSite;
    private Document repPlanHTML;
    private final int FIRST_SITE = 1;


    public GetRepPlan(MainActivity mainActivity, int currentSite) {
        this.mainActivity = mainActivity;
        this.currentSite = currentSite;
        repPlanHTML = new Document("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + currentSite + ".html"); // TODO nice
        parsedRepPlan = new RepPlan();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loadingDialog = new LoadingDialog(mainActivity);
        loadingDialog.buildDialog();
    }


    @Override
    protected Void doInBackground(Void... params) {

        if(mainActivity.isFirstThread()) {
            repPlanHTML = getTodaysRepPlan();
        }
        else{
            repPlanHTML = getRepPlanDocument(currentSite);
        }

        if (!repPlanForDayAvailable(repPlanHTML)) {
            parsedRepPlan.add("Leer");
            Leer = true;
            LeerInhalt = true;
            return null;
        }

        if(SearchFieldEmpty()) { // TODO schoolclass = Es wird gesucht?
            LeerInhalt = false;
            Elements Vertretungsplan = getRepPageTable(repPlanHTML); //TODO wirklich mit Argument oder von global nehmen?
            parseAndStoreRepPageTable(Vertretungsplan);
        }
        else {
            LeerInhalt = true;
            Elements Vertretungsplan = repPlanHTML.select(".list-table tr");
            boolean isFirstLine = true;
            for (Element Zeile : Vertretungsplan) {
                Elements EinzelnZeile = extract(Zeile);
                if(isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                for (Element data : EinzelnZeile) {
                    if(dataContainsSearch(data.text())){
                        storeWholeLine(EinzelnZeile);
                    }
                }
            }
        }
        return null;
    }

    private Document getRepPlanDocument(int SiteNumber){
        Document doc = new Document("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + currentSite + ".html"); // TODO No null!

        try {
            doc = Jsoup.connect("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + SiteNumber + ".html").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    public Document getTodaysRepPlan(){
        Document maybeRepPlanHTML = getRepPlanDocument(currentSite);
        DayOfWeek WochenTagVer = DayOfWeek.getDayOfWeekOfRepPlan(maybeRepPlanHTML);
        DayOfWeek WochenTagHeute = DayOfWeek.getTodaysDayOfWeek();

        int Difference = WochenTagHeute.getDifferenceTo(WochenTagVer);
        if (Difference > 0) {
            currentSite = (Difference % 5) + 1;
            maybeRepPlanHTML = getRepPlanDocument(currentSite);
            if (!repPlanForDayAvailable(maybeRepPlanHTML)) {
                currentSite = FIRST_SITE;
                maybeRepPlanHTML = getRepPlanDocument(currentSite);
            }
        }
        return maybeRepPlanHTML;
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

    private boolean SearchFieldEmpty(){
        return mainActivity.getSearch().equals("");
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
            if(inFirstRow) {
                inFirstRow = false;
                continue;
            }
            parsedRepPlan.add(currentData.text());
        }
    }

    private boolean dataContainsSearch(String data){
        data = data.toLowerCase();
        //TODO Why?
        return data.contains(mainActivity.getSearch().toLowerCase()) && !data.contains("i") && !data.contains("0")&& !data.contains("h4") && !data.contains("h1");
    }

    private void storeWholeLine(Elements line){
        LeerInhalt = false;
        parsedRepPlan.add(""); // To format right
        int currentRow = FIRST_SITE;
        for (Element data : line){
            if(currentRow > 2){
                parsedRepPlan.add(data.text());
            }
            currentRow++;
        }
    }
    
    @Override
    protected void onPostExecute(Void result) {
        ActionMenuItemView Links = (ActionMenuItemView) mainActivity.findViewById(R.id.vorheriger_tag);
        ActionMenuItemView Rechts = (ActionMenuItemView) mainActivity.findViewById(R.id.nächster_Tag);

        setUpFrame(Links, Rechts);

        if(!repPlanContainsDate() && !repPlanContainsContent()){
            String title = "Diesen Tag gibt es (noch) keine Vertretungen.";
            new OKTextDialog(mainActivity, title).buildDialog();

            disableLastPressedButton(Links, Rechts);
            Leer = false;
        }
        else if(repPlanContainsDate() && !repPlanContainsContent()){
            String title = "Es gibt keine Stunde für " + mainActivity.getSearch() +" an diesem Tag.";
            new OKTextDialog(mainActivity, title).buildDialog();

            parsedRepPlan.add("Leer");
        }
        LeerInhalt = true; // TODO Wozu? Für nächsten Thread?

        setUpRepPlanInFrame();
        loadingDialog.close();
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
                new OKTextDialog(mainActivity, title).buildDialog();
            }
        });
    }

}