package com.example.clonespace

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.*

class MainActivity : Activity() {
    private val REQ_PROVISION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dpm = getSystemService(DevicePolicyManager::class.java)

        // Inside the work profile: nothing to do here, cloning is requested from the personal side.
        if (dpm.isProfileOwnerApp(packageName)) {
            setContentView(TextView(this).apply {
                text = "Clone Space is active.\n\nOpen Clone Space from your personal home screen and tap an app to clone it here. Clones appear with a briefcase badge."
                setPadding(48, 96, 48, 48); textSize = 16f
            })
            return
        }

        val pm = packageManager
        val apps = pm.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)
            .map { it.loadLabel(pm).toString() to it.activityInfo.packageName }
            .filter { it.second != packageName }
            .sortedBy { it.first.lowercase() }

        val create = Button(this).apply {
            text = "1. Create clone space (one time)"
            setOnClickListener { createProfile(dpm) }
        }
        val list = ListView(this).apply {
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1,
                apps.map { "${it.first}\n${it.second}" })
            setOnItemClickListener { _, _, pos, _ -> clone(apps[pos].second) }
        }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(create)
            addView(TextView(context).apply { text = "2. Tap an app to clone it"; setPadding(32, 16, 32, 16) })
            addView(list, LinearLayout.LayoutParams(-1, 0, 1f))
        })
    }

    private fun createProfile(dpm: DevicePolicyManager) {
        if (!dpm.isProvisioningAllowed(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE)) {
            Toast.makeText(this, "This device doesn't allow a work profile (or one already exists).", Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE).putExtra(
            DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME,
            ComponentName(this, AdminReceiver::class.java))
        startActivityForResult(intent, REQ_PROVISION)
    }

    private fun clone(pkg: String) {
        try {
            startActivity(Intent(ACTION_CLONE).putExtra(EXTRA_PKG, pkg))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Create the clone space first (step 1).", Toast.LENGTH_LONG).show()
        }
    }
}
