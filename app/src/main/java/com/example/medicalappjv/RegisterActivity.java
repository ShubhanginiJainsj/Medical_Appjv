package com.example.medicalappjv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText username, email, password;
    Button btn_register;
    FirebaseAuth auth;
    DatabaseReference reference;
    TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password =(EditText) findViewById(R.id.password);
        btn_register = (Button) findViewById(R.id.regbtn);
        login = (TextView) findViewById(R.id.login);
        auth = FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       Intent intent = new Intent(getApplicationContext(), LoginaActivity.class);
                                       startActivity(intent);
                                   }
                               }

        );
        if(auth.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_username) || (TextUtils.isEmpty(txt_email)) || (TextUtils.isEmpty(txt_password))) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 character", Toast.LENGTH_SHORT).show();
                } else {
                    register(txt_username, txt_email, txt_password);
                }
            }
        });

    }
    private  void register(final String username, String email, String password)
    {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    String userid = firebaseUser.getUid();
                                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("id", userid);
                                    hashMap.put("username", username);

                                    hashMap.put("status", "offline");
                                    hashMap.put("search", username.toLowerCase());
                                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(RegisterActivity.this , "You can't have email or password for registration", Toast.LENGTH_SHORT);
                                }
                            }
                        }
                );
    }    }
