package com.example.sagar.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();

        //read the session_id and session_name variables
        if (extras != null) {
            session_id = extras.getString("SESSION_ID");
            session_name = extras.getString("SESSION_NAME");
        }
        Toast.makeText(this,session_id+session_name,Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public String session_name;
    public String session_id;

    private class DownloadFilesTask extends AsyncTask<String, Integer, Integer> {
        protected Integer doInBackground(String... params) {

            HttpClient httpClient=new DefaultHttpClient();

            HttpPost httppost = new HttpPost("http:/10.0.2.2/sk.dd:8083/user/login");
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");


            try {

                //get the UI elements for username and password
                EditText username= (EditText) findViewById(R.id.editText);
                EditText password= (EditText) findViewById(R.id.editText2);

                JSONObject json = new JSONObject();
                //extract the username and password from UI elements and create a JSON object
                json.put("username", username.getText().toString().trim());
                json.put("password", password.getText().toString().trim());

                //add serialised JSON object into POST request
                StringEntity se = new StringEntity(json.toString());
                //set request content type
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/hal+json"));
                httppost.setEntity(se);

                //send the POST request
                HttpResponse response = httpClient.execute(httppost);

                //read the response from Services endpoint
                Log.e("info",response.toString());
                System.out.println(response.toString());
                String jsonResponse = EntityUtils.toString(response.getEntity());

                JSONObject jsonObject = new JSONObject(jsonResponse);
                //read the session information
                session_name=jsonObject.getString("uuid");
                //session_id=jsonObject.getString("uuid");

                return 0;

            }catch (Exception e) {
                Log.v("Error adding article", e.getMessage());
            }

            HttpGet httpget = new HttpGet("http:/10.0.2.2/sk.dd:8083/rest/session/token");
            //set header to tell REST endpoint the request and response content types
            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-type", "application/json");

            JSONArray json = new JSONArray();

            try {

                HttpResponse response = httpClient.execute(httpget);

                //read the response and convert it into JSON array
                json = new JSONArray(EntityUtils.toString(response.getEntity()));
                //return the JSON array for post processing to onPostExecute function
                session_id=json.getString(0);



            }catch (Exception e) {
                Log.v("Error adding article",e.getMessage());
            }

            return 0;
        }



        protected void onPostExecute(Integer result) {

            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            //pass the session_id and session_name to ListActivity
            intent.putExtra("SESSION_ID", session_id);
            intent.putExtra("SESSION_NAME", session_name);
            //start the ListActivity
            startActivity(intent);

        }
    }

    public void Button_click(View view)
   {
        new DownloadFilesTask().execute();
   }
}
