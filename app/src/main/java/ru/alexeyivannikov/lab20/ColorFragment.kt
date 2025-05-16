package ru.alexeyivannikov.lab20

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Action
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.alexeyivannikov.lab20.databinding.FragmentColorBinding

class ColorFragment : Fragment() {

    private var _binding: FragmentColorBinding? = null
    private val binding: FragmentColorBinding
        get() = _binding ?: throw RuntimeException()

    val broadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                COLOR_ACTION -> {
                    val colorHex =
                        RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_COLOR).toString()
                    selectColor(colorHex)
                    cancelNotification()
                }

                REJECT_ACTION -> {
                    resetColor()
                    cancelNotification()
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            sendSimpleNotification()
        } else {
            navigateToExplanation()
        }
    }

    private lateinit var notificationManager: NotificationManagerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationManager = NotificationManagerCompat.from(requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentColorBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setOnClickListeners()
    }

    override fun onStart() {
        super.onStart()
        ContextCompat.registerReceiver(requireActivity(), broadcastReceiver, IntentFilter().apply {
            addAction(COLOR_ACTION)
            addAction(REJECT_ACTION)
        }, ContextCompat.RECEIVER_EXPORTED)
    }

    private fun setOnClickListeners() {
        binding.btGetContacts.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        android.Manifest.permission.POST_NOTIFICATIONS
                    )
                ) {
                    navigateToExplanation()
                } else {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                sendSimpleNotification()
            }
        }
    }


    private fun createNotificationChanel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Выбор цвета",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }


    private fun sendSimpleNotification() {
        createNotificationChanel()

        val actionRejectPendingIntent = PendingIntent.getBroadcast(
            requireActivity(),
            0,
            Intent().apply { action = REJECT_ACTION },
            PendingIntent.FLAG_IMMUTABLE
        )

        val colorInput = RemoteInput.Builder(KEY_COLOR).run {
            setLabel("Введите цвет...")
            build()
        }

        val setColorIntent = Intent("ru.alexeyivannikov.lab20").apply {
            action = COLOR_ACTION
        }
        setColorIntent.setPackage(PACKAGE)

        val setColorPendingIntent = PendingIntent.getBroadcast(
            requireActivity(),
            1,
            setColorIntent,
            PendingIntent.FLAG_MUTABLE
        )


        val setColorAction =
            Action.Builder(R.drawable.notification_bell, "Ввести цвет", setColorPendingIntent)
                .addRemoteInput(colorInput)
                .build()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(requireActivity(), CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_bell)
            .setContentTitle("Выбор цвета")
            .setContentText("Можно ввести цвет, например \"#FFAABB\"")
            .addAction(R.drawable.notification_bell, "Сбросить цвет", actionRejectPendingIntent)
            .addAction(setColorAction)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        val notification = builder.build()
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify("tag", NOTIFICATION_ID, notification)
    }

    fun resetColor() {
        binding.tvColorHexLabel.text = resources.getText(R.string.color_not_selected)
        binding.root.setBackgroundColor(Color.WHITE)
    }

    fun selectColor(color: String) {
        try {
            val colorInt = color.toColorInt()
            binding.root.setBackgroundColor(colorInt)

            val s = resources.getText(R.string.color_selected).toString()

            binding.tvColorHexLabel.text =
                String.format(s, color)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(requireActivity(), "Ошибка обработки цвета: $color", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelNotification() {
        notificationManager.cancel("tag",NOTIFICATION_ID)
    }

    private fun navigateToExplanation() {
        findNavController().navigate(R.id.explanationFragment)
    }

    companion object {
        const val NOTIFICATION_ID = 14
        const val PACKAGE = "ru.alexeyivannikov.lab20"
        const val COLOR_ACTION = "$PACKAGE.select_color"
        const val REJECT_ACTION = "$PACKAGE.reject"
        private const val CHANNEL_ID = "lab20_channel_msg"
        const val KEY_COLOR = "notification_color"
    }
}