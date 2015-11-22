package edu.csumb.brainwavemusicfinder;


import org.json.JSONObject;

/**
 * Created by Andre on 21.11.2015.
 */

    public interface MessageCallback {
        public void on(String event, JSONObject... data);
        public void onMessage(String message);
        public void onMessage(JSONObject json);
        public void onConnect();
        public void onDisconnect();
        public void onConnectFailure();
    }

