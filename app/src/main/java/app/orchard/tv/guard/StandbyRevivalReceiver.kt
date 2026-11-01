package app.orchard.tv.guard

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import app.orchard.tv.system.TmallM31H

/**
 * A one-minute alarm that outlives the guard's process. The firmware (Tmall MagicBox M31H,
 * 天猫魔盒, YunOS) kills the guard during standby despite every background exemption; with the
 * guard dies the shallow-standby heartbeat, and the box slides into deep suspend where the
 * remote cannot wake it. AlarmManager is system-managed: the alarm fires into a dead process,
 * which restarts it, and the receiver re-enables accessibility so the system rebinds the guard —
 * whose onServiceConnected then sees the screen still off and resumes the heartbeat. Each firing
 * arms the next one; setExactAndAllowWhileIdle so it also cuts through suspend (>= 9 s apart).
 *
 * Armed only on that box. On Android 12+ an exact alarm needs [AlarmManager.canScheduleExactAlarms]
 * (the SCHEDULE_EXACT_ALARM permission is not granted by default from Android 14); without it the
 * same revival is scheduled inexact instead of throwing SecurityException.
 */
class StandbyRevivalReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!TmallM31H.isTmallM31H()) {
            // A build that armed this on every device may still have a pending alarm.
            cancel(context)
            return
        }
        arm(context)
        val pm = context.getSystemService(PowerManager::class.java) ?: return
        if (!GuardSetup.isUserUnlocked(context)) return
        if (pm.isInteractive) return
        // Screen off: the heartbeat must be running. ensureEnabled re-enables the guard when the
        // firmware has switched it off (returns true only when it just did); the setting change
        // makes the system rebind the service, and onServiceConnected resumes the heartbeat.
        if (GuardSetup.ensureEnabled(context)) {
            Log.w(TAG, "standby revival: guard had been switched off; re-enabled")
        }
    }

    companion object {
        private const val TAG = "AerialGuard"
        private const val REQUEST = 20260726

        fun arm(context: Context) {
            if (!TmallM31H.isTmallM31H()) return
            val am = context.getSystemService(AlarmManager::class.java) ?: return
            val pi = pending(context, PendingIntent.FLAG_UPDATE_CURRENT)
            val at = System.currentTimeMillis() + 60_000L
            // canScheduleExactAlarms exists from Android 12. Before that exact alarms need no grant.
            val exact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms()
            runCatching {
                if (exact) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC, at, pi)
                } else {
                    am.setAndAllowWhileIdle(AlarmManager.RTC, at, pi)
                }
            }.onSuccess {
                Log.i(TAG, if (exact) "standby revival armed, exact" else "exact alarms not allowed; standby revival armed inexact")
            }.onFailure { Log.w(TAG, "could not arm the standby revival", it) }
        }

        /** Drops a pending alarm left by a build that scheduled one on every device. */
        fun cancel(context: Context) {
            val am = context.getSystemService(AlarmManager::class.java) ?: return
            val pi = PendingIntent.getBroadcast(
                context,
                REQUEST,
                Intent(context, StandbyRevivalReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            ) ?: return
            am.cancel(pi)
        }

        private fun pending(context: Context, mutability: Int): PendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST,
                Intent(context, StandbyRevivalReceiver::class.java),
                mutability or PendingIntent.FLAG_IMMUTABLE,
            )
    }
}
