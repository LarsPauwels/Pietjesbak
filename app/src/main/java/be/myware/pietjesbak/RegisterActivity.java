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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    // Variabels
    TextView noAccount;
    EditText tv_name, tv_email, tv_password;
    Button btn_register;
    ProgressBar loading;

    private FirebaseAuth firebaseAuth;
    boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialise your firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Get view by id into variable
        noAccount = (TextView) findViewById(R.id.dont_have_account);

        btn_register = (Button) findViewById(R.id.btn_register);

        tv_name = (EditText) findViewById(R.id.name);
        tv_email = (EditText) findViewById(R.id.email);
        tv_password = (EditText) findViewById(R.id.password);

        loading = (ProgressBar) findViewById(R.id.loading);

        // Insert string wih 2 colors
        noAccount.setText(Html.fromHtml("Already have a account? <font color='#FF9927'><b>Sign In</b></font>"));

        // Click on "Already have a account? Sign In" ==> go to activity
        noAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        // Click on register button ==> start function "register"
        btn_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                register();
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

        if (firebaseAuth.getCurrentUser() != null) {
            // Handle the already login user
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        }
    }

    private void register() {
        final String username = tv_name.getText().toString().trim();
        final String email = tv_email.getText().toString().trim();
        final String password = tv_password.getText().toString().trim();
        final int wins = 0;
        final int plays = 0;

        if (TextUtils.isEmpty(username)) {
            // Username is empty
            Toast.makeText(this, "Please enter an username.", Toast.LENGTH_SHORT).show();
            // Stopping the function execution further
            return;
        }

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
        btn_register.setVisibility(View.GONE);
        noAccount.setVisibility(View.GONE);

        // Check if username already exists
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    loading.setVisibility(View.GONE);
                    btn_register.setVisibility(View.VISIBLE);
                    noAccount.setVisibility(View.VISIBLE);

                    // Username already Exists
                    Toast.makeText(getApplicationContext(), "Username already exists. Please try other username.", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if email already exists
                    Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("email").equalTo(email);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                loading.setVisibility(View.GONE);
                                btn_register.setVisibility(View.VISIBLE);
                                noAccount.setVisibility(View.VISIBLE);

                                // Email already Exists
                                Toast.makeText(getApplicationContext(), "Email already exists. Please try other email.", Toast.LENGTH_SHORT).show();
                            } else {
                                // create user in firebase
                                firebaseAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // User is successfully registerd and logged in
                                                    User user = new User(
                                                            username,
                                                            email,
                                                            wins,
                                                            plays
                                                    );

                                                    FirebaseDatabase.getInstance().getReference("Users")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            loading.setVisibility(View.GONE);
                                                            btn_register.setVisibility(View.VISIBLE);
                                                            noAccount.setVisibility(View.VISIBLE);

                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(RegisterActivity.this, "You are successfully registerd.", Toast.LENGTH_SHORT).show();

                                                                String sessionId = getIntent().getStringExtra("EXTRA_SESSION_ID");
                                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                                intent.putExtra("EXTRA_SESSION_ID", sessionId);
                                                                startActivity(intent);
                                                            } else {
                                                                Toast.makeText(RegisterActivity.this, "Could not register... please try again.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    loading.setVisibility(View.GONE);
                                                    btn_register.setVisibility(View.VISIBLE);
                                                    noAccount.setVisibility(View.VISIBLE);
                                                    Toast.makeText(RegisterActivity.this, "Could not register... please try again.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    //function register
        /*
        ==> Get value of inputfields
        ==> Send values as post to link
        ==> Try to get array back and send message when is succes
        ==> When fails give error message/indicate input field
        */
    /*private void Register() {
        loading.setVisibility(View.VISIBLE);
        btn_register.setVisibility(View.GONE);
        noAccount.setVisibility(View.GONE);

        final String name = this.tv_name.getText().toString().trim();
        final String email = this.tv_email.getText().toString().trim();
        final String password = this.tv_password.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String succes = jsonObject.getString("succes");
                            String message = jsonObject.getString("message");

                            if (succes.equals("1")) {
                                String user = jsonObject.getString("user");
                                Toast.makeText(RegisterActivity.this, message + " " + user + "!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

                                loading.setVisibility(View.GONE);
                                btn_register.setVisibility(View.VISIBLE);
                                noAccount.setVisibility(View.VISIBLE);
                                tv_email.setBackgroundResource(R.drawable.edit_text);
                                tv_name.setBackgroundResource(R.drawable.edit_text);
                                tv_password.setBackgroundResource(R.drawable.edit_text);
                                tv_email.getText().clear();
                                tv_name.getText().clear();
                                tv_password.getText().clear();
                            } else if (succes.equals("0")) {
                                String error = jsonObject.getString("error");
                                Toast.makeText(RegisterActivity.this, message + "", Toast.LENGTH_SHORT).show();
                                tv_email.setBackgroundResource(R.drawable.edit_text);
                                tv_name.setBackgroundResource(R.drawable.edit_text);
                                tv_password.setBackgroundResource(R.drawable.edit_text);

                                switch(error) {
                                    case "email" :
                                        tv_email.setBackgroundResource(R.drawable.edit_text_error);
                                        break;

                                    case "username" :
                                        tv_name.setBackgroundResource(R.drawable.edit_text_error);
                                        break;

                                    case "password" :
                                        tv_name.setBackgroundResource(R.drawable.edit_text_error);
                                        break;
                                }

                                loading.setVisibility(View.GONE);
                                btn_register.setVisibility(View.VISIBLE);
                                noAccount.setVisibility(View.VISIBLE);
                            }

                        } catch(JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Register Error!" + e.toString(), Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            btn_register.setVisibility(View.VISIBLE);
                            noAccount.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "Register Error!" + error.toString(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_register.setVisibility(View.VISIBLE);
                        noAccount.setVisibility(View.VISIBLE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }*/

    /*public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isNameValid(String name) {
        boolean specialChar = name.matches("[a-zA-Z.? ]*");
        String[] separated = name.split(" ");


        if (separated.length > 1 && specialChar == true) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isPasswordValid(String password) {
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean isAtLeast8 = password.length() >= 8;//Checks for at least 8 characters
        boolean hasSpecial = !password.matches("[A-Za-z0-9 ]*");//Checks at least one char is not alpha numeric
        boolean noConditions = !(password.contains("AND") || password.contains("NOT"));//Check that it doesn't contain AND or NOT

        if (hasUppercase && hasLowercase && isAtLeast8 && hasSpecial && noConditions) {
            return true;
        } else {
            return false;
        }
    }*/
}
