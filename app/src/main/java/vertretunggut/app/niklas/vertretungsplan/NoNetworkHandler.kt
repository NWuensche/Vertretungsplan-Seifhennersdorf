package vertretunggut.app.niklas.vertretungsplan

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by nwuensche on 26.09.16.
 */
// TODO Error Message when connected with VPN
class NoNetworkHandler(private val activity: MainActivity) {

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
