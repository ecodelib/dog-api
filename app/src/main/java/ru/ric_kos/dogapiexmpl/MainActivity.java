package ru.ric_kos.dogapiexmpl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    public static Bitmap getBitmapFromURL(String src) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(src);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();


        }finally {
            connection.disconnect(); // close the HttpURLConnection
        }
        return null;
    }

    private class MyNetworkHandlerThread extends HandlerThread {
        private static final int STATE_1 = 1; //Получаем путь к файлу
        private static final int STATE_2 = 2;//Загружаем картинку
        private String url;
        private Handler mHandler;
        public MyNetworkHandlerThread() {
            super("NetworkHandlerThread");
        }
        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            mHandler = new Handler(getLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    switch(msg.what){
                        case STATE_1:
                           try {
                               url = getURL(); //первая сетевая операция
                               if (url != null) {
                                   sendMessage(obtainMessage(STATE_2, url));
                               } else {


                               }
                           }catch  (JSONException e) {
                            e.printStackTrace();
                        }

                            break;

                        case STATE_2:
                            Bitmap b = getBitmapFromURL((String) msg.obj);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ImageView mImageView = (ImageView)findViewById(R.id.imageView);
                                    mImageView.setImageBitmap(b);

                                }
                            });
                            break;
                    }
                }
            };
            fetchDataFromNetwork();
        }
        public void fetchDataFromNetwork() {
            mHandler.sendEmptyMessage(STATE_1);
        }
    }
    private String getURL() throws JSONException {
     //   SystemClock.sleep(2000); // задержимся немного
        JSONObject jsonObject = getFromServer();
        String s = "";
        if (jsonObject == null) {
            Toast message = Toast.makeText(getApplicationContext(),
                    getString(R.string.message_server_disable),
                    Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                    message.getYOffset() / 2);
            message.show();
        }else{
            try {

                s = jsonObject.getString("message");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            int i = s.trim().length();
            if (i>0) {

                return s;
            }

        }
        return getString(R.string.dog_setter_nouse);
    }
    private JSONObject getFromServer(){
        HttpURLConnection connection = null;
        URL url = createURL("");
        try {
            connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                StringBuilder builder = new StringBuilder();

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {

                    String line;

                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                }
                catch (IOException e) {

                    e.printStackTrace();
                }

                return new JSONObject(builder.toString());
            }
            else {

            }
        }
        catch (Exception e) {

            e.printStackTrace();
        }
        finally {
            connection.disconnect(); // close the HttpURLConnection
        }

        return null;
    }
    private URL createURL(String city) {

        String baseUrl = getString(R.string.https_service);
        try {

            String urlString =  baseUrl;

            return new URL(urlString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null; // URL was malformed
    }
    private MyNetworkHandlerThread mThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button mGetButton = (Button) findViewById(R.id.getButton);
        mGetButton.setOnClickListener(new View.OnClickListener(

        ) {
            @Override
            public void onClick(View view) {
                mThread = new MyNetworkHandlerThread();
                mThread.start();


            }
        });
        Button mGetButton1 = (Button) findViewById(R.id.getButton1);
        final String[] tpm_url = new String[1];
        mGetButton1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                ExecutorService es = Executors.newSingleThreadExecutor();

                /////////
                es.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String url;
                            url = getURL(); //первая сетевая операция
                            if (url != null) {
                                tpm_url[0] = url;
                            } else {


                            }
                        }catch  (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                ////////
                es.submit(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap b = getBitmapFromURL((String) tpm_url[0]);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView mImageView = (ImageView)findViewById(R.id.imageView);
                                mImageView.setImageBitmap(b);

                            }
                        });
                    }
                });
            es.shutdown();
            }
        });


    }
}