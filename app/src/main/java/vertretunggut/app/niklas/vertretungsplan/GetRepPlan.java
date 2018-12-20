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

import java.util.ArrayList;

/**
 * Created by nwuensche on 22.09.16.
 */
public class GetRepPlan extends AsyncTask<Void, Void, Void> {
    private LoadingDialog loadingDialog;
    private ArrayList<RepPlanLine> parsedRepPlanLines;
    private MainActivity mainActivity;
    private int currentSite;
    private RepPlanDocumentDecorator repPlanHTML;

    public GetRepPlan(MainActivity mainActivity, int currentSite) {
        this.mainActivity = mainActivity;
        this.currentSite = currentSite;
        repPlanHTML = new RepPlanDocumentDecorator("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + currentSite + ".html"); // TODO make that better, I don't need this.
        parsedRepPlanLines = new ArrayList<>();
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
            repPlanHTML = RepPlanDocumentDecorator.createTodaysDocument(mainActivity);
        }
        else{
            repPlanHTML = RepPlanDocumentDecorator.createDocument(currentSite);
        }

        // TODO Hier schon abfangen, wenn HTML keine Daten enthält

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

        parsedRepPlanLines.clear();
        if(search.equals("")) {
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
        RepPlanLine line;
        String hour = "";
        String teacher = "";
        String subject = "";
        String room = "";
        String schoolClass = "";
        String type = "";
        String message = "";
        int currColumn = 1;
        for (Element currentData : allDataInCurrentLine) {
            switch(currColumn){
                case 1:
                    hour = currentData.text();
                    break;
                case 2:
                    teacher = currentData.text();
                    break;
                case 3:
                    subject = currentData.text();
                    break;
                case 4:
                    room = currentData.text();
                    break;
                case 5:
                    schoolClass = currentData.text();
                    break;
                case 6:
                    type = currentData.text();
                    break;
                case 7:
                    message = currentData.text();
                    break;
                default:
                    break;
            }
            currColumn++;
        }
        line = new RepPlanLine(hour, teacher, subject, room, schoolClass, type, message);
        if(!line.isEmpty()){
            parsedRepPlanLines.add(line);
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
                    parseAndStoreDataInLine(EinzelnZeile);
                    break;
                }
            }
        }
    }

    private boolean dataContainsSearch(String data, String search) {
        data = data.toLowerCase();
        return data.contains(search.toLowerCase());
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
        return repPlanHTML.repPlanAvailable() && parsedRepPlanLines.isEmpty();
    }

    public boolean nothingToShow(){
        return !repPlanHTML.repPlanAvailable() || searchFoundNothing();
    }

    private void printRepPlan() {
        RepPlanAdapter adapter = new RepPlanAdapter(mainActivity, parsedRepPlanLines);
        ListView listOfReps = (ListView) mainActivity.findViewById(R.id.list_view);
        listOfReps.setAdapter(adapter);
    }

}