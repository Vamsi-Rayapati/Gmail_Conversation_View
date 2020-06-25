package com.example.gmmail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static android.content.ClipData.Item;
import static android.widget.ListPopupWindow.MATCH_PARENT;
import static android.widget.ListPopupWindow.WRAP_CONTENT;
import static com.example.gmmail.MainActivity.ctxt;
import static com.example.gmmail.MainActivity.sharedPrefs;
import static com.example.gmmail.MainActivity.udataList;


public class NewMail extends AppCompatActivity {
    public static  ArrayList<User> arrayOfUsers;
    public static  ArrayList<MFile> arrayOfFiles;
    public  static  UserAdapter adapter;
    public  static  FileAdapter fileAdapter;
    public  static  ListView flow;
    public static  int PICKFILE_RESULT_CODE=10010;
    public static String mfrom;
    public static String msubject;
    public static String mbody;
    public static  boolean done=true;

    private static final String GMAIL_SCOPE = "oauth2:https://www.googleapis.com/auth/gmail.compose";
    private Gmail mailService;
    private static String me;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_mail);

        AutoCompleteTextView autoCompleteTextView=findViewById(R.id.autoeditText3);
        AutoCompleteAdapter autoCompleteAdapter=new AutoCompleteAdapter(ctxt,udataList);
        autoCompleteTextView.setAdapter(autoCompleteAdapter);
        autoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    arrayOfUsers.add(new User(autoCompleteTextView.getText().toString(),autoCompleteTextView.getText().toString()));
                    adapter.notifyDataSetChanged();
                    autoCompleteTextView.setText("");
                    return true;
                }
                return false;
            }
        });
        TextView from=(TextView)findViewById(R.id.from);
        from.setText(sharedPrefs.getString("gmid","me"));
        EditText subject=(EditText)findViewById(R.id.subject);
        EditText body=(EditText)findViewById(R.id.body);

        arrayOfFiles=new ArrayList<MFile>();
        fileAdapter=new FileAdapter(ctxt,arrayOfFiles);
        ListView files=(ListView)findViewById(R.id.files);
        files.setAdapter(fileAdapter);

        arrayOfUsers = new ArrayList<User>();
        adapter = new UserAdapter(ctxt, arrayOfUsers);
        flow=(ListView) findViewById(R.id.flow);
        flow.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT));
        flow.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Udata item = (Udata)parent.getItemAtPosition(position);
                arrayOfUsers.add(new User(item.getName(),item.getEmail()));
                adapter.notifyDataSetChanged();
                if(adapter.getCount()==4)
                {
                    flow.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT,flow.getHeight()));
                }

                autoCompleteTextView.setText("");

            }
        });
        ImageButton filechooser=(ImageButton)findViewById(R.id.filechooser);
        filechooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");;
                chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(chooseFile,PICKFILE_RESULT_CODE);
            }
        });
        ImageButton send=(ImageButton)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mfrom=from.getText().toString();
                msubject=subject.getText().toString();
                mbody=body.getText().toString();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new SendMail().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else // Below Api Level 13
                    {
                        new SendMail().execute();
                    }
                }
                catch (Exception exc3){}
            }


        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==12345)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) // Above Api Level 13
            {
                new SendMail().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else // Below Api Level 13
            {
                new SendMail().execute();
            }
        }
        if(requestCode == PICKFILE_RESULT_CODE) {
            if(null != data)
            {
                if(null != data.getClipData())
                {

                    for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Item fitem=data.getClipData().getItemAt(i);
                        Uri uri = fitem.getUri();
                        String filename=getFileName(uri);
                        String filePath="";
                        try (InputStream is = ctxt.getContentResolver().openInputStream(uri))
                        {
                            File file = new File(ctxt.getCacheDir(), filename);
                            try (OutputStream os = new FileOutputStream(file))
                            {
                                byte[] buffer = new byte[1024];
                                int read;
                                while ((read = is.read(buffer))>0) {
                                    os.write(buffer, 0, read);
                                }
                                os.flush();
                            }
                            filePath=file.getPath();
                        }
                        catch (Exception exc)
                        {

                        }
                        arrayOfFiles.add(new MFile(filePath,filename));
                        fileAdapter.notifyDataSetChanged();


                    }
                }
                else
                    {
                    Uri uri = data.getData();
                    String filename=getFileName(uri);
                    String filePath="";
                    try (InputStream is = ctxt.getContentResolver().openInputStream(uri))
                    {
                        File file = new File(ctxt.getCacheDir(), filename);
                        try (OutputStream os = new FileOutputStream(file))
                        {
                            byte[] buffer = new byte[1024];
                            int read;
                            while ((read = is.read(buffer))>0) {
                                os.write(buffer, 0, read);
                            }
                                os.flush();
                        }
                        filePath=file.getPath();
                    }
                    catch (Exception exc)
                    {

                    }
                    arrayOfFiles.add(new MFile(filePath,filename));
                    fileAdapter.notifyDataSetChanged();
                }
            }
        }
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = ctxt.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    public static MimeMessage createEmailWithAttachment(String to, String from, String subject, String bodyText)
            throws MessagingException, IOException, URISyntaxException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/plain");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        for(MFile file:arrayOfFiles )
        {
            mimeBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(file.path);
            mimeBodyPart.setDataHandler(new DataHandler(source));
            mimeBodyPart.setFileName(file.fileName);
            multipart.addBodyPart(mimeBodyPart);
        }
        email.setContent(multipart);

        return email;
    }
    public static Message sendMessage(Gmail service,
                                      String userId,
                                      MimeMessage emailContent)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }
    public static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
    class SendMail extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            String token="";
            SharedPreferences sharedPrefs = ctxt.getSharedPreferences("GMmaild", 0);
            me = sharedPrefs.getString("gmid", "me");
            try {
                token = GoogleAuthUtil.getToken(ctxt, me, GMAIL_SCOPE);
                System.out.println("Got Token");

            }
            catch (UserRecoverableAuthException e)
            {
                startActivityForResult(e.getIntent(),12345);
                return  null;
            }
            catch (Exception exc) {
                System.out.println(":::"+exc.getClass());

            }
            try {
                try {
                    GoogleCredential credential = new GoogleCredential()
                            .setAccessToken(token);
                    System.out.println("Got Credential");
                    HttpTransport httpTransport = new NetHttpTransport();
                    JsonFactory jsonFactory = new JacksonFactory();
                    mailService = new Gmail.Builder(httpTransport, jsonFactory,
                            credential).setApplicationName("GMmail").build();
                    for(User user:arrayOfUsers)
                    {
                        String mto = user.mailid;
                        MimeMessage mail = createEmailWithAttachment(mto, mfrom, msubject, mbody);
                        sendMessage(mailService, me, mail);
                    }

                } catch (Exception exc2) {
                    System.out.println(exc2.getMessage());
                }
            }
            catch (Exception exc){

            }

            return null;
        }
    }
    public  void goback(View view)
    {
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }






}
