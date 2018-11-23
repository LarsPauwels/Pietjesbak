package be.myware.pietjesbak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //private Button btn_logout;
    HorizontalScrollView types;
    RelativeLayout container;
    LinearLayout scrollInner;
    com.makeramen.roundedimageview.RoundedImageView first, second, third;
    Button play1;
    ImageView exit;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        //btn_logout = findViewById(R.id.btn_logout);
        play1 = (Button) findViewById(R.id.play1);

        types = (HorizontalScrollView) findViewById(R.id.types);

        container = (RelativeLayout) findViewById(R.id.container);

        scrollInner = (LinearLayout) findViewById(R.id.scrollInner);

        exit = (ImageView) findViewById(R.id.exit);

        first = (com.makeramen.roundedimageview.RoundedImageView) findViewById(R.id.first);
        second = (com.makeramen.roundedimageview.RoundedImageView) findViewById(R.id.second);
        third = (com.makeramen.roundedimageview.RoundedImageView) findViewById(R.id.third);

        /*btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
            }
        });*/

        types.post(new Runnable() {
            @Override
            public void run() {
                int typesWidth = scrollInner.getWidth() /2;
                int containerWidth = container.getWidth() / 2;
                typesWidth = typesWidth - containerWidth;

                types.scrollTo(typesWidth, 0);
            }
        });

        play1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoaderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }

}
