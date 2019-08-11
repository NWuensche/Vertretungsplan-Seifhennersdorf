package vertretunggut.app.niklas.vertretungsplan

import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by nwuensche on 26.09.16.
 */

class MainActivityWrapper(private val activity: MainActivity) {
    private val prevDay = activity.prev_day_button
    private val nextDay = activity.next_day_button

    init {
        prevDay.setOnClickListener { previousDayButtonPressed() }
        nextDay.setOnClickListener { nextDayButtonPressed() }
    }

    fun hideMoveButtons() {
        nextDay.hide()
        prevDay.hide()
    }

    fun showMoveButtons() {
        nextDay.show()
        prevDay.show()
    }

    fun setTitle(headerTitle: String) {
        activity.supportActionBar!!.title = headerTitle
    }

    fun disableLastPressedButton() {
        if (nextDayButtonLastPressed()) {
            nextDay.hide()
        } else {
            prevDay.hide()
        }
    }

    fun nextDayButtonLastPressed(): Boolean {
        return nextDayButtonLastPressed
    }

    fun previousDayButtonPressed() {
        activity.decreaseCurrentRepPlanSite()
        nextDayButtonLastPressed = false

        val connected = NoNetworkHandler(activity).isNetworkAvailable()

        if (connected) {
            activity.loadNewSite()
        } else {
            showNoInternetView()
        }
    }

    fun nextDayButtonPressed() {

        activity.increaseCurrentRepPlanSite()
        nextDayButtonLastPressed = true

        val connected = NoNetworkHandler(activity).isNetworkAvailable()
        if (connected) {
            activity.loadNewSite()
        } else {
            showNoInternetView()
        }
    }

    fun showNoInternetView() {
        activity.noNetworkLayout.visibility = View.VISIBLE

        activity.layout_of_reps.visibility = View.GONE
        activity.loadingPanel.visibility = View.GONE

        MainActivityWrapper(activity).apply {
            hideMoveButtons()
            setTitle("Kein Internet")
        }
        activity.noNetworkRetry.setOnClickListener { handleUINetwork() }
    }

    /*
        TODO Can see last table short before new one if shown if
        internet on -> load first day -> internet off -> next day -> internet on -> retry
     */
    fun disableNoNetworkView(reloadPlan: Boolean) {
        activity.noNetworkLayout.visibility = View.GONE


        activity.loadingPanel.visibility = View.VISIBLE

        showMoveButtons()

        if (reloadPlan) activity.loadNewSite()

        activity.layout_of_reps.visibility = View.VISIBLE
    }

    fun handleUINetwork(reloadPlan: Boolean = true) {
        if (NoNetworkHandler(activity).isNetworkAvailable()) {
            disableNoNetworkView(reloadPlan)
        } else {
            showNoInternetView()
        }
    }

    companion object {
        private var nextDayButtonLastPressed = false
    }
}
