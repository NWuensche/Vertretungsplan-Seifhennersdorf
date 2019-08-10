package vertretunggut.app.niklas.vertretungsplan

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import android.widget.Toast
import arrow.core.Option

import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.nodes.Document
import kotlin.system.exitProcess

fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    var search = ""
        private set //TODO Better
    private val FIRST_SITE = 1
    private var currentRepPlanSite: Int = FIRST_SITE
    private var getK: GetK? = null
    var connectedToInternet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentRepPlanSite = FIRST_SITE

        loadNewSite(currentRepPlanSite, true)

        handleNetworkAndStartGetter(NoNetworkHandler(this))
    }

    fun handleNetworkAndStartGetter(nnH: NoNetworkHandler) {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        val connected = if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
        connectedToInternet = connected
        if (connected) {
            nnH.disableNoNetworkView()
        } else {
            nnH.showNoNetworkView()
        }

    }
    //TODO search drin title wrong

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
        searchView.setOnQueryTextListener(this) //TODO Ist das nötig

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
        renderTableWithSearch(this.search, getK)
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.About -> createAboutPage()
            R.id.Exit -> exitProcess(0)
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

        prev_day_button.hide()
        next_day_button.hide()
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

    fun restartRepPlanGetter(site: Int = currentRepPlanSite) {
        loadNewSite(site)
    }

    fun setCurrentRepPlanSite(site: Int) {
        this.currentRepPlanSite = site
    }

    fun loadNewSite(currentSite: Int = this.currentRepPlanSite, searchForToday: Boolean = false) {
        GlobalScope.launch (Dispatchers.Main) {
            //onPreExecution
            val loadingDialog = LoadingDialog(this@MainActivity)
            loadingDialog.buildDialog()

            //doInBackground
            GlobalScope.launch(Dispatchers.IO) {
                getK = GetK(currentSite, searchForToday)

            }.join()
            renderTableWithSearch(search,getK)
            getK?.updateTitleBarTitle(this@MainActivity, connectedToInternet)
            loadingDialog.close()
        }
    }

    private fun renderTableWithSearch(search: String, getK: GetK?) {
        val tableToShow = getTableWithSearch(search, getK)
        showTable(tableToShow)
    }

    private fun getTableWithSearch(search: String, getK: GetK?): List<RepPlanLine> {
        if (search.isEmpty()) {
            return getK?.parseAndStoreRepPageTable() ?: emptyList()
        } else {
            return getK?.startSearch(search) ?: emptyList()
        }
    }

    private fun showTable(tableToShow: List<RepPlanLine>) {
        val adapter = RepPlanAdapter(this, ArrayList(tableToShow))
        list_view.adapter = adapter
    }


}
