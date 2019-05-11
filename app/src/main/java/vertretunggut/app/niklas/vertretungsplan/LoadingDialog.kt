package vertretunggut.app.niklas.vertretungsplan

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.ProgressBar

/**
 * Created by nwuensche on 26.09.16.
 */
class LoadingDialog(internal var activity: MainActivity) : DialogBuilder {

    override fun buildDialog() {
        val loadingCircle = activity.findViewById<View>(R.id.progress_bar) as ProgressBar
        val color = Color.parseColor("#ADD8E6")

        loadingCircle.indeterminateDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)

        activity.findViewById<View>(R.id.loadingPanel).visibility = View.VISIBLE
        activity.findViewById<View>(R.id.layout_of_reps).visibility = View.GONE
    }

    fun close() {
        activity.findViewById<View>(R.id.loadingPanel).visibility = View.GONE
        activity.findViewById<View>(R.id.layout_of_reps).visibility = View.VISIBLE

    }
}
