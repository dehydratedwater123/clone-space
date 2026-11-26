package com.example.clonespace

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager

const val ACTION_CLONE = "com.example.clonespace.CLONE"
const val EXTRA_PKG = "pkg"

class AdminReceiver : DeviceAdminReceiver() {
    // Runs inside the new work profile once Android finishes creating it.
    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        val dpm = context.getSystemService(DevicePolicyManager::class.java)
        val admin = getComponentName(context)
        dpm.setProfileName(admin, "Clone Space")

        // Let the personal side send "clone this package" requests into the profile.
        context.packageManager.setComponentEnabledSetting(
            ComponentName(context, CloneReceiverActivity::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )
        val filter = IntentFilter(ACTION_CLONE).apply { addCategory(Intent.CATEGORY_DEFAULT) }
        dpm.addCrossProfileIntentFilter(admin, filter, DevicePolicyManager.FLAG_PARENT_CAN_ACCESS_MANAGED)

        dpm.setProfileEnabled(admin)
    }
}
