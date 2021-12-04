package com.example.staticlogin;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationBuilderWithBuilderAccessor;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.zip.Inflater;

class AccountStorage{
    static class User{
        private String username;
        private String password;

        public void setUsername(String username){
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public boolean comparePassword(String password){
            if(password.isEmpty()) return false;
            return this.password.equals(password);
        }
    }
    private static ArrayList<User> LocalStorage = new ArrayList<>();

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
    HashMap<String, String> accountStorage;

    Button loginBtn;
    EditText usernameET;
    EditText passwordET;
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

        AccountStorage accountStorage = new AccountStorage();
        accountStorage.addUser("johndoe", "12345678");

//      INITIALIZE VIEWS
        loginBtn = findViewById(R.id.login);
        usernameET = findViewById(R.id.username);
        passwordET = findViewById(R.id.password);

//       ON CLICK FUNCTION
        loginBtn.setOnClickListener(view -> {
            String username = usernameET.getText().toString(),
                    password = passwordET.getText().toString();

//              IF USERNAME AND THE PASSWORD DOESN'T EXIST IN OUT EDITTEXTS
            if(username.isEmpty()){
                displayToast("Username is required!");
            }
            if(password.isEmpty()){
                displayToast("Password is required!");
            }
//              IF THE TWO EXISTS EXISTS

            try {
                AccountStorage.User user = accountStorage.findUser(username);

                if(user.comparePassword(password)){
                    welcome();
                    notifyLogin();
                    Intent homeIntent = new Intent(getApplicationContext(), Home.class);
                    startActivity(homeIntent);
                    finish();
                }
            }catch (NullPointerException npe){
                displayToast("Username or Password is incorrect.");
            }

        });
    }

    public void welcome(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.welcome_toast, null);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, 10);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
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
    public int generateNotificationID(){
        return (int) Math.floor(Math.random() * 9999);
    }
}