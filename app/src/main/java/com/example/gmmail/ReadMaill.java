package com.example.gmmail;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;

import java.io.IOException;

import static com.example.gmmail.MainActivity.ctxt;

public class ReadMaill extends AppCompatActivity {
    private static  String mid;
    private static final String GMAIL_SCOPE = "oauth2:https://www.googleapis.com/auth/gmail.readonly";
    private static Gmail mailService;
    private static  String html;
    private static String body;
    private static String subject;
    private static WebView web;
    private static String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_maill);
        mid=getIntent().getStringExtra("mid");
        subject=getIntent().getStringExtra("subject");
        name=getIntent().getStringExtra("name");
        ((TextView)findViewById(R.id.subject)).setText(subject);
        ((TextView)findViewById(R.id.name)).setText(name);

        web=(WebView)findViewById(R.id.web);
        web.setInitialScale(1);
        html="<meta name=\"viewport\" content='width=device-width, initial-scale=1.0,text/html,charset=utf-8' >";

        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setLoadWithOverviewMode(true);

        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setSupportZoom(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) // Above Api Level 13
        {
            new ReadMail().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else // Below Api Level 13
        {
            new ReadMail().execute();
        }


    }
    class ReadMail extends AsyncTask<Void,Void,Void>
    {
        SharedPreferences sharedPrefs = getSharedPreferences("GMmaild", 0);
        String me=sharedPrefs.getString("gmid","me");
        String token;
        Message mail;
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                token = GoogleAuthUtil.getToken(ctxt, me, GMAIL_SCOPE);
                GoogleCredential credential = new GoogleCredential()
                        .setAccessToken(token);
                HttpTransport httpTransport = new NetHttpTransport();
                JsonFactory jsonFactory = new JacksonFactory();
                mailService = new Gmail.Builder(httpTransport, jsonFactory,
                        credential).setApplicationName("GMmail").build();
                mail=getMessage(mailService,me,mid);
                for(MessagePart part:mail.getPayload().getParts())
                {
                    if(part.getMimeType().equalsIgnoreCase("text/html"))
                    {
                        html+=Decode(part.getBody().getData());
                    }

                }



            }
            catch (Exception exc){}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            web.loadDataWithBaseURL(null,html,"text/html", "utf-8", null);
            System.out.println(html);
        }
    }
    public static Message getMessage(Gmail service, String userId, String messageId) throws IOException {
        Message message = service.users().messages().get(userId, messageId).setFormat("full").execute();
        return message;
    }
    public  String  Decode(String s)
    {
        return StringUtils.newStringUtf8(Base64.decodeBase64(s));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public  void goback(View view)
    {
        onBackPressed();
    }
}
