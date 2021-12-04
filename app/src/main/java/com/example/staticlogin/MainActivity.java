package com.example.staticlogin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

// CUSTOM LOCAL DATABASE
// APPLIED ENCAPSULATION
class AccountStorage{
    // USER OBJECT THAT CONTAINS USERNAME AND PASSWORD AND A COMPARE PASSWORD METHOD FOR VALIDATION
    static class User{
        private String username;
        private String password;

        public void setUsername(String username){
            this.username = username;
        }
        public void setPassword(String password) {
            this.password = password;
        }
        public boolean comparePassword(@NonNull String password){
            if(password.isEmpty()) return false;
            return this.password.equals(password);
        }
    }
    // OUR ARRAY HOLDER
    private final static ArrayList<User> LocalStorage = new ArrayList<>();

    public void addUser(String username, String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        LocalStorage.add(user);
    }

    public User findUser(String username){
        for (User user : LocalStorage) {
            if(username.equals(user.username)) {
                return user;
            }
        }
        return null;
    }

}
public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "StaticLogin_Notification_Welcome";

    // DECLAARE VIEWS
    Button loginBtn;
    EditText usernameET;
    EditText passwordET;

    // DEFAULT TOAST
    public void displayToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        CharSequence name = "Welcome Message";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        // OUR LOCAL DATABASE
        AccountStorage accountStorage = new AccountStorage();
        accountStorage.addUser("johndoe", "12345678");

        // INITIALIZE VIEWS
        loginBtn = findViewById(R.id.login);
        usernameET = findViewById(R.id.username);
        passwordET = findViewById(R.id.password);

        // ON CLICK FUNCTION, THIS WILL ONLY FIRE IF THE LOGIN BUTTON WAS CLICKED
        loginBtn.setOnClickListener(view -> {
            String username = usernameET.getText().toString(),
                    password = passwordET.getText().toString();

        // IF USERNAME AND THE PASSWORD DOESN'T EXIST IN OUR EDITTEXTS
            if(username.isEmpty()) displayToast("Username is required!");
            else if(password.isEmpty()) displayToast("Password is required!");
            else {
                // IF THE TWO EXISTS EXISTS
                // WE USED TRY-CATCH HERE TO HANDLE NULL IF THE USER ISN'T FOUND IN OUR LOCAL DATABASE
                try {

                    // FIND USER WITH THE SAME USERNAME
                    AccountStorage.User user = accountStorage.findUser(username);

                    // IF FOUND
                    if (user.comparePassword(password)) {
                        welcome(); // DISPLAY CUSTOM TOAST
                        notifyLogin(); // DISPLAY NOTIFICATION
                        Intent homeIntent = new Intent(getApplicationContext(), Home.class);
                        startActivity(homeIntent); // REDIRECT TO HOME
                        finish(); // CLOSE THE CURRENT ACTIVITY (LOGIN)
                    }
                    // IF NOT FOUND AN ERROR WILL BE CAUGHT AND WILL DISPLAY OUR TOAST
                } catch (NullPointerException npe) {
                    displayToast("Username or Password is incorrect.");
                }
            }
        });
    }

    // DISPLAY CUSTOM WELCOME TOAST
    public void welcome(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.welcome_toast, null);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, 10);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    // OUR NOTIFICATION WHEN LOGGED IN
    public void notifyLogin(){
        Intent intent = new Intent(this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification.Builder notificationBuilder = new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Successful log in.")
                .setContentText("Welcome to Mobile Programming!")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        int NOTIFICATION_ID = generateNotificationID();
        NotificationManagerCompat notificationManagerComp = NotificationManagerCompat.from(this);
        notificationManagerComp.notify(NOTIFICATION_ID, notificationBuilder.build());

    }
    // ID GENERATOR FOR NOTIFICATION
    public int generateNotificationID(){
        return (int) Math.floor(Math.random() * 9999);
    }
}