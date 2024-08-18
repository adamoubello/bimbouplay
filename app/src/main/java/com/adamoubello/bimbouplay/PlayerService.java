package com.adamoubello.bimbouplay;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;

public class PlayerService extends Service {

    MediaPlayer mediaPlayer = new MediaPlayer();
    private static final int NOTIFICATION_ID = 200;
    private static final String CHANNEL_ID = "myChannel";
    private static final String CHANNEL_NAME = "myChannelName";

    private final IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }

    public PlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        if (intent.getStringExtra("url") != null) {
            playStream(intent.getStringExtra("url"));
        }
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)){
            Log.i("Info", "Start foreground service");
            //showNotification();
            startForeground();
        }else if(intent.getAction().equals(Constants.ACTION.PREV_ACTION)){
            Log.i("Info", "Prev pressed");
        }else if(intent.getAction().equals(Constants.ACTION.PLAY_ACTION)){
        Log.i("Info", "Play pressed");
        }else if(intent.getAction().equals(Constants.ACTION.NEXT_ACTION)){
            Log.i("Info", "Next pressed");
        }else if(intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)){
            Log.i("Info", "Stop foreground pressed");
            stopForeground(true);
            stopSelf();
        }

        return START_REDELIVER_INTENT;
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, MainActivity.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getActivity(
                this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, MainActivity.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getActivity(
                this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, MainActivity.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getActivity(
                this, 0, nextIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.penny);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Music player")
                .setTicker("Playing music")
                .setContentText("My song")
                .setSmallIcon(R.drawable.penny)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_previous, "Previous", ppreviousIntent)
                .addAction(android.R.drawable.ic_media_play, "Play", pplayIntent)
                .addAction(android.R.drawable.ic_media_next, "Next", pnextIntent)
                .build();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

    private void startForeground() {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, MainActivity.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getActivity(
                this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, MainActivity.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getActivity(
                this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, MainActivity.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getActivity(
                this, 0, nextIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.penny);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext(), CHANNEL_ID);

        Notification notification;

//        notification = mBuilder.setTicker(getString(R.string.app_name)).setWhen(0)
//                .setOngoing(true)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText("Send SMS gateway is running background")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setShowWhen(true)
//                .build();

        notification = mBuilder.setTicker(getString(R.string.app_name)).setWhen(0)
                //.setContentTitle(getString(R.string.app_name))
                //.setShowWhen(true)

                .setContentTitle("Music player")
                .setTicker("Playing music")
                .setContentText("My song")
                .setSmallIcon(R.drawable.penny)
                .setLargeIcon(Bitmap.createScaledBitmap(icon,
                        128, 128, false))
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_previous,
                        "Previous", ppreviousIntent)
                .addAction(android.R.drawable.ic_media_play, "Play", pplayIntent)
                .addAction(android.R.drawable.ic_media_next, "Next", pnextIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getApplication()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        //All notifications should go through NotificationChannel on Android 26 & above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

        }
        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    public void playStream(String url){
        if (mediaPlayer != null) {
            try{
                mediaPlayer.stop();
            } catch (Exception e){

            }
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    //mediaPlayer.start();
                    playPlayer();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    flipPlayPauseButton(false);
                }
            });
            mediaPlayer.prepareAsync();
            mediaPlayer.start();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void pausePlayer() {
        try {
            mediaPlayer.pause();
            flipPlayPauseButton(false);
        } catch (Exception e){
            Log.d("EXCEPTION PAUSE", "Failed to pause media player");
        }
    }

    public void playPlayer() {
        try {
            mediaPlayer.start();
            flipPlayPauseButton(true);
        } catch (Exception e){
            Log.d("EXCEPTION PLAY", "Failed to play media player");
        }
    }

    public void togglePlayer(){
        try {
            if (mediaPlayer.isPlaying()){
                pausePlayer();
            }else {
                playPlayer();
            }
        } catch (Exception e){
            Log.d("EXCEPTION TOGGLE", "Failed to toggle media player");
        }
    }

    public void flipPlayPauseButton(boolean isPlaying){
        //Communication with main thread
        Intent intent = new Intent("changePlayButton");
        intent.putExtra("isPlaying", isPlaying);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}