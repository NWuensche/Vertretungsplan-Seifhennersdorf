package vertretunggut.app.niklas.vertretungsplan

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import arrow.core.Option

import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private var currentRepPlanSite: Int = 0
    var search = ""
        private set
    private var repPlanGetter: GetRepPlan? = null
    private var firstTimeStarted = true
    private val FIRST_SITE = 1
    private var noNetwork: NoNetworkHandler? = null
    var getK: GetK? = null


    val isFirstThread: Boolean
        get() {
            val firstTimeStartedTmp = firstTimeStarted
            firstTimeStarted = false
            return firstTimeStartedTmp
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentRepPlanSite = FIRST_SITE
        var doc: Option<Document> = Option.empty()
        GlobalScope.launch (Dispatchers.Main) {
            val loadingDialog = LoadingDialog(this@MainActivity)
            loadingDialog.buildDialog()
            GlobalScope.launch (Dispatchers.IO) {
                getK = GetK(currentRepPlanSite)
            }.join()
            loadingDialog.close()
        }
        repPlanGetter = GetRepPlan(this, currentRepPlanSite)
        noNetwork = NoNetworkHandler(this)

        handleNetworkAndStartGetter()
    }

    fun handleNetworkAndStartGetter() {
        if (NoNetworkHandler.isNetworkAvailable(this)) {
            noNetwork!!.disableNoNetworkView()
        } else {
            noNetwork!!.showNoNetworkView()
        }

    }

    fun increaseCurrentRepPlanSite() {
        currentRepPlanSite++
    }

    fun decreaseCurrentRepPlanSite() {
        currentRepPlanSite--
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu.findItem(R.id.search)
        //TODO Old
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.setOnQueryTextListener(this)

        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        // User pressed the search button
        return false
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val main = main_layout
            //TODO 2x Deprecated
            main.setBackgroundDrawable(resources.getDrawable(R.drawable.school_horizontal))
        } else {
            val main = findViewById<View>(R.id.main_layout) as FrameLayout
            main.setBackgroundDrawable(resources.getDrawable(R.drawable.school_vertical))
        }
    }

    override fun onQueryTextChange(search: String): Boolean {
        // User changed the text
        this.search = search.replace("\\s+".toRegex(), "")
        repPlanGetter!!.searchFor(this.search)
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.About -> createAboutPage()
            R.id.Exit -> System.exit(0)
        }

        return super.onOptionsItemSelected(item)
    }

    fun createAboutPage() {
        val school = getLicenseElement("der Schule", "https://www.gymnasium-seifhennersdorf.de/")
        val logo = getLicenseElement("dem Logo", "http://game-icons.net/")
        val jSoup = getLicenseElement("jsoup", "http://jsoup.org/")
        val aboutPageBuilder = getLicenseElement("android-about-page", "https://github.com/medyo/android-about-page")


        val aboutPage = AboutPage(this)
                .isRTL(false)
                .setDescription("Dies ist die offizielle Vertretungsplan-App vom Oberland-Gymnasium Seifhennersdorf")
                .setImage(R.drawable.buch)
                .addGroup("Kontakt")
                .addEmail("wuensche.niklas@gmail.com")
                .addPlayStore("niklas.app.vertretunggut")
                .addGitHub("nwuensche")
                .addGroup("Lizenzen")
                .addItem(school)
                .addItem(logo)
                .addItem(jSoup)
                .addItem(aboutPageBuilder)
                .create()

        val aboutLayout = findViewById<View>(R.id.aboutLayout) as RelativeLayout
        aboutLayout.addView(aboutPage)

        val prevDay = findViewById<View>(R.id.prev_day_button) as FloatingActionButton
        val nextDay = findViewById<View>(R.id.next_day_button) as FloatingActionButton
        prevDay.hide()
        nextDay.hide()
        layout_of_reps.visibility = View.GONE
        loadingPanel.visibility = View.GONE
        noNetworkLayout.visibility = View.GONE

    }

    fun getLicenseElement(name: String, website: String): Element {
        val item = Element()

        item.title = "Klick hier für die Website von $name"
        item.onClickListener = View.OnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(website))
            startActivity(browserIntent)
        }

        return item
    }

    fun restartRepPlanGetter() {
        repPlanGetter!!.cancel(true)
        repPlanGetter = GetRepPlan(this, currentRepPlanSite)
        repPlanGetter!!.execute()
    }

    fun setCurrentRepPlanSite(site: Int) {
        this.currentRepPlanSite = site
    }
}
