import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.app.AlarmManager
import android.content.pm.PackageManager

fun checkAndRequestExactAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Check if the app has permission to schedule exact alarms
        if (context.checkSelfPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, open the settings screen to request it
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
        }
    }
}
