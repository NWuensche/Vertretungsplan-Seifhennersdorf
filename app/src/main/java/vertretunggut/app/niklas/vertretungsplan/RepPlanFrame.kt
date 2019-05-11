package vertretunggut.app.niklas.vertretungsplan

import android.support.design.widget.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by nwuensche on 26.09.16.
 */
class RepPlanFrame(private val activity: MainActivity) {
    private val prevDay = activity.prev_day_button
    private val nextDay = activity.next_day_button

    init {
        prevDay.setOnClickListener { previousDayButtonPressed() }
        nextDay.setOnClickListener { nextDayButtonPressed() }
    }

    fun enableMoveButtons() {
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

        val connected = handleNetworkRight()

        if (connected) {
            activity.restartRepPlanGetter()
        }
    }

    fun nextDayButtonPressed() {

        activity.increaseCurrentRepPlanSite()
        nextDayButtonLastPressed = true

        val connected = handleNetworkRight()

        if (connected) {
            activity.restartRepPlanGetter()
        }
    }

    fun handleNetworkRight(): Boolean {
        if (!NoNetworkHandler.isNetworkAvailable(activity)) {
            NoNetworkHandler(activity).showNoNetworkView()
            return false
        }
        return true
    }

    companion object {
        private var nextDayButtonLastPressed = false
    }
}
