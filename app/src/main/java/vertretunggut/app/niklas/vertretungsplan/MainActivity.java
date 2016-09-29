package vertretunggut.app.niklas.vertretungsplan;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private int currentRepPlanSite;
    private String search = "";
    private GetRepPlan repPlanGetter;
    private boolean firstTimeStarted = true;
    private final int FIRST_SITE = 1;
    private NoNetworkDialog noNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentRepPlanSite = FIRST_SITE;
        repPlanGetter = new GetRepPlan(this, currentRepPlanSite);

        handleNetworkAndStartGetter();
    }

    // TODO kein Handling, wenn Internet nach erster Seite ausfällt und ich weiter drücke
    public void handleNetworkAndStartGetter(){
        if(NoNetworkDialog.isNetworkAvailable(this)) {
            findViewById(R.id.noNetworkLayout).setVisibility(View.GONE);
            findViewById(R.id.next_day_button).setVisibility(View.VISIBLE);
            findViewById(R.id.prev_day_button).setVisibility(View.VISIBLE); // TODO was wenn kein tag aber internet weg, und knopf nicht da sein sollte

            findViewById(R.id.list_of_reps).setVisibility(View.VISIBLE);
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            repPlanGetter.execute();
        }
        else{

            findViewById(R.id.noNetworkLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.next_day_button).setVisibility(View.GONE);
            findViewById(R.id.prev_day_button).setVisibility(View.GONE);
            findViewById(R.id.list_of_reps).setVisibility(View.GONE);
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.noNetworkRetry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleNetworkAndStartGetter();
                }
            });
        }

    }

    public void increaseCurrentRepPlanSite(){
        currentRepPlanSite++;
    }

    public void decreaseCurrentRepPlanSite(){
        currentRepPlanSite--;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button
        return false;
    }

    @Override
    public boolean onQueryTextChange(String search) {
        // User changed the text
        this.search = search.replaceAll("\\s+","");
        repPlanGetter.searchFor(this.search);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO better?
        switch(item.getItemId()){
            case R.id.About:
                new AboutDialog(this).buildDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void restartRepPlanGetter() {
        repPlanGetter.cancel(true);
        repPlanGetter = new GetRepPlan(this, currentRepPlanSite);
        repPlanGetter.execute();
    }

    public String getSearch() {
        return search;
    }

    public boolean isFirstThread() {
        boolean firstTimeStartedTmp = firstTimeStarted;
        firstTimeStarted = false;
        return firstTimeStartedTmp;
    }
}
