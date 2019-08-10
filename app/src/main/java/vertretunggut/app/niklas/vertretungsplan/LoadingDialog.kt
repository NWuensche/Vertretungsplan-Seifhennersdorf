package vertretunggut.app.niklas.vertretungsplan

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by nwuensche on 26.09.16.
 */
class LoadingDialog(private var activity: MainActivity) : DialogBuilder {

    override fun buildDialog() {
        val loadingCircle = activity.progress_bar
        val color = Color.parseColor("#ADD8E6")

        loadingCircle.indeterminateDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)

        activity.loadingPanel.visibility = View.VISIBLE
        activity.layout_of_reps.visibility = View.GONE
    }

    fun close() {
        activity.loadingPanel.visibility = View.GONE
        activity.layout_of_reps.visibility = View.VISIBLE
    }
}
