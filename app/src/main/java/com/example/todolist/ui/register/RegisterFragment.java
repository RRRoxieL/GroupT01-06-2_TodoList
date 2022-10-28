package com.example.todolist.ui.register;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.todolist.R;

import com.example.todolist.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterFragment extends Fragment {

    private View root;
    EditText editTextUsername;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextConfirmPassword;
    RadioGroup radioGroup;
    Button signupBtn;

    private FirebaseAuth mAuth;

    String gender = "";
// ...
// Initialize Firebase Auth


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_register, container, false);
        }

        //auth components
        mAuth = FirebaseAuth.getInstance();

        editTextUsername = root.findViewById(R.id.register_username);
        editTextEmail = root.findViewById(R.id.register_email);
        editTextPassword = root.findViewById(R.id.register_password);
        editTextConfirmPassword = root.findViewById(R.id.register_passwordCheck);

        radioGroup = root.findViewById(R.id.register_radiogroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch(checkedId) {
                    case R.id.register_male:
                        gender = "male";
                        break;
                    case R.id.register_female:
                        gender = "female";
                        break;
                }
            }
        });

        signupBtn = root.findViewById(R.id.btn_signup);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                isValid(view);
            }
        });
        return root;
    }



    private void isValid(View view){
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirm_password = editTextConfirmPassword.getText().toString().trim();

        if(username.isEmpty()){
            editTextUsername.setError("Username is required!");
            editTextUsername.requestFocus();
            return;
        }

        if(email.isEmpty()){
            editTextEmail.setError("email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            editTextPassword.setError("at least 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        if(confirm_password.isEmpty()){
            editTextConfirmPassword.setError("confirm password is required!");
            editTextConfirmPassword.requestFocus();
            return;
        }

        if(!password.equals(confirm_password)){
            editTextConfirmPassword.setError("password are not equals!");
            editTextConfirmPassword.requestFocus();
            return;
        }

        //add to firebase
        mAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    User user = new User(email, password, username, gender);

                    FirebaseDatabase.getInstance().getReference("Users").
                            child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                            setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        NavController controller = Navigation.findNavController(view);
                                        controller.navigate(R.id.action_registerFragment_to_loginFragment);
                                    }else{

                                    }
                                }
                            });
                }
            }
        });

    }
}