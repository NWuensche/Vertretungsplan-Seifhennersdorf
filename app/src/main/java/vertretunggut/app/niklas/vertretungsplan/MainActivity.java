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
    private String schoolClass = "";
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
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            LayoutInflater inflater2 = this.getLayoutInflater();
            final View rootView2 = inflater2.inflate(R.layout.internetfehler, null);
            builder2.setView(rootView2)
                    .setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .create()
                    .show();
        }
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
        // TODO Blöden Block weg
        GetRepPlan newRepPlanGetter;
        AlertDialog.Builder builder;
        LayoutInflater inflater;
        final View rootView;
        final MainActivity self = this;

        // TODO Alte Threads löschen?
        switch(item.getItemId()){
            case R.id.vorheriger_tag:
                currentRepPlanSite--;
                buttonRechts = false;
                newRepPlanGetter = new GetRepPlan(self, currentRepPlanSite);
                newRepPlanGetter.execute();
                break;
            case R.id.nächster_Tag:
                currentRepPlanSite++;
                buttonRechts = true;
                newRepPlanGetter = new GetRepPlan(this, currentRepPlanSite);
                newRepPlanGetter.execute();
                break;
            case R.id.Suchen:
                builder = new AlertDialog.Builder(this);
                inflater = this.getLayoutInflater();
                rootView = inflater.inflate(R.layout.dialog, null);
                builder.setView(rootView)
                        .setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText EKlasse = (EditText) rootView.findViewById(R.id.editKlasse);
                                schoolClass = EKlasse.getText().toString();
                                schoolClass.replaceAll("\\s+","");
                                GetRepPlan t1 = new GetRepPlan(self, currentRepPlanSite);
                                t1.execute();


                            }
                        })

                        .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        .create().show();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                break;
            case R.id.Uber:
                builder = new AlertDialog.Builder(this);
                inflater = this.getLayoutInflater();
                rootView = inflater.inflate(R.layout.uber, null);
                builder.setView(rootView)

                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        .create().show();
                ImageView I = (ImageView) rootView.findViewById(R.id.CC);
                I.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://creativecommons.org/licenses/by/3.0/"));
                        startActivity(browserIntent);
                    }
                });

                TextView T = (TextView) rootView.findViewById(R.id.CCText);
                T.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://game-icons.net/"));
                        startActivity(browserIntent);

                    }
                });
                TextView T2 = (TextView) rootView.findViewById(R.id.mit);
                T2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jsoup.org/license"));
                        startActivity(browserIntent);

                    }
                });
                TextView T3 = (TextView) rootView.findViewById(R.id.mittext);
                T3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jsoup.org/"));
                        startActivity(browserIntent);

                    }
                });
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public String getSchoolClass(){
        return schoolClass;
    }

    public boolean getButtonRechts(){
        return buttonRechts;
    }

    public boolean isFirstThread(){
        boolean firstTimeStartedTmp = firstTimeStarted;
        firstTimeStarted = false;
        return firstTimeStartedTmp;
    }




}
