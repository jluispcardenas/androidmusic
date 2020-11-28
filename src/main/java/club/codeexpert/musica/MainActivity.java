package club.codeexpert.musica;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import club.codeexpert.musica.activities.BaseActivity;
import club.codeexpert.musica.data.SongRepository;
import club.codeexpert.musica.data.db.DbFactory;
import club.codeexpert.musica.services.PlayerService;
import club.codeexpert.musica.managers.ApiManager;

public class MainActivity extends BaseActivity {
    static public PlayerService mService;
    public boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if (!checkAuth()) return;

        setContentView(R.layout.activity_main);

        //FirebaseAuth auth = FirebaseAuth.getInstance();
        //FirebaseUser current = auth.getCurrentUser();

        /*TextView txt = (TextView)findViewById(R.id.welcome);
        txt.setText(current.getDisplayName());

        ImageView picture = (ImageView)findViewById(R.id.picture);
        if (current.getPhotoUrl() != null)
            Picasso.with(this).load(current.getPhotoUrl()); //.transform(new RoundedTransformation(30, 0)).into(picture);
        */

        ApiManager.getInstance(getApplicationContext());
        SongRepository.getInstance(getApplicationContext());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_discover, R.id.navigation_downloads, R.id.navigation_notifications)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            PlayerService.MusicBinder binder = (PlayerService.MusicBinder)service;
            mService = binder.getService();

            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}