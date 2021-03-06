package com.example.travelbook;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase sFirebaseDatabase;
    public static DatabaseReference sDatabaseReference;
    public static FirebaseStorage sFirebaseStorage;
    public static StorageReference sStorageReference;
    public static ArrayList<TravelDeal> sTravelDealList;
    private static FirebaseUtil sFirebaseUtil;
    public static FirebaseAuth sFirebaseAuth;
    public static FirebaseAuth.AuthStateListener sAuthStateListener;
    private static ListActivity sCallerActivity;

    public static boolean isAdminUser;
    private static final int RC_SIGN_IN = 123;

    private FirebaseUtil(){}

    public static void  openFBReference(String ref,final ListActivity callerActivity){
        if(sFirebaseUtil==null) {
            sFirebaseUtil=new FirebaseUtil();
            sFirebaseDatabase = FirebaseDatabase.getInstance();
            sFirebaseAuth=FirebaseAuth.getInstance();
            sCallerActivity=callerActivity;
            sAuthStateListener=new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth pFirebaseAuth) {
                        if(pFirebaseAuth.getCurrentUser()==null)
                        {
                            SignIn();
                        }
                        else
                        {
                            String UserId= sFirebaseAuth.getUid();
                            checkAdminAccess(UserId);
                        }
                }
            };


        }
        sDatabaseReference=sFirebaseDatabase.getReference().child(ref);
        sTravelDealList=new ArrayList<TravelDeal>();
       connectStorage();

    }

    private static void checkAdminAccess(String pUserId) {
        FirebaseUtil.isAdminUser=false;
        DatabaseReference localDatabaseReference=sFirebaseDatabase.getReference().child("administrators").child(pUserId);
        ChildEventListener localEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot pDataSnapshot, @Nullable String pS) {
                FirebaseUtil.isAdminUser=true;
                sCallerActivity.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot pDataSnapshot, @Nullable String pS) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot pDataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot pDataSnapshot, @Nullable String pS) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError pDatabaseError) {

            }
        };
        localDatabaseReference.addChildEventListener(localEventListener);
    }

    private static void SignIn()
    {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
               // new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
                //new AuthUI.IdpConfig.FacebookBuilder().build(),
                //new AuthUI.IdpConfig.TwitterBuilder().build()
                );

// Create and launch sign-in intent
        sCallerActivity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attachAuthListener()
    {
      sFirebaseAuth.addAuthStateListener(sAuthStateListener);
    }
    public static void detachAuthListener()
    {
        sFirebaseAuth.removeAuthStateListener(sAuthStateListener);
    }

    private static void connectStorage(){
        sFirebaseStorage=FirebaseStorage.getInstance();
        sStorageReference=sFirebaseStorage.getReference().child("deals_pictures");

    }
}
