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
        Elements repPlanTable;

        if(mainActivity.isFirstThread()) {
            repPlanHTML = RepPlanDocumentDecorator.createTodaysDocument();
        }
        else{
            repPlanHTML = RepPlanDocumentDecorator.createDocument(currentSite);
        }

        repPlanTable = repPlanHTML.getRepPageTable();

        if(SearchFieldEmpty()) {
            parseAndStoreRepPageTable(repPlanTable);
        }
        else {
            startSearch(repPlanTable);
        }
        return null;
    }

    private boolean SearchFieldEmpty() {
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

    private void startSearch(Elements repPlanTable) {
        boolean isFirstLine = true;
        for (Element Zeile : repPlanTable) {
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

    private boolean dataContainsSearch(String data) {
        data = data.toLowerCase();
        return data.contains(mainActivity.getSearch().toLowerCase());
    }

    private void storeWholeLine(Elements line) {
        addBlankCell();
        int currentRow = FIRST_SITE;
        for (Element data : line) {
            if(currentRow > 2) {
                parsedRepPlan.add(data.text());
            }
            currentRow++;
        }
    }

    private void addBlankCell(){
        parsedRepPlan.add("");
    }
    
    @Override
    protected void onPostExecute(Void result) {
        RepPlanFrame titleBar = new RepPlanFrame(mainActivity);

        String headerTitle;
        titleBar.enableAllButtons();

        if(nothingToShow()) {
            headerTitle = "Leer";
            createDialogAndMaybeDisableButton(titleBar);
        }
        else {
            headerTitle = repPlanHTML.getTableTitle();
        }

        titleBar.setTitle(headerTitle);

        printRepPlan();
        loadingDialog.close();
    }

    public void createDialogAndMaybeDisableButton(RepPlanFrame titleBar){
        String title = "";

        if(!repPlanHTML.repPlanAvailable()) {
            title = "Diesen Tag gibt es (noch) keine Vertretungen.";
            titleBar.disableLastPressedButton();
        }
        else if(searchFoundNothing()) {
            title = "Es gibt keine Stunde für " + mainActivity.getSearch() +" an diesem Tag.";
        }

        new OKTextDialog(mainActivity, title).buildDialog();
    }

    public boolean searchFoundNothing() {
        return repPlanHTML.repPlanAvailable() && !parsedRepPlan.containsContent();
    }

    public boolean nothingToShow(){
        return !repPlanHTML.repPlanAvailable() || searchFoundNothing();
    }

    private void printRepPlan() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity, R.layout.itemliste, R.id.item_liste, parsedRepPlan.getPreviewList());
        GridView listOfReps = (GridView) mainActivity.findViewById(R.id.list_of_reps);
        listOfReps.setAdapter(adapter);
        listOfReps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = parsedRepPlan.getFullTextAt(position);
                new OKTextDialog(mainActivity, title).buildDialog();
            }
        });
    }

}