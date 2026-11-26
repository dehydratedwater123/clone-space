package com.example.clonespace

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.os.Bundle
import android.widget.Toast

/** Runs only inside the work profile: installs a copy of the requested app there. */
class CloneReceiverActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pkg = intent.getStringExtra(EXTRA_PKG)
        val dpm = getSystemService(DevicePolicyManager::class.java)
        val admin = ComponentName(this, AdminReceiver::class.java)
        val ok = try {
            pkg != null && dpm.isProfileOwnerApp(packageName) && dpm.installExistingPackage(admin, pkg)
        } catch (e: Exception) { false }
        Toast.makeText(this, if (ok) "Cloned $pkg" else "Could not clone $pkg", Toast.LENGTH_LONG).show()
        finish()
    }
}
