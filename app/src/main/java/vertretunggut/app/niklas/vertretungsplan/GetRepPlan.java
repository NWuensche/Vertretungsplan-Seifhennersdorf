package vertretunggut.app.niklas.vertretungsplan;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nwuensche on 22.09.16.
 */
public class GetRepPlan {
    private LoadingDialog loadingDialog;
    private ArrayList<RepPlanLine> parsedRepPlanLines;
    private MainActivity mainActivity;
    private int currentSite;
    private RepPlanDocumentDecorator repPlanHTML;

       /* //TODO Brauch ich das?
        if(mainActivity.isFirstThread()) {
            repPlanHTML = RepPlanDocumentDecorator.Companion.createTodaysDocument(mainActivity);
        }
        else{
            repPlanHTML = RepPlanDocumentDecorator.Companion.createDocument(currentSite);
        }*/

    private boolean SearchFieldEmpty() {
        return mainActivity.getSearch().equals("");
    }

    //If in search, always use the last set hour as current Hour. lastSetHour is the last displayed hour in the table, so the current hour.

    private void startSearch(Elements repPlanTable, String search) {
        boolean isFirstLine = true;
        String lastSetHour = "";
        for (Element Zeile : repPlanTable) {
            Elements EinzelnZeile = RepPlanDocumentDecorator.Companion.extract(Zeile);
            if(isFirstLine) {
                isFirstLine = false;
                continue;
            }

            // Store last hour that was set
            if(!((Element) EinzelnZeile.toArray()[0]).text().replace("\u00A0", "").isEmpty()) {
                lastSetHour = ((Element) EinzelnZeile.toArray()[0]).text().replace("\u00A0", "");
            }

            for (Element data : EinzelnZeile) {
                if(dataContainsSearch(data.text(), search)){
                    //parseAndStoreDataInLine(EinzelnZeile, lastSetHour, true);
                    break;
                }
            }
        }
    }

    private boolean dataContainsSearch(String data, String search) {
        data = data.toLowerCase();
        return data.contains(search.toLowerCase());
    }

    protected void onPostExecute(Void result) {
        updateTitleBarTitle();

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


}