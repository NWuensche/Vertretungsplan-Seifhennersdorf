package vertretunggut.app.niklas.vertretungsplan;

import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    private RepPlanDocumentDecorator repPlanHTML;
    private final int FIRST_SITE = 1;

    public GetRepPlan(MainActivity mainActivity, int currentSite) {
        this.mainActivity = mainActivity;
        this.currentSite = currentSite;
        repPlanHTML = new RepPlanDocumentDecorator("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + currentSite + ".html"); // TODO nice
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
            repPlanHTML = RepPlanDocumentDecorator.createTodaysDocument();
        }
        else{
            repPlanHTML = RepPlanDocumentDecorator.createDocument(currentSite);
        }

        if (!repPlanHTML.repPlanAvailable()) {
            parsedRepPlan.add("Leer");
            Leer = true;
            LeerInhalt = true;
            return null;
        }

        if(SearchFieldEmpty()) { // TODO schoolclass = Es wird gesucht?
            LeerInhalt = false;
            Elements Vertretungsplan = repPlanHTML.getRepPageTable(repPlanHTML); //TODO wirklich mit Argument oder von global nehmen?
            parseAndStoreRepPageTable(Vertretungsplan);
        }
        else {
            LeerInhalt = true;
            Elements Vertretungsplan = repPlanHTML.select(".list-table tr");
            boolean isFirstLine = true;
            for (Element Zeile : Vertretungsplan) {
                Elements EinzelnZeile = RepPlanDocumentDecorator.extract(Zeile);
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



    private boolean SearchFieldEmpty(){
        return mainActivity.getSearch().equals("");
    }

    public void parseAndStoreRepPageTable(Elements table) {
        for (Element currentLine : table) {
            Elements allDataInCurrentLine = RepPlanDocumentDecorator.extract(currentLine);
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
        RepPlanFrame visualRepPlan = new RepPlanFrame(mainActivity);

        String headerTitle = repPlanHTML.getTableTitle();
        visualRepPlan.setUpFrame(headerTitle);

        if(!repPlanContainsDate() && !repPlanContainsContent()){
            String title = "Diesen Tag gibt es (noch) keine Vertretungen.";
            new OKTextDialog(mainActivity, title).buildDialog();

            visualRepPlan.disableLastPressedButton();
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


    private boolean repPlanContainsDate(){
        return !Leer;
    }

    private boolean repPlanContainsContent(){
        return !LeerInhalt;
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