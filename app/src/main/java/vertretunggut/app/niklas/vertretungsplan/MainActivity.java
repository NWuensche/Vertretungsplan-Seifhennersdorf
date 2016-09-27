package vertretunggut.app.niklas.vertretungsplan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private int currentRepPlanSite;
    private String search = "";
    private boolean buttonRechts = false;
    private GetRepPlan repPlanGetter;
    private boolean firstTimeStarted = true;
    private final int FIRST_SITE = 1;
    private RepPlanFrame headOfRepPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int firstRepPlanSite = FIRST_SITE;

        currentRepPlanSite = firstRepPlanSite;
        repPlanGetter = new GetRepPlan(this, currentRepPlanSite);

        if(NoNetworkDialog.isNetworkAvailable(this)) {
            repPlanGetter.execute();
        }
        else{
            new NoNetworkDialog(this).buildDialog();
        }



        headOfRepPlan = new RepPlanFrame(this);

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.vorheriger_tag:
                headOfRepPlan.previousDayButtonPressed();
                break;
            case R.id.nächster_Tag:
                headOfRepPlan.nextDayButtonPressed();
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



    public void restartRepPlanGetter() {
        repPlanGetter.cancel(true);
        repPlanGetter = new GetRepPlan(this, currentRepPlanSite);
        repPlanGetter.execute();
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public boolean nextDayButtonLastPressed() {
        return buttonRechts;
    }

    public boolean isFirstThread() {
        boolean firstTimeStartedTmp = firstTimeStarted;
        firstTimeStarted = false;
        return firstTimeStartedTmp;
    }
}
