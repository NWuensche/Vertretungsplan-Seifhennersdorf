package vertretunggut.app.niklas.vertretungsplan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int currentRepPlanSite;
    private String search = "";
    private boolean buttonRechts = false;
    private GetRepPlan repPlanGetter;
    private boolean firstTimeStarted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int firstRepPlanSite = 1;
        currentRepPlanSite = firstRepPlanSite;
        repPlanGetter = new GetRepPlan(this, currentRepPlanSite);

        if(networkAvailable()) {
            repPlanGetter.execute();
        }
        else{
            buildAndShowNoNetworkDialog();
        }
    }

    private void buildAndShowNoNetworkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View rootView = getLayoutInflater().inflate(R.layout.internetfehler, null);
        builder.setView(rootView)
                .setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .create()
                .show();
    }

    private boolean networkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.vorheriger_tag:
                previousDayButtonPressed();
                break;
            case R.id.nächster_Tag:
                nextDayButtonPressed();
                break;
            case R.id.Suchen:
                new SearchDialog(this).buildDialog();
                break;
            case R.id.Uber:
                new AboutDialog(this).buildDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void previousDayButtonPressed(){
        currentRepPlanSite--;
        buttonRechts = false;
        restartRepPlanGetter();
    }

    private void nextDayButtonPressed() {
        currentRepPlanSite++;
        buttonRechts = true;
        restartRepPlanGetter();
    }

    public void restartRepPlanGetter(){
        repPlanGetter.cancel(true);
        repPlanGetter = new GetRepPlan(this, currentRepPlanSite);
        repPlanGetter.execute();
    }

    public String getSearch(){
        return search;
    }

    public void setSearch(String search){
        this.search = search;
    }

    public boolean getButtonRechts(){
        return buttonRechts;
    }

    public boolean isFirstThread(){
        boolean firstTimeStartedTmp = firstTimeStarted;
        firstTimeStarted = false;
        return firstTimeStartedTmp;
    }

    // TODO Mit anderen Dialogen eigene Klasse machen





}
