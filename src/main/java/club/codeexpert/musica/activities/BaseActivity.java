package club.codeexpert.musica.activities;


import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected boolean checkAuth() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(createIntent(this, SignedInActivity.class));
            finish();
            return false;
        }

        return true;
    }

    public static Intent createIntent(Context context, Class oClass)  {
        Intent in = new Intent();
        in.setClass(context, oClass);
        return in;
    }
}
