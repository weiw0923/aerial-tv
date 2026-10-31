package app.orchard.tv

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import app.orchard.tv.system.TmallM31H

/**
 * Interceptor for the yunostv_yingshi:// deep links that this firmware's remote keys fire
 * (Tmall MagicBox M31H, 天猫魔盒: the M key reaches the stock launcher as
 * yunostv_yingshi://goto_tab?tabId=1039, the "M键生活广场" ad portal, without ever becoming a
 * key event). Registered with priority 999 so implicit resolution lands here before the stock
 * launcher's own filter. On that box, tabId 1039 is swallowed and every other deep link is
 * forwarded to the stock launcher untouched. The filter is in the manifest for every install;
 * on any other device the link is handed to the next app that claims it and nothing is swallowed.
 */
class DeepLinkInterceptor : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri: Uri? = intent?.data
        if (!TmallM31H.isTmallM31H()) {
            forwardToOtherApp(uri)
            finish()
            return
        }
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

    /** This activity won resolution only because of the manifest filter. Give the link back. */
    private fun forwardToOtherApp(uri: Uri?) {
        if (uri == null) return
        val view = Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_DEFAULT)
        val next = packageManager.queryIntentActivities(view, PackageManager.MATCH_DEFAULT_ONLY)
            .firstOrNull { it.activityInfo.packageName != packageName } ?: return
        runCatching {
            startActivity(
                Intent(view).setClassName(next.activityInfo.packageName, next.activityInfo.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }.onFailure { Log.w("AerialGuard", "could not hand deep link $uri to another app", it) }
    }
}
