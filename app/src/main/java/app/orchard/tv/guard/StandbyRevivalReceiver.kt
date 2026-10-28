package app.orchard.tv.guard

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log

/**
 * A one-minute alarm that outlives the guard's process. The firmware (YunOS MagicBox M31H) kills
 * the guard during standby despite every background exemption; with the guard dies the
 * shallow-standby heartbeat, and the box slides into deep suspend where the remote cannot wake
 * it. AlarmManager is system-managed: the alarm fires into a dead process, which restarts it, and
 * the receiver re-enables accessibility so the system rebinds the guard — whose
 * onServiceConnected then sees the screen still off and resumes the heartbeat. Each firing arms
 * the next one; setExactAndAllowWhileIdle so it also cuts through suspend (>= 9 s apart).
 */
class StandbyRevivalReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        arm(context)
        val pm = context.getSystemService(PowerManager::class.java) ?: return
        if (!GuardSetup.isUserUnlocked(context)) return
        if (pm.isInteractive) return
        // Screen off: the heartbeat must be running. ensureEnabled re-enables the guard when the
        // firmware has switched it off (returns true only when it just did); the setting change
        // makes the system rebind the service, and onServiceConnected resumes the heartbeat.
        if (GuardSetup.ensureEnabled(context)) {
            Log.w("AerialGuard", "standby revival: guard had been switched off; re-enabled")
        }
    }

    companion object {
        private const val REQUEST = 20260726

        fun arm(context: Context) {
            val am = context.getSystemService(AlarmManager::class.java) ?: return
            val pi = PendingIntent.getBroadcast(
                context,
                REQUEST,
                Intent(context, StandbyRevivalReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            runCatching {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + 60_000L, pi)
            }
        }
    }
}
