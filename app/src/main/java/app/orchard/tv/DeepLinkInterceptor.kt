package app.orchard.tv

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

/**
 * Interceptor for the yunostv_yingshi:// deep links that this firmware's remote keys fire
 * (MagicBox M31H: the M key reaches the stock launcher as
 * yunostv_yingshi://goto_tab?tabId=1039, the "M键生活广场" ad portal, without ever becoming a
 * key event). Registered with priority 999 so implicit resolution lands here before the stock
 * launcher's own filter. tabId 1039 is swallowed; every other deep link is forwarded to the
 * stock launcher untouched.
 */
class DeepLinkInterceptor : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri: Uri? = intent?.data
        val tabId = uri?.getQueryParameter("tabId")
        if (tabId == "1039") {
            Log.i("AerialGuard", "M-key portal deep link swallowed")
        } else {
            runCatching {
                val forward = Intent(intent).setClassName(
                    "com.youku.taitan.tv",
                    "com.youku.tv.home.activity.TabActivity",
                )
                startActivity(forward)
            }.onFailure { Log.w("AerialGuard", "could not forward deep link $uri", it) }
        }
        finish()
    }
}
