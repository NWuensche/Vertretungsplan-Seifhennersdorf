package vertretunggut.app.niklas.vertretungsplan;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private int currentRepPlanSite;
    private String search = "";
    private GetRepPlan repPlanGetter;
    private boolean firstTimeStarted = true;
    private final int FIRST_SITE = 1;
    private NoNetworkHandler noNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentRepPlanSite = FIRST_SITE;
        repPlanGetter = new GetRepPlan(this, currentRepPlanSite);
        noNetwork = new NoNetworkHandler(this);

        handleNetworkAndStartGetter();
    }

    public void handleNetworkAndStartGetter(){
        if(NoNetworkHandler.isNetworkAvailable(this)) { // TODO besser
            noNetwork.disableNoNetworkView();
        }
        else{
            noNetwork.showNoNetworkView();
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

        switch(item.getItemId()){
            case R.id.About:
                createAboutPage();
                break;
            case R.id.Exit:
                System.exit(0);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createAboutPage(){
        Element logo = getLicenseElement("dem Logo", "http://game-icons.net/");
        Element jSoup = getLicenseElement("jsoup", "http://jsoup.org/");
        Element aboutPageBuilder = getLicenseElement("android-about-page", "https://github.com/medyo/android-about-page");

        View aboutPage = new AboutPage(this)
                .isRTL(false) // TODO ?
                .setDescription("Dies ist die offizielle Vertretungsplan-App vom Oberland-Gymnasium Seifhennersdorf")
                .setImage(R.drawable.buch)
                .addGroup("Kontakt")
                .addEmail("wuensche.niklas@gmail.com")
                .addPlayStore("niklas.app.vertretunggut")
                .addGitHub("nwuensche")
                .addGroup("Lizenzen")
                .addItem(logo)
                .addItem(jSoup)
                .addItem(aboutPageBuilder)
                .create();

        RelativeLayout aboutLayout = (RelativeLayout) findViewById(R.id.aboutLayout);
        aboutLayout.addView(aboutPage);

        FloatingActionButton prevDay = (FloatingActionButton) findViewById(R.id.prev_day_button);
        FloatingActionButton nextDay = (FloatingActionButton) findViewById(R.id.next_day_button);
        prevDay.hide();
        nextDay.hide();
        findViewById(R.id.layout_of_reps).setVisibility(View.GONE);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        findViewById(R.id.noNetworkLayout).setVisibility(View.GONE);

    }

    public Element getLicenseElement(String name, final String website) {
        Element item = new Element();

        item.setTitle("Klick hier für die Website von " + name);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                startActivity(browserIntent);
            }
        });

        return item;
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
