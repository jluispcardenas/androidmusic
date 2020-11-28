package club.codeexpert.musica.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;


import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import club.codeexpert.musica.R;
import club.codeexpert.musica.data.db.Song;
import club.codeexpert.musica.managers.ApiManager;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    //media player
    private MediaPlayer player;

    private ArrayList<Song> songs;

    private int songPosn;

    //binder
    private final IBinder musicBind = new MusicBinder();

    private String songTitle = "";
    private String songAlbumart = "";
    private String songDuration = "";

    private boolean ispaused = false;
    private Random rand;

    Notification mNotification;
    static RemoteViews mRemoteViews;
    public static int currentPos = 0;
    NotificationManager mNotifiManager;
    public static final int NOTIFICATION_IDFOREGROUND_SERVICE = 78945;
    public static final String ACTION_STOP="com.yourapp.ACTION_STOP";
    public static final String ACTION_PLAY="com.yourapp.ACTION_PLAY";
    public static final String ACTION_PAUSE="com.yourapp.ACTION_PAUSE";
    public static final String ACTION_RESUME="com.yourapp.ACTION_RESUME";
    public static final String ACTION_NEXT="com.yourapp.ACTION_NEXT";
    public static final String ACTION_PREV="com.yourapp.ACTION_PREV";
    int REQUEST_CODE_STOP = 1111;
    int REQUEST_CODE_PAUSE = 2222;
    int REQUEST_CODE_RESUME = 3333;
    int REQUEST_CODE_NEXT = 4444;
    int REQUEST_CODE_PREV = 6666;
    int REQUEST_CODE_ACTVITY = 5555;

    @Override
    public  void onCreate() {
        super.onCreate();

        mNotifiManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        songPosn = 0;

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    //pass song list
    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;

    }

    public void appendItem(Song theSong) {
        songs.add(theSong);
    }

    //binder
    public class MusicBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    //activity will bind to service
    public IBinder onBind(Intent arg0) {
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent){
        //player.stop();
        //player.release();
        return false;
    }

    //set the song
    public void setSong(int songIndex){
        songPosn = songIndex;
    }

    public void playSong() {
        //play
        player.reset();

        //get song
        final Song playSong = songs.get(songPosn);
        if (!ApiManager.getInstance().isDownloaded(playSong.id)) {
            final String songId = playSong.id;
            ApiManager.getInstance().call("play/" + songId, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.has("url")) {
                            String play_url = response.getString("url");
                            if (!play_url.equals("")) {
                                playNow(playSong.title, playSong.duration, play_url);
                            } else {
                                Log.d("APP", String.valueOf(R.string.file_pending));
                            }
                        } else {
                            Log.d("APP", String.valueOf(R.string.file_invalid));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            String play_url = playSong.id + ".mp3";
            playNow(playSong.title, playSong.duration, play_url);
        }
    }

    private void playNow(String title, String duration, String link) {
        songTitle = title;
        songDuration = duration;
        songAlbumart = "";
        try {
            if (link.indexOf("http") != -1) {
                Log.d("APP", "Playing: " + link);
                player.setDataSource(link);
            } else {
                File file =  new File(ApiManager.getInstance().getDir(), link);
                Log.d("APP", "Playing: " + file.getAbsolutePath());
                player.setDataSource(file.getAbsolutePath());
            }
        } catch(Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();

        //notification
        //showControllerInNotification(songTitle, songDuration,songAlbumart);
    }

    //playback methods
    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDuration(){
        return player.getDuration();
    }

    public boolean isPlaying(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void stopPlayer(){
        player.stop();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void start(){
        player.start();
    }

    //skip to previous track
    public void playPrev(){
        songPosn--;
        if (songPosn < 0) songPosn=songs.size()-1;

        playSong();
    }

    public void playNext(){
        songPosn++;
        if (songPosn >= songs.size()) songPosn = 0;

        playSong();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            if (!TextUtils.isEmpty(action)) {
                if (action.equals(ACTION_PLAY)) {
                    ispaused = false;
                    playSong();

                } else if (action.equals(ACTION_PAUSE)) {
                    pausePlayer();
                    currentPos = getPosn();
                    ispaused = true;


                } else if (action.equals(ACTION_RESUME)) {
                    seek(currentPos);

                    ispaused = false;
                    start();
                } else if (action.equals(ACTION_STOP)) {
                    ispaused = false;
                    player.stop();
                    mNotifiManager.cancel(NOTIFICATION_IDFOREGROUND_SERVICE);
                } else if (action.equals(ACTION_NEXT)) {
                    ispaused = false;
                    playNext();
                } else if (action.equals(ACTION_PREV)) {
                    ispaused = false;
                    playPrev();
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //player.stop();
        stopForeground(true);
    }

    @Override
    public void onLowMemory() {

    }

    /*private void showControllerInNotification(String title,String duration,String songAlbumart) {
        PendingIntent pendingIntent = null;
        Intent intent = null;

        //Inflate a remote view with a layout which you want to display in the notification bar.
        if (mRemoteViews == null) {
            mRemoteViews = new RemoteViews(getPackageName(),
                    R.layout.status_bar);
        }

        if (songAlbumart=="")
            mRemoteViews.setImageViewResource(R.id.status_bar_album_art,R.drawable.musicplayericon);
        else {
            //Drawable image = Drawable.createFromPath(songAlbumart);
            Bitmap bitmap = BitmapFactory.decodeFile(songAlbumart);
            mRemoteViews.setImageViewBitmap(R.id.status_bar_album_art, bitmap);
            //mRemoteViews.setImageViewResource(R.id.status_bar_album_art, image);
        }
        mRemoteViews.setTextViewText(R.id.status_bar_track_name, title);

        float dua= Float.valueOf(duration);
        dua=dua/60000;

        //mRemoteViews.setTextViewText(R.id.status_bar_progress_music, String.format("%.2f", dua));
        mRemoteViews.setImageViewResource(R.id.status_bar_playpause, R.drawable.apollo_holo_dark_pause);
        mRemoteViews.setImageViewResource(R.id.status_bar_next, R.drawable.apollo_holo_dark_next);

        //Define what you want to do after clicked the button in notification.
        //Here we launcher a service by an action named "ACTION_STOP" which will stop the music play.
        intent = new Intent(ACTION_STOP);
        pendingIntent = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_STOP, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.status_bar_collapse,
                pendingIntent);

        //mRemoteViews.setBackgroundColor(0x0000FF00);
        Intent intentpause = new Intent(ACTION_PAUSE);
        PendingIntent pendingIntentpause = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_PAUSE, intentpause,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.status_bar_playpause,
                pendingIntentpause);

        Intent intentnext = new Intent(ACTION_NEXT);
        PendingIntent pendingIntentnext = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_NEXT, intentnext,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.status_bar_next,
                pendingIntentnext);

        Intent intentprev = new Intent(ACTION_PREV);
        PendingIntent pendingIntentprev = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_PREV, intentprev,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.status_bar_prev,
                pendingIntentprev);


        Intent intentactivity=new Intent(getApplicationContext(), ListMusic.class);
        PendingIntent pendingIntentActivity = PendingIntent.getActivity(getApplicationContext(),
                REQUEST_CODE_ACTVITY, intentactivity,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //Create the notification instance.
        mNotification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.notificationimage)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntentActivity)
                .setContent(mRemoteViews)
                .build();


        mNotifiManager.notify(NOTIFICATION_IDFOREGROUND_SERVICE, mNotification);
    }*/


    @Override
    public void onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of a track
        if (player.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
            Log.e("MUSIC PLAYER", "Staring Next Song:" + songTitle);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("MUSIC PLAYER", "Playback Error");
        mp.reset();
        return false;
    }
}