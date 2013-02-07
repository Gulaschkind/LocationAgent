package org.ligi.LocationAgent;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * User: ligi
 * Date: 2/6/13
 * Time: 10:41 PM
 */

public class UpdaterService extends Service implements LocationListener {

    private static final int NOTIFICATION_EX = 1;
    private NotificationManager notificationManager;
    private LocationManager locationManager;
    private String username;
    private String url;
    private Location act_location;

    public UpdaterService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //code to execute when the service is first created

    }

    @Override
    public void onDestroy() {
        stopService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {


        notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "Location Agent Starting";
        long when = System.currentTimeMillis();

        Notification notification = new Notification.Builder(this).setSmallIcon(R.drawable.ic_launcher).setTicker(tickerText).setWhen(when).setOngoing(true).build();
        //new Notification(icon, tickerText, when);

        username = intent.getStringExtra("username");
        url = intent.getStringExtra("url");



        Context context = getApplicationContext();
        CharSequence contentTitle = "Location Agent running";
        CharSequence contentText = "we are updating";
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle,
                contentText, contentIntent);

        notificationManager.notify(NOTIFICATION_EX, notification);


        Toast.makeText(this, "Started!", Toast.LENGTH_LONG);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, intent.getIntExtra("time",10), 1, this);

        return START_STICKY;
    }

    private void stopService() {

        locationManager.removeUpdates(this);
        notificationManager.cancel(NOTIFICATION_EX);
    }

    @Override
    public void onLocationChanged(Location location) {
        act_location=location;

        new SendAsyncTask().execute();
    }

    class SendAsyncTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject js = new JSONObject();
            try {

                js.put("username", username);
                js.put("lat", "" + act_location.getLatitude());
                js.put("lon", "" + act_location.getLongitude());
                js.put("accuracy", "" + act_location.getAccuracy());

                url="http://fatbaby.de:8088/location";
                Log.i("LocationAgent","location changed " + js.toString(2) + " to " + url);
                DefaultHttpClient httpclient = new DefaultHttpClient();

                //url with the post data

                HttpPost httpost = new HttpPost(url);

                //convert parameters into JSON object

                //passes the results to a string builder/entity
                StringEntity se = new StringEntity(js.toString(2));

                //sets the post request as the resulting string
                httpost.setEntity(se);
                //sets a request header so the page receving the request
                //will know what to do with it
                httpost.setHeader("Accept", "application/json");
                httpost.setHeader("Content-type", "application/json");
                ResponseHandler responseHandler = new BasicResponseHandler();

                httpclient.execute(httpost, responseHandler);
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ClientProtocolException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            catch (IllegalStateException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            return null;
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}