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
                buildAboutDialog();
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
    private void buildAboutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View rootView = getLayoutInflater().inflate(R.layout.uber, null);

        addOKButton(builder, rootView);
        addPicture(builder, rootView);
        addTextViews(builder, rootView);

    }

    private void addOKButton(AlertDialog.Builder builder, final View rootView){
        builder.setView(rootView)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void addPicture(AlertDialog.Builder builder, final View rootView){
        ImageView I = (ImageView) rootView.findViewById(R.id.CC);
        I.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://creativecommons.org/licenses/by/3.0/"));
                startActivity(browserIntent);
            }
        });
    }

    private void addTextViews(AlertDialog.Builder builder, final View rootView) {
        String urlCCText = "http://game-icons.net/";
        String urlJSoupLicense = "http://jsoup.org/license";
        String urlJSoup = "http://jsoup.org/";
        int idCCText = R.id.CCText;
        int idMIT = R.id.mit;
        int idMITText = R.id.mittext;

        addTextView(builder, rootView, idCCText, urlCCText);
        addTextView(builder,rootView, idMIT, urlJSoupLicense);
        addTextView(builder, rootView, idMITText, urlJSoup);
    }

    private void addTextView(AlertDialog.Builder builder, final View rootView, int id, final String url) {
        TextView T = (TextView) rootView.findViewById(id);
        T.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);

            }
        });
    }




}
