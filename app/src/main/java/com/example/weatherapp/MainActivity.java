package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText city;
    TextView results;

    public void Search(View view){
        DownloadTask task = new DownloadTask();
        String result = null;

        String City = city.getText().toString();
        String cityName = City.substring(0,1).toUpperCase() + City.substring(1);

        try {

            result = task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + cityName +"&appid=4251b32bff8958703510f26b92816199").get();

        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Enter correct City!!", Toast.LENGTH_LONG).show();
        }
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(city.getWindowToken(),0);
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection = null;
            String result = "";

            try {

                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Enter correct City!!", Toast.LENGTH_LONG).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {

                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather Data ",weatherInfo);

                JSONArray arrInfo = new JSONArray(weatherInfo);
                for (int i=0;i<arrInfo.length();i++){
                    JSONObject subObject = arrInfo.getJSONObject(i);

                    results.setVisibility(View.VISIBLE);
                    String main = subObject.getString("main");
                    String description = subObject.getString("description");
                    if (!main.equals("") && !description.equals("")){
                        results.setText(main + ": " + description);
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter correct City!!", Toast.LENGTH_LONG).show();
                    }
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = (EditText) findViewById(R.id.city);
        results = (TextView) findViewById(R.id.results);
    }
}