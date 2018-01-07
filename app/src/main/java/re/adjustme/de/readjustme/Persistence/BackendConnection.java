package re.adjustme.de.readjustme.Persistence;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import re.adjustme.de.readjustme.Persistence.internal.HttpRequestQueue;

/**
 * Created by semmel on 05.01.2018.
 */

public class BackendConnection extends Service {

    public void sendRequest(JSONObject body, Context context) {
        String url = "http://re.adjustme.de/testData.php";

        JsonObjectRequest jsObjRequest;
        jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, body, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("backend", response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("backend-error", "Doesn't send anything.");
                    }
                });
// Access the RequestQueue through your singleton class.
        HttpRequestQueue.getInstance(context).addToRequestQueue(jsObjRequest);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
