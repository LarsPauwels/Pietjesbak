package be.myware.pietjesbak;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Variabels
    private TextView noAccount;
    private EditText tv_email, tv_password;
    private static Button btn_login;
    private static ProgressBar loading;

    boolean isChecked = false;
    FirebaseAuth firebaseAuth;
    String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialise your firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Get view by id into variable
        tv_email = (EditText) findViewById(R.id.email);
        tv_password = (EditText) findViewById(R.id.password);

        noAccount =(TextView) findViewById(R.id.dont_have_account);

        loading = (ProgressBar) findViewById(R.id.loading);

        btn_login = (Button) findViewById(R.id.btn_login);

        // Insert string wih 2 colors
        noAccount.setText(Html.fromHtml("Don't have a account? <font color='#FF9927'><b>Sign Up</b></font>"));

        // Click on "Already have a account? Sign In" ==> go to activity
        noAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // Touch/click on the eye icon ==> set input value to dots or to text
        tv_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (tv_password.getRight() - (tv_password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width() + 8))) {
                        if (!isChecked) {
                            // show password
                            tv_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            isChecked = true;
                        } else {
                            // hide password
                            tv_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            isChecked = false;
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseAuth.getCurrentUser() != null && sessionId == "") {
            // Handle the already login user
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    private void login() {
        final String email = tv_email.getText().toString().trim();
        final String password = tv_password.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            // Email is empty
            Toast.makeText(this, "Please enter an email.", Toast.LENGTH_SHORT).show();
            // Stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(password)) {
            // Password is empty
            Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT).show();
            // Stopping the function execution further
            return;
        }

        // Show loader if validation is ok
        loading.setVisibility(View.VISIBLE);
        btn_login.setVisibility(View.GONE);
        noAccount.setVisibility(View.GONE);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loading.setVisibility(View.GONE);
                btn_login.setVisibility(View.VISIBLE);
                noAccount.setVisibility(View.VISIBLE);

                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
