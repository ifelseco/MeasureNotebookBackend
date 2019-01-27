package com.javaman.service.impl;

import com.javaman.entity.Notification;
import com.javaman.entity.User;
import com.javaman.service.FirebaseNotificationService;
import com.javaman.util.ConstFirebase;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

@Service
public class FirebaseNotificationServiceImpl implements FirebaseNotificationService{

    private static final Logger LOG = LoggerFactory.getLogger(FirebaseNotificationServiceImpl.class);

    @Override
    public void sendNotificationToMobile(User adminUser, Notification notification) {
        try{
            URL url = new URL(ConstFirebase.FIREBASE_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String date = format.format(notification.getCreatedDate());

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + ConstFirebase.FIREBASE_SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject json = new JSONObject();
            json.put("to", adminUser.getLastFirebaseRegId().trim());
            JSONObject data = new JSONObject();
            JSONObject mNotification = new JSONObject();
            mNotification.put("sound", "default");
            mNotification.put("title", notification.getTitle());
            data.put("message", notification.getMessage()); // data key :body
            data.put("data", notification.getData());
            data.put("time", date);
            data.put("id", notification.getId());
            json.put("data", data);
            json.put("notification", mNotification);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            conn.getInputStream();
        }catch(Exception e){
            LOG.error("Firebase Mobile Notf. Error: "+e.getClass()+" "+e.getMessage());
        }
    }

    @Override
    public void sendNotificationToWeb(User adminUser, Notification notification) {
        try{
            URL url = new URL(ConstFirebase.FIREBASE_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
            String date = format.format(notification.getCreatedDate());

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + ConstFirebase.FIREBASE_SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject json = new JSONObject();
            json.put("to", adminUser.getLastFirebaseWebRegId().trim());
            JSONObject data = new JSONObject();
            JSONObject mNotification = new JSONObject();
            mNotification.put("sound", "default");
            mNotification.put("title", notification.getTitle());
            json.put("notification", mNotification);
            data.put("message", notification.getMessage()); // data key :body
            data.put("data", notification.getData());
            data.put("time", date);
            data.put("id", notification.getId());
            json.put("data", data);


            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            conn.getInputStream();
        }catch(Exception e){
            LOG.error("Firebase Web Notf. Error: "+e.getClass()+" "+e.getMessage());
        }
    }
}
