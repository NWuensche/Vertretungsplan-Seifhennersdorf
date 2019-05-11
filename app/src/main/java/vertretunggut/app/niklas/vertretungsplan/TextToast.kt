package vertretunggut.app.niklas.vertretungsplan

import android.widget.Toast

/**
 * Created by nwuensche on 26.09.16.
 */
class TextToast(private val activity: MainActivity, private val text: String) : DialogBuilder {

    override fun buildDialog() {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }
}
