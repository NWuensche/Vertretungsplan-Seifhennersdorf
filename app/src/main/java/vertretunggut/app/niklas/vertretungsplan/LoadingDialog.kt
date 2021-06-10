package vertretunggut.app.niklas.vertretungsplan

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout

/**
 * Created by nwuensche on 26.09.16.
 */
class LoadingDialog(private var activity: MainActivity) : DialogBuilder {
    val progress_bar = activity.findViewById<ProgressBar>(R.id.progress_bar)
    val loadingPanel = activity.findViewById<RelativeLayout>(R.id.loadingPanel)
    val layout_of_reps = activity.findViewById<LinearLayout>(R.id.layout_of_reps)

    override fun buildDialog() {
        val loadingCircle = progress_bar
        val color = Color.parseColor("#ADD8E6")

        loadingCircle.indeterminateDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)

        loadingPanel.visibility = View.VISIBLE
        layout_of_reps.visibility = View.GONE
    }

    fun close() {
        loadingPanel.visibility = View.GONE
        layout_of_reps.visibility = View.VISIBLE
    }
}
