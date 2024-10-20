package com.hackathon.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.stereotype.Service;

@Service
public class FirebaseUserService {

    public String createUser(String email, String password) {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password);
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            return userRecord.getUid();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String loginUser(String email, String password) {
        try {
            // Firebase doesn't directly handle password-based login from backend.
            // Instead, you simulate token generation as if a front-end client logged in.
            // For example, manually generating a session/token using custom claims.
            String customToken = FirebaseAuth.getInstance().createCustomToken(email);
            return customToken;
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return null;
        }
    }
}
