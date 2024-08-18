package com.adamoubello.bimbouplay;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.adamoubello.bimbouplay.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.widget.AdapterView;
import android.widget.ImageSwitcher;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private static ActivityMainBinding binding;
    static FloatingActionButton playPauseButton;
    PlayerService mBoundService;
    boolean mServiceBound = false;
    List<Song> songs = new ArrayList<>();
    ListView songsListView;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            PlayerService.MyBinder myBinder = (PlayerService.MyBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBound = false;
        }
    };

    private BroadcastReceiver mMessagereceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPlaying = intent.getBooleanExtra("isPlaying", false);
            flipPlayPauseButton(isPlaying);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        /*NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);*/

        //playPauseButton = findViewById(R.id.fab);
        binding.fab.setOnClickListener(new View.OnClickListener() {
        //playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mServiceBound){
                    mBoundService.togglePlayer();
                }
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                //Player.player.togglePlayer();
            }
        });

        //startStreamingService("http://musicapp.adamoubello.com/romantic.mp3");
        songsListView = findViewById(R.id.SongsListView);
        fetchSongsFromWeb();

//      String url = "http://musicapp.adamoubello.com/romantic.mp3";
        /*if (Player.player == null){
            new Player();
        }
        Player.player.playStream(url);*/

//        MediaPlayer mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//        try {
//            mediaPlayer.setDataSource(url);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void startStreamingService(String url){
        Intent i = new Intent(this, PlayerService.class);
        i.putExtra("url", url);
        i.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(i);
        bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceBound){
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    public static void flipPlayPauseButton(boolean isPlaying){
        if (isPlaying){
            //playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            binding.fab.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            //playPauseButton.setImageResource(android.R.drawable.ic_media_play);
            binding.fab.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessagereceiver
                , new IntentFilter("changePlayButton"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessagereceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }*/

    private void fetchSongsFromWeb() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                InputStream inputStream;

                try {
                    URL url = new URL("http://musicapp.adamoubello.com/getmusic.php");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    int statusCode = urlConnection.getResponseCode();
                    Log.i("urlConnection: ", String.valueOf(urlConnection));
                    if(statusCode == 200){
                        inputStream = new BufferedInputStream(urlConnection.getInputStream());
                        String response = convertInputStreamToString(inputStream);
                        Log.i("Got SONGS!", response);
                        parseIntoSongs(response);
                    }

                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null){
                       urlConnection.disconnect();
                    }
                }

            }
        });
        thread.start();
    }

    private void parseIntoSongs(String data) {
        String[] dataArray = data.split("\\*");
        int i = 0;

        for (i=0; i < dataArray.length; i++){
            String[] songArray = dataArray[i].split(",");
            Song song = new Song(songArray[0], songArray[1], songArray[2], songArray[3]);
            songs.add(song);
        }

        for (i=0; i < songs.size(); i++){
            Log.i("GOT SONG : ", songs.get(i).getTitle());
        }
        populateSongsListView();
    }

    private void populateSongsListView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SongListAdapter adapter = new SongListAdapter(MainActivity.this, songs);
                songsListView.setAdapter(adapter);
                songsListView.setAdapter(adapter);
                songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Song song = songs.get(position);
                        String songAddress = "http://musicapp.adamoubello.com/" + song.getTitle();
                        startStreamingService(songAddress);
                        markSongplayed(song.getId());
                        askForLikes(song);
                    }
                });
            }
        });
    }

    private void askForLikes(Song song) {
       new AlertDialog.Builder(this)
               .setTitle(song.getTitle())
               .setMessage("Do you like this song?")
               .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       likeSong(song.getId());
                   }
               })
               .setNegativeButton("NO :(", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {

                   }
               })
               .setIcon(android.R.drawable.ic_dialog_alert)
               .show();
    }

    private void likeSong(int chosenId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;

                try {
                    URL url = new URL("http://musicapp.adamoubello.com/add_like.php?id="
                            + chosenId);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.getResponseCode();
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null){
                        urlConnection.disconnect();
                    }
                }
            }
        });
        thread.start();
    }

    private void markSongplayed(int chosenId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                HttpURLConnection urlConnection = null;

                try {
                    URL url = new URL("http://musicapp.adamoubello.com/add_play.php?id="
                            + Integer.toString(chosenId));
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    int statusCode = urlConnection.getResponseCode();

                    if (statusCode == 200){
                        inputStream = new BufferedInputStream(urlConnection.getInputStream());
                        String response = convertInputStreamToString(inputStream);
                        Log.i("PLAYED SONG ID", response);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null){
                        urlConnection.disconnect();
                    }
                }
            }
        });
        thread.start();
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder result = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null){
            result.append(line);
        }
        if (inputStream != null){
            inputStream.close();
        }

        return result.toString();
    }
}