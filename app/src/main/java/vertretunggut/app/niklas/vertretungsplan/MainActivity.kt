package vertretunggut.app.niklas.vertretungsplan

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import androidx.core.view.MenuItemCompat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    var search = ""
        private set
    private val FIRST_SITE = 1
    var currentRepPlanSite = FIRST_SITE
    private var getK: Plan? = null
    val main_layout: FrameLayout by lazy {findViewById(R.id.main_layout)}
    val prev_day_button: FloatingActionButton by lazy {findViewById(R.id.prev_day_button)}
    val next_day_button: FloatingActionButton by lazy {findViewById(R.id.next_day_button)}
    val layout_of_reps: LinearLayout by lazy {findViewById(R.id.layout_of_reps)}
    val loadingPanel: RelativeLayout by lazy {findViewById(R.id.loadingPanel)}
    val noNetworkLayout: LinearLayout by lazy {findViewById(R.id.noNetworkLayout)}
    val list_view: ListView by lazy {findViewById(R.id.list_view)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MainActivityWrapper(this).handleUINetwork(reloadPlan = false)// Otherwise searchForToday will get messy
        loadNewSite(currentRepPlanSite, true)
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

        val searchView = searchItem.actionView as SearchView
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
            main_layout.setBackgroundDrawable(resources.getDrawable(R.drawable.school_horizontal))
        } else {
            main_layout.setBackgroundDrawable(resources.getDrawable(R.drawable.school_vertical))
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

    fun loadNewSite(currentSite: Int = this.currentRepPlanSite, searchForToday: Boolean = false) {
        GlobalScope.launch (Dispatchers.Main) {
            val loadingDialog = LoadingDialog(this@MainActivity)
            loadingDialog.buildDialog()

            GlobalScope.launch(Dispatchers.IO) {
                getK = Plan(this@MainActivity , searchForToday)
            }.join()
            // Block Buttons until new page displayed to prevent double clicking

            renderTableWithSearch(search,getK)
            getK?.updateTitleBarTitle(this@MainActivity, NoNetworkHandler(this@MainActivity).isNetworkAvailable())
            loadingDialog.close()
        }
    }

    private fun renderTableWithSearch(search: String, getK: Plan?) {
        val tableToShow = getTableWithSearch(search, getK)
        showTable(tableToShow)
    }

    private fun getTableWithSearch(search: String, getK: Plan?): List<RepPlanLine> {
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
