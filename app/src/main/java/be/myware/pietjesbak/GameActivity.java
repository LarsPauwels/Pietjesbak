package be.myware.pietjesbak;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    Button roll, stop, hide, rules, leave;
    ImageView dice1, dice2, dice3;
    TextView myScore, myUsername;

    private int[] dice = {R.drawable.dice1, R.drawable.dice2, R.drawable.dice3, R.drawable.dice4, R.drawable.dice5, R.drawable.dice6};
    private int[] randomNumbers = new int[3];
    private int scoreNumber, min = 0, max = 5, amountPlayed, oldWidth;
    private String scoreText, userID;
    private boolean[] checkZand = {false, false, false};
    private boolean animationCheck = false;
    ValueAnimator slideAnimator;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(GameActivity.this, "Successfully signed in with: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GameActivity.this, "Successfully signed out.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Get XML elements into variables
        roll = (Button) findViewById(R.id.roll);
        stop = (Button) findViewById(R.id.stop);
        hide = (Button) findViewById(R.id.hide);
        rules = (Button) findViewById(R.id.rules);
        leave = (Button) findViewById(R.id.leave);

        dice1 = (ImageView) findViewById(R.id.dice1);
        dice2 = (ImageView) findViewById(R.id.dice2);
        dice3 = (ImageView) findViewById(R.id.dice3);

        myScore = (TextView) findViewById(R.id.myScore);
        myUsername = (TextView) findViewById(R.id.myUsername);

        // When clicked on roll button start rollDice function
        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollDice();
            }
        });

        // When clicked on stop button start stopTurn function
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTurn();
            }
        });

        // When clicked on leave button start leaveGame function
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGame();
            }
        });

        //myUsername.measure(0, 0);
        //myUsername.getMeasuredWidth();
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        leaveGame();

        if (authListener != null) {
            firebaseAuth.removeAuthStateListener(authListener);
        }
    }

    private void rollDice() {
        for (int i = 0; i < 3; i++) {
            Random r = new Random();
            int random = r.nextInt((max - min) + 1) + min;
            randomNumbers[i] = random;
        }

        dice1.setImageResource(dice[randomNumbers[0]]);
        dice2.setImageResource(dice[randomNumbers[1]]);
        dice3.setImageResource(dice[randomNumbers[2]]);

        getScore();
    }

    private void getScore() {
        // Check if numbers are 1, 1, 1 ==> 3 Apen
        if (randomNumbers[0] == 0 && randomNumbers[1] == 0 && randomNumbers[2] == 0) {
            scoreText = "3 Apen";
            // Check if numbers are 4, 4, 4 or 3, 3, 3 (3 the same) ==> Zand
        } else if (randomNumbers[0] == randomNumbers[1] && randomNumbers[0] == randomNumbers[2]) {
            scoreText = "Zand";
        }

        for (int i = 0; i < 3; i++) {
            if (randomNumbers[i] == 3) {
                checkZand[0] = true;
            }

            if (randomNumbers[i] == 4) {
                checkZand[1] = true;
            }

            if (randomNumbers[i] == 5) {
                checkZand[2] = true;
            }
        }

        // Check if numbers are 4, 5 or 6 ==> Soixante-Neuf
        if (checkZand[0] == true && checkZand[1] == true && checkZand[2] == true) {
            scoreText = "Soixante-Neuf";
        }

        // If numbers are nothing from above, then calculate score
        if (scoreText == null) {
            for (int i = 0; i < 3; i++) {
                switch (randomNumbers[i]) {
                    case 0:
                        scoreNumber += 100;
                        break;

                    case 1:
                        scoreNumber += 2;
                        break;

                    case 2:
                        scoreNumber += 3;
                        break;

                    case 3:
                        scoreNumber += 4;
                        break;

                    case 4:
                        scoreNumber += 5;
                        break;

                    case 5:
                        scoreNumber += 60;
                        break;
                }
            }
            // Get numbers score into String score
            scoreText = scoreNumber + " points";
        }

        if (scoreText != null) {
            // Write score away
            myScore.setText(scoreText);

            // Calculate/write away your amount of turns
            amountPlayed++;
            int playsLeft = 3 - amountPlayed;
            Toast.makeText(this, "You have " + playsLeft + " turns left!", Toast.LENGTH_SHORT).show();

            if (amountPlayed >= 1) {
                roll.setText("ROLL AGAIN");
                stop.setBackgroundResource(R.drawable.blue_gradient);
                stop.setEnabled(true);
            }

            if (amountPlayed == 3) {
                stopTurn();
            }

            // Reset variables
            scoreText = null;
            scoreNumber = 0;
            checkZand[0] = false;
            checkZand[1] = false;
            checkZand[2] = false;
        }
    }

    private void animation() {
        if (animationCheck == false) {
            oldWidth = myUsername.getWidth();
        }

        if (animationCheck == false) {
            slideAnimator = ValueAnimator
                    .ofInt(oldWidth, 0)
                    .setDuration(500);
            animationCheck = true;
        } else if (animationCheck == true) {
            slideAnimator = ValueAnimator
                    .ofInt(0, oldWidth)
                    .setDuration(500);
            animationCheck = false;
        }

        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // get the value the interpolator is at
                Integer value = (Integer) animation.getAnimatedValue();
                // I'm going to set the layout's height 1:1 to the tick
                myUsername.getLayoutParams().width = value.intValue();
                // force all layouts to see which ones are affected by
                // this layouts height change
                myUsername.requestLayout();
            }
        });

        AnimatorSet set = new AnimatorSet();
        // since this is the only animation we are going to run we just use
        // play
        set.play(slideAnimator);
        // this is how you set the parabola which controls acceleration
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        // start the animation
        set.start();
    }

    private void stopTurn() {
        // When turn ends disble buttons
        roll.setText("ROLL");
        stop.setBackgroundResource(R.drawable.disabled_button);
        roll.setBackgroundResource(R.drawable.disabled_button);
        stop.setEnabled(false);
        roll.setEnabled(false);
    }

    private void leaveGame() {
        startActivity(new Intent(GameActivity.this,MainActivity.class));
    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            User userInfo = new User();
            userInfo.setEmail(ds.child(userID).getValue(User.class).getEmail());
            userInfo.setUsername(ds.child(userID).getValue(User.class).getUsername());



            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(userInfo.getEmail());
            arrayList.add(userInfo.getUsername());

            String[] array ={userInfo.getEmail(), userInfo.getUsername()};

            myUsername.setText(array[1]);
        }
    }
}