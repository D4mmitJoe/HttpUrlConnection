package com.example.joem.httpurlconnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import org.apache.commons.io.IOUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonCheckConnection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()){
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    new GetDataAsync().execute("http://api.theappsdr.com/simple.php");//need to pass 'execute' a string, in this case a URL
                }else{
                    Toast.makeText(MainActivity.this, "Not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //function enables us to check status of internet
    private boolean isConnected(){
        //ConnectivityManager allows us to get current network information
        ConnectivityManager connectivityManagerName = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfoName = connectivityManagerName.getActiveNetworkInfo();//returns network info

        //checking to see if there's an internet connection
        if (networkInfoName == null || !networkInfoName.isConnected() ||
                (networkInfoName.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfoName.getType() != ConnectivityManager.TYPE_MOBILE)){
            return false;
        }
        return true;
    }

    //1st parameter: URL, 2nd: progress reporting (void nullifies it), 3rd: return [value?]
    private class GetDataAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... paramsName) {

            //convert BufferedReader into a string, enabling to read line by line and then append it to stringBuilder
            //this is done instead of appending to string (which will create a new string), thus stringBuilder is more efficient
            //stringBuilder is similar to an arrayList in that you add things to the end of the arrayList instead of creating a string every time you append
            StringBuilder stringBuilderName = new StringBuilder();

            HttpURLConnection connectionName = null;
            BufferedReader readerName = null;
            String result = null;
            try {
                //URL passed as parameter
                //A lot of network calls can cause exceptions so should contain try-catch block
                URL urlName = new URL(paramsName[0]);
                connectionName = (HttpURLConnection)urlName.openConnection();//gets connection
                connectionName.connect();
                //'responseCode' checks if status is 'ok,' hence ".http_ok"
                if (connectionName.getResponseCode() == HttpURLConnection.HTTP_OK){
                    //reads results, else results returns null
                    //***MUST comment this out if you wish to use "reader" instead
                    result = IOUtils.toString(connectionName.getInputStream(), "UTF8");//'UTF8'=encoding
                }

                //BufferedReader has the ability to read incoming stream word by word
                //InputStreamReader has the ability to read incoming stream character by character
                //commented out bc replaced by 'return IOUtils...' above
                /*readerName = new BufferedReader(new InputStreamReader(connectionName.getInputStream()));
                String line = "";
                while((line = readerName.readLine()) != null){//means 'line' is set to the next line
                    stringBuilderName.append(line);
                }
                result = stringBuilderName.toString();*/

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connectionName != null){
                    connectionName.disconnect();
                }
                //need to close the connection to clean-up the resources after the connection is done
                    //can be done at bufferReader level (or inputStream level?)
                if (readerName != null){
                    try {
                        readerName.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }

        @Override//to receive the results; result in this case is "basically the API?"
        protected void onPostExecute(String resultName) {
            if (resultName != null){
                Log.d("demo", resultName);
            }else{
                Log.d("demo", "null result");
            }
        }
    }
}