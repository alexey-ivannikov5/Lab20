package ru.alexeyivannikov.lab20

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("NOTIFICSTION", "main activity created")
        setContentView(R.layout.activity_main)

//        val navHostFragment = supportFragmentManager
//            .findFragmentById(R.id.fc_main) as NavHostFragment
//
//        val navController = navHostFragment.navController
//        navController.navigate(R.id.colorFragment)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}