//package com.sarmadtechempire.blogapp.NotificationUtils;
//import android.app.Activity;
//import android.content.Context;
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import org.json.JSONException;
//import org.json.JSONObject;
//import java.util.HashMap;
//import java.util.Map;
//
//public class FcmNotificationsSender {
//
//    String userFcmToken;
//    String title;
//    String body;
//    Context mContext;
//    Activity mActivity;
//    private RequestQueue requestQueue;
//    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
//    private final String fcmServerKey = "YOUR_SERVER_KEY";
//
//    public FcmNotificationsSender(String userFcmToken, String title, String body, Context mContext, Activity mActivity) {
//        this.userFcmToken = userFcmToken;
//        this.title = title;
//        this.body = body;
//        this.mContext = mContext;
//        this.mActivity = mActivity;
//    }
//
//    public void SendNotifications() {
//        requestQueue = Volley.newRequestQueue(mActivity);
//        JSONObject mainObj = new JSONObject();
//        try {
//            mainObj.put("to", userFcmToken);
//            JSONObject notiObject = new JSONObject();
//            notiObject.put("title", title);
//            notiObject.put("body", body);
//            notiObject.put("icon", "icon"); // drawable mein available icon ka naam
//            mainObj.put("notification", notiObject);
//
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    // response milne par code run karega
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    // error milne par code run karega
//                }
//            }) {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> header = new HashMap<>();
//                    header.put("content-type", "application/json");
//                    header.put("authorization", "key=" + fcmServerKey);
//                    return header;
//                }
//            };
//            requestQueue.add(request);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//}
//

// FcmNotificationsSender.java
package com.sarmadtechempire.blogapp.NotificationUtils;

import android.app.Activity;
import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.auth.oauth2.GoogleCredentials;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender {

    private String userFcmToken;
    private String title;
    private String body;
    private String imageUrl;
    private Context mContext;
    private Activity mActivity;
    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";

    public FcmNotificationsSender(String userFcmToken, String title, String body, String imageUrl, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public void sendNotification() {
        requestQueue = Volley.newRequestQueue(mContext);
        JSONObject mainObj = new JSONObject();
        try {
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", title);
            notificationObj.put("body", body);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                notificationObj.put("image", imageUrl);
            }

            JSONObject messageObj = new JSONObject();
            messageObj.put("notification", notificationObj);
            messageObj.put("to", userFcmToken);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, messageObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Handle success response
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error response
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    try {
                        headers.put("Authorization", "Bearer " + getAccessToken());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return headers;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getAccessToken() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount)
                .createScoped("https://www.googleapis.com/auth/firebase.messaging");
        googleCredentials.refresh();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
