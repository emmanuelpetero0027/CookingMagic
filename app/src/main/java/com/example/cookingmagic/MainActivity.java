package com.example.cookingmagic;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cookingmagic.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import BottomNavigationBar.Add;
import BottomNavigationBar.Bookmark;
import BottomNavigationBar.Generate;
import BottomNavigationBar.ViewAll;
import Model.Bookmark.BookmarkModel;
import Model.Database.BookmarkDatabaseOpenHelper;
import Model.Database.DishInformationDatabaseOpenHelper;
import Model.ViewAll.DishInformationModel;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Define a list of target dates (using the format "MMMM/dd/yyyy")
        List<String> targetDates = new ArrayList<>();

        for(BookmarkModel dates: BookmarkDatabaseOpenHelper.getInstance(this).getBookmark()){
            targetDates.add(dates.getBookmarkDate());
        }

        // Check if today's date matches any of the target dates and schedule an alarm for future dates
        for (String targetDateStr : targetDates) {
            if (isTargetDateToday(targetDateStr)) {
                showNotification("Reminder", "Today is " + targetDateStr + ", it's time to Cook!");
            } else {
                // If the target date is in the future, schedule an alarm for that date
                scheduleNotification(targetDateStr);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            // You can send notifications
        } else {
            // Permission is not granted, request it
            requestNotificationPermission();
        }


        //binding bottom navigation bar
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        replaceFragment(new Generate());


        //Copying the Database to Local Storage
        try {
            DishInformationDatabaseOpenHelper.getInstance(this).onCreateDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        createBookmarkDatabase();

        //navigation bar
        binding.bottomNavigationView.setOnItemSelectedListener(item ->{
            if(item.getItemId() == R.id.generate){
                replaceFragment(new Generate());
            }
            else if(item.getItemId() == R.id.viewAll){
                replaceFragment(new ViewAll());
            }
            else if(item.getItemId() == R.id.bookMark){
                replaceFragment(new Bookmark());
            }
            else if(item.getItemId() == R.id.add){
                replaceFragment(new Add());
            }
            return true;
        });

    }
    //changing fragments for bottom navigation bar
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void createBookmarkDatabase() {
        BookmarkDatabaseOpenHelper dbHelper = BookmarkDatabaseOpenHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        System.out.print("Bookmark Database Created");
        if (db != null) {
            System.out.print("Bookmark Database Created");
        }
        db.close();
    }


    private static final String CHANNEL_ID = "notification_channel";

    private boolean isTargetDateToday(String targetDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM/dd/yyyy", java.util.Locale.ENGLISH);

        try {
            // Parse the target date string to Date object
            Date targetDate = sdf.parse(targetDateStr);

            // Get today's date
            Date today = new Date();

            // Compare the two dates (ignoring time part)
            return isSameDay(today, targetDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // Date format that ignores time
        return sdf.format(date1).equals(sdf.format(date2));
    }
    private void showNotification(String title, String message) {
        // Create the notification manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for devices running Android 8.0+ (API level 26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.main_bottom_nav_add_icon)  // Add your app icon here
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build();

        // Show the notification
        notificationManager.notify(1, notification);
    }
    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(String targetDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM/dd/yyyy", java.util.Locale.ENGLISH);

        try {
            Date targetDate = sdf.parse(targetDateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(targetDate);

            // Set up the alarm to go off at midnight on the target date (adjust the time as needed)
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // Create the intent to trigger the broadcast receiver
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("target_date", targetDateStr);  // Pass the target date to the receiver

            // Create a PendingIntent to trigger the broadcast
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);


            // Get the AlarmManager service
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            // Schedule the alarm to trigger at the specified time
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestNotificationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            // You can show an explanation to the user why the permission is needed (optional).
            // For now, we'll request the permission directly.
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now send notifications
            } else {
                // Permission denied, show a message to the user explaining that notifications won't work
                showPermissionDeniedDialog();
            }
        }
    }
    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Denied")
                .setMessage("Notification permission is required. Please enable it in the app settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    // Open app settings to allow the user to manually enable the permission
                    openAppSettings();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void openAppSettings() {
        // Open the app's settings page
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

}