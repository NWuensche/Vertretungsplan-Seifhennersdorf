package vertretunggut.app.niklas.vertretungsplan;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

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
            startSearch(repPlanTable, mainActivity.getSearch());
        }
        return null;
    }

    private boolean SearchFieldEmpty() {
        return mainActivity.getSearch().equals("");
    }



    public void searchFor(String search){
        Elements repPlanTable = repPlanHTML.getRepPageTable();

        parsedRepPlan.clear();
        if(search == null || search.equals("")) {// TODO wie wird es gemacht?
            parseAndStoreRepPageTable(repPlanTable);
        }
        else{
            startSearch(repPlanTable, search);
        }
        printRepPlan();

    }

    private void parseAndStoreRepPageTable(Elements table) {
        for (Element currentLine : table) {
            Elements allDataInCurrentLine = RepPlanDocumentDecorator.extract(currentLine);
            parseAndStoreDataInLine(allDataInCurrentLine);
        }
    }

    private void parseAndStoreDataInLine(Elements allDataInCurrentLine) {
        int currColumn = 1;
        for (Element currentData : allDataInCurrentLine) {
            if(currColumn <= 2) {
                currColumn++ ;
                continue;
            }
            parsedRepPlan.add(currentData.text());
        }
    }

    private void startSearch(Elements repPlanTable, String search) {
        boolean isFirstLine = true;
        for (Element Zeile : repPlanTable) {
            Elements EinzelnZeile = RepPlanDocumentDecorator.extract(Zeile);
            if(isFirstLine) {
                isFirstLine = false;
                continue;
            }
            for (Element data : EinzelnZeile) {
                if(dataContainsSearch(data.text(), search)){
                    storeWholeLine(EinzelnZeile);
                    break;
                }
            }
        }
    }

    private boolean dataContainsSearch(String data, String search) {
        data = data.toLowerCase();
        return data.contains(search.toLowerCase());
    }

    private void storeWholeLine(Elements line) {
        int currentRow = FIRST_SITE;
        for (Element data : line) {
            if(currentRow > 2) {
                parsedRepPlan.add(data.text());
            }
            currentRow++;
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        updateTitleBarTitle();

        printRepPlan();
        loadingDialog.close();
    }

    public void updateTitleBarTitle(){
        RepPlanFrame titleBar = new RepPlanFrame(mainActivity);

        String headerTitle;
        titleBar.enableMoveButtons();

        if(nothingToShow()) {
            headerTitle = "Kein Inhalt";
            createDialogAndMaybeDisableButton(titleBar);
        }
        else {
            headerTitle = repPlanHTML.getTableTitle();
        }

        titleBar.setTitle(headerTitle);
    }

    public void createDialogAndMaybeDisableButton(RepPlanFrame titleBar){
        String title = "";

        if(!repPlanHTML.repPlanAvailable()) {
            title = "Keine Vertretungen für diesen Tag";
            titleBar.disableLastPressedButton();
        }
        else if(searchFoundNothing()) {
            title = "Keine Vertretungen für " + mainActivity.getSearch() +" an diesem Tag.";
        }

        new TextToast(mainActivity, title).buildDialog();
    }

    public boolean searchFoundNothing() {
        return repPlanHTML.repPlanAvailable() && !parsedRepPlan.containsContent();
    }

    public boolean nothingToShow(){
        return !repPlanHTML.repPlanAvailable() || searchFoundNothing();
    }

    private void printRepPlan() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity, R.layout.list_row, parsedRepPlan.getPreviewList());
        ListView listOfReps = (ListView) mainActivity.findViewById(R.id.list);
        listOfReps.setAdapter(adapter);
        listOfReps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = parsedRepPlan.getFullTextAt(position);
                new TextToast(mainActivity, title).buildDialog();
            }
        });
    }

}