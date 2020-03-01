import android.app.Activity;
import android.content.Context;
import android.os.AsynTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.HttpUrlConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends Activity
{
    EditText cityName;
    TextView resultTextView;
    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);
    public void findWeather(View view)
    {
        try {
            String EncodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            downloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q="+EncodedCityName);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
        }
        
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
    }

    public class downloadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String ... urls)
        {
            String result="";
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data!=-1)
                {
                    char curr = (char) data;
                    result+=curr;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
            }
        }
        return null;
    }

    @Override
    public void onPostExecute(String result)
    {
        super.onPostExecute(result);
        try {
            
            String msg="";
            JSONobject obj = new JSONObject(result);
            String weatherInfo = obj.getString(weather);
            JSONArray arr = new JSONArray(weatherInfo);

            for(int i=0;i<arr.length;i++)
            {
                JSONObject jpart = new JSONObject(i);
                String main = jpart.getString(main);
                String desc = jpart.getString(desc);
                if(main!="" && desc !="")
                {
                    msg+=(main+": "+desc);
                }
            }
            if(msg!="")
            {
                resultTextView.setText(msg);
            }
            else{
                Toast.makeToast(getApplicationContext(),"could not load the weather",Toast.LENGTH_LONG);
            }
        } catch (Exception e) {
            Toast.makeToast(getApplicationContext(),"could not load the weather",Toast.LENGTH_LONG);
        }
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
}
