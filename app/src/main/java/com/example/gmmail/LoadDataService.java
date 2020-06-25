package com.example.gmmail;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.gmmail.MainActivity.ctxt;


public class LoadDataService extends Service {
    DataBaseHelper mydb;
    private static final String GMAIL_SCOPE = "oauth2:https://www.googleapis.com/auth/gmail.readonly";
    private static Gmail mailService;
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "loaddataservice";
        String channelName = "Load Data Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);

        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.user)
                .setContentTitle("GMmail")
                .setContentText("Getting data from server")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AsyncTask<Void,String,Void> datatask=new AsyncTask<Void,String, Void>() {


            @Override
            protected Void doInBackground(Void... voids) {
                mydb=new DataBaseHelper(LoadDataService.this);
                SharedPreferences sharedPrefs = getSharedPreferences("GMmaild", 0);
                String me=sharedPrefs.getString("gmid","me");
                String token;

                try{
                    token = GoogleAuthUtil.getToken(ctxt, me, GMAIL_SCOPE);
                    GoogleCredential credential = new GoogleCredential()
                            .setAccessToken(token);
                    System.out.println("got Credential");
                    HttpTransport httpTransport = new NetHttpTransport();
                    JsonFactory jsonFactory = new JacksonFactory();
                    mailService = new Gmail.Builder(httpTransport, jsonFactory,
                            credential).setApplicationName("GMmail").build();
                    System.out.println("got Service");

                    List<Message> messages=listMessagesMatchingQuery(mailService,me,"");
                    String from ="";
                    String fromid="";
                    String fromname="";
                    String to="";
                    String toid="";
                    String toname="";
                    String Subject="";
                    String Snippet;
                    String mid="";
                    Message mail;
                    String Labels;
                    String cname="";
                    String cmail="";
                    String pos="0";
                    String group="0";


                    String[] forms = { "EEE, dd MMM yyyy HH:mm:ss Z", "EEE, d MMM yyyy HH:mm:ss Z", "d MMM yyyy HH:mm:ss Z" };
                    for (Message m : messages)
                    {
                        try{
                            mid=m.getId();
                            mail=getMessage(mailService,"me",m.getId()+"");
                            from=mail.getPayload().getHeaders().stream().filter(f ->f.getName().equalsIgnoreCase("From")).findFirst().get().getValue();
                            try{
                                to=mail.getPayload().getHeaders().stream().filter(f ->f.getName().equalsIgnoreCase("To")).findFirst().get().getValue();
                                if(to.contains(","))
                                {
                                    continue;
                                }
                            }
                            catch (Exception exc){continue;}
                            try{
                                Subject=mail.getPayload().getHeaders().stream().filter(f ->f.getName().equalsIgnoreCase("Subject")).findFirst().get().getValue();
                                if(Subject.equals(" ")||Subject.equals("")||Subject.isEmpty())
                                {
                                    Subject="No Subject";
                                }
                            }
                            catch (Exception exc){Subject="No Subject";}
                            try {
                                Snippet=mail.getSnippet();
                                if(Snippet.equals(" ")||Snippet.equals("")||Snippet.isEmpty())
                                {
                                    Snippet="[No Text]";
                                }
                            }
                            catch (Exception exc){Snippet="[No Text]";}
                            String Date=mail.getPayload().getHeaders().stream().filter(f ->f.getName().equalsIgnoreCase("Date")).findFirst().get().getValue();
                            if(Date.contains("("))
                            {
                                Date=Date.substring(0,Date.indexOf('(')-1);
                            }
                            Date=toDate(Date,forms,"dd/MM/yy\nhh:mm a");
                            if(from.contains("<"))
                            {
                                fromid=from.substring(from.indexOf('<')+1,from.indexOf('>'));
                                fromname=from.substring(0,from.indexOf('<'));
                                if(from.indexOf('<')==0)
                                {
                                    fromname=from.substring(1,from.lastIndexOf("."));
                                }
                            }
                            else
                            {
                                if(from.contains("."))
                                {
                                    fromid=from;
                                    fromname=from.substring(0,from.lastIndexOf("."));
                                }
                            }
                            if(to.contains("<"))
                            {
                                toid=to.substring(to.indexOf('<')+1,to.indexOf('>'));
                                toname=to.substring(0,to.indexOf('<'));
                            }
                            else
                            {
                                if(to.contains("."))
                                {
                                    toid=to;
                                    toname=to.substring(0,to.lastIndexOf("."));
                                }

                            }
                            fromname=fromname.replace("\"", "");
                            toname=toname.replace("\"", "");
                            for(String lbl : mail.getLabelIds())
                            {
                                mydb.insertLabels(mid,lbl.replace("CATEGORY_",""));
                            }
                            if(!fromid.equals(me) && !toid.equals(me))
                            {
                                group="1";
                                cname=toname;
                                cmail=toid;

                            }
                            else if(fromid.equals(me))
                            {
                                pos="1";
                                cname=toname;
                                cmail=toid;
                            }
                            else
                            {
                                pos="0";
                                cname=fromname;
                                cmail=fromid;
                            }
                            mydb.insertData(mid,fromid,fromname,toid,toname,Subject,Snippet,Date,cname,cmail,pos,group);
                            System.out.println(from+":"+fromid+":"+fromname);
                        }
                        catch (Exception exc){System.out.println(exc.getMessage());}
                    }
                }
                catch (Exception e)
                {
                    System.out.println(e+"");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                stopForeground(true);
                stopSelf();
            }
        };
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) // Above Api Level 13
        {
            datatask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else // Below Api Level 13
        {
            datatask.execute();
        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public static List<Message> listMessagesMatchingQuery(Gmail service, String userId, String query) throws IOException {
        ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();

        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null)
        {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null)
            {
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(userId).setQ(query)
                        .setPageToken(pageToken).execute();
            }
            else {
                break;
            }
        }
        return messages;
    }

    public static Message getMessage(Gmail service, String userId, String messageId) throws IOException {
        Message message = service.users().messages().get(userId, messageId).setFormat("full").execute();
        return message;
    }

    public String toDate(String date,String[] fformat,String toformat)
    {
        for(String dt: fformat)
        {
            try{
                return new SimpleDateFormat(toformat).format(new SimpleDateFormat(dt).parse(date));
            }
            catch (Exception exc){}
        }
        return "NULL";
    }

}
