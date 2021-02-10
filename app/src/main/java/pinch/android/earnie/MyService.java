package pinch.android.earnie;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static pinch.android.earnie.App.CHANNEL_ID;

public class MyService extends Service {

    FirebaseAuth auth;
    String input, savingId;
    String savedAmount;
    boolean isSet;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.i("SERVICESSS","START");
        auth = FirebaseAuth.getInstance();
        input = intent.getStringExtra("inputExtra");
        savedAmount = intent.getStringExtra("saved");
        savingId = intent.getStringExtra("savingId");
//        try {
//            Intent notificationIntent = new Intent(this, MainActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                    0, notificationIntent, 0);
//
//            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setContentTitle("Earnie Running")
//                    .setSmallIcon(R.drawable.ic_launcher_background)
//                    .setContentIntent(pendingIntent)
//                    .build();
//            startForeground(1, notification);
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        getSavedAmount();
        deductExpenseEverySecond(Double.parseDouble(input), savedAmount);
//        Timer timer = new Timer();
//        TimerTask timerTask;
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
                //refresh your textview
//            }
//        };
//        timer.schedule(timerTask, 0, 2000);


        //do heavy work on a background thread
//        stopSelf();
        return START_NOT_STICKY;
    }


    private void deductExpenseEverySecond(double value, String savedAmount) {

        SharedPreferences preferences = getSharedPreferences("pinch.android.earnie",MODE_PRIVATE);
        isSet = preferences.getBoolean("isSet",false);
        SharedPreferences.Editor editor = preferences.edit();

        double remaining = Double.parseDouble(savedAmount) - value;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid()).child("MonthlySavings");

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate2 = df2.format(c);

        String id = UUID.randomUUID().toString();

        HashMap<String, Object> map = new HashMap<>();
        map.put("saved",remaining);
        map.put("month",formattedDate2.split("-")[1]);

        if(isSet){
            reference.child(savingId).updateChildren(map);
        }else {
            editor.putBoolean("isSet",true);
            editor.putString("id",id);
            editor.apply();
            reference.child(id).setValue(map);
        }

    }

    private void getSavedAmount(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid()).child("MonthlySaved");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Date c = Calendar.getInstance().getTime();
                    System.out.println("Current time => " + c);
                    SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    String formattedDate2 = df2.format(c);

                    if(formattedDate2.split("-")[1].equals(dataSnapshot.child("month").getValue().toString())) {
                        savedAmount = snapshot.child("saved").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.i("SERVICESSS","DESTROY");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}