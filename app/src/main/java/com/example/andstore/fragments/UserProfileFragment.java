package com.example.andstore.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andstore.R;
import com.example.andstore.activities.LoginActivity;
import com.example.andstore.models.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileFragment extends Fragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserInfo currentUser = new UserInfo();
    AppCompatButton editProfileButton;
    EditText fullNameForm;
    EditText phoneForm;
    EditText addressForm;
    LinearLayout logoutButton;
    TextView usernameText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile_fragment_layout, container, false);

        // Init views
        editProfileButton = (AppCompatButton) view.findViewById(R.id.edit_profile_button);
        fullNameForm = (EditText) view.findViewById(R.id.full_name_form);
        phoneForm = (EditText) view.findViewById(R.id.phone_form);
        addressForm = (EditText) view.findViewById(R.id.address_form);
        usernameText = (TextView) view.findViewById(R.id.username_text);
        logoutButton = (LinearLayout) view.findViewById(R.id.logout_button);

        // Try to load current user info by userId to put in forms
        db.collection(getString(R.string.user_collection)).document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentUser = task.getResult().toObject(UserInfo.class);
                if (currentUser == null) {
                    Toast.makeText(getActivity(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                    return;
                }
                fullNameForm.setText(currentUser.getFullName());
                phoneForm.setText(currentUser.getPhoneNumber());
                addressForm.setText(currentUser.getAddress());
            } else {
                Toast.makeText(getActivity(), "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });

        // Edit profile button
        editProfileButton.setOnClickListener(v -> {
            // If form is empty then notify error
            if (fullNameForm.getText().toString().isEmpty()) {
                fullNameForm.setError("Full name is required");
                return;
            }

            if (phoneForm.getText().toString().isEmpty()) {
                phoneForm.setError("Phone is required");
                return;
            }

            if (addressForm.getText().toString().isEmpty()) {
                addressForm.setError("Address is required");
                return;
            }

            // Check if phone number is in correct format
            if (!phoneForm.getText().toString().matches("^0\\d{9}$")) {
                phoneForm.setError("Wrong phone number format");
                return;
            }

            // Save user data
            db.collection(getString(R.string.user_collection)).document(mAuth.getCurrentUser().getUid()).update(
                    "fullName", fullNameForm.getText().toString(),
                    "phoneNumber", phoneForm.getText().toString(),
                    "address", addressForm.getText().toString()
            );

            // Notify user with toast that update is successful
            Toast.makeText(getActivity(), "Profile updated", Toast.LENGTH_SHORT).show();
        });

        // Change username text
        usernameText.setText(mAuth.getCurrentUser().getEmail());

        // Logout button
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        });

        return view;
    }
}