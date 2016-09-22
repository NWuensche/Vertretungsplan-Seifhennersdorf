package vertretunggut.app.niklas.vertretungsplan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {



    private Calendar cal = Calendar.getInstance();
    private int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
    private String dayOfMonthStr = String.valueOf(dayOfMonth);

    private ArrayAdapter<String> s2;

    private Calendar sCalendar = Calendar.getInstance();
    private String Wochentag = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

    GetRepPlan t = new GetRepPlan(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isNetworkAvailable()) {
            t.execute();
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



                    .create().show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //menu.add("Test").setShowAsAction();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //   return true;
        //}

       //TODO Make this work again START HERE
        /*if(id ==  R.id.vorheriger_tag) {

            // if (!R.id.nächster_Tag.isEnabled()) Nächster.setEnabled(true);
            Seite--;
            // if (Seite == 1) Vor.setEnabled(false);
            //   Button Rechts = (Button) findViewById(R.id.nächster_Tag);
            // Rechts.setEnabled(true);
            buttonRechts = false;
            GetRepPlan t1 = new GetRepPlan();
            t1.execute();


        }
        else if(id == R.id.nächster_Tag){




            Seite++;
            //Button Links = (Button) findViewById(R.id.vorheriger_tag);
            // Links.setEnabled(true);
            buttonRechts = true;
            GetRepPlan t1 = new GetRepPlan();
            t1.execute();

        }
        else if (id == R.id.Suchen) {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View rootView = inflater.inflate(R.layout.dialog, null);
            builder.setView(rootView)
                    .setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText EKlasse = (EditText) rootView.findViewById(R.id.editKlasse);
                            Klasse = EKlasse.getText().toString();
                            Klasse.replaceAll("\\s+","");
                            GetRepPlan t1 = new GetRepPlan();
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
        }
        else if (id == R.id.Uber) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View rootView = inflater.inflate(R.layout.uber, null);
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

        }*/


        return super.onOptionsItemSelected(item);
    }




}
