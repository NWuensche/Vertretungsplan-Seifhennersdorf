package vertretunggut.app.niklas.vertretungsplan

import android.content.Context
import android.net.ConnectivityManager
import android.support.design.widget.FloatingActionButton
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by nwuensche on 26.09.16.
 */
class NoNetworkHandler(private val activity: MainActivity) {
    private val prevDay = activity.prev_day_button
    private val nextDay = activity.next_day_button

    fun showNoNetworkView() {
        activity.noNetworkLayout.visibility = View.VISIBLE

        prevDay.hide()
        nextDay.hide()
        activity.layout_of_reps.visibility = View.GONE
        activity.loadingPanel.visibility = View.GONE

        activity.noNetworkRetry.setOnClickListener { activity.handleNetworkAndStartGetter() }
    }

    fun disableNoNetworkView() {
        activity.noNetworkLayout.visibility = View.GONE

        prevDay.show()
        nextDay.show()
        activity.layout_of_reps.visibility = View.VISIBLE
        activity.loadingPanel.visibility = View.VISIBLE

        activity.restartRepPlanGetter()
    }

    companion object {

        fun isNetworkAvailable(activity: MainActivity): Boolean {
            val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}