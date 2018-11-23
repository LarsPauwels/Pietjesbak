package be.myware.pietjesbak;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;

public class LoaderActivity extends AppCompatActivity {

    Button signIn, signUp;
    RelativeLayout loader;
    Handler start;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        // Inisialise your firebase
        firebaseAuth = FirebaseAuth.getInstance();

        signIn = (Button) findViewById(R.id.signIn);
        loader = (RelativeLayout) findViewById(R.id.loader);
        signIn = (Button) findViewById(R.id.signIn);
        signUp = (Button) findViewById(R.id.signUp);

        final Handler start = new Handler();
        start.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animation = ObjectAnimator.ofFloat(loader, "translationY", -550f);
                animation.setDuration(1000);
                animation.start();
            }
        }, 2500);

        signIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(LoaderActivity.this,LoginActivity.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(LoaderActivity.this,RegisterActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseAuth.getCurrentUser() != null) {
            // Handle the already login user
            startActivity(new Intent(LoaderActivity.this, MainActivity.class));
        }
    }
}
