package edu.csumb.brainwavemusicfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONObject;

public class MainActivity extends Activity implements
        PlayerNotificationCallback, ConnectionStateCallback {

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "4976a9e391f74baebbc1d44e515b6a2e";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "spotifytester://callback";
    private double count=0;
    private Player mPlayer;
    private IOSocket socket;
    private String mood;
    private String previosMood= "nothing";
    private int someVariable = 0;
    private TextView moodState;
    private ImageView img;
    // Request code that will be used to verify if the result comes from correct activity
// Can be any integer
    private static final int REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connect();
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.imageView);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        FloatingActionButton detectButton = (FloatingActionButton) findViewById(R.id.detect);
        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodState = (TextView) findViewById(R.id.textView);
                moodState.setText(mood);
                if (mood.equals("meditation")) {
                    img.setImageResource(R.drawable.meditation);
                    mPlayer.setShuffle(true);
                    mPlayer.play("spotify:user:spotify:playlist:1oXl0OHlE1mPDChMa8Y0Ax");
                } else if (mood.equals("excitement")) {
                    img.setImageResource(R.drawable.happy);
                    mPlayer.setShuffle(true);
                    mPlayer.play("spotify:user:spotify:playlist:1B9o7mER9kfxbmsRH9ko4z");
                } else if (mood.equals("bored")) {
                    img.setImageResource(R.drawable.bored);
                    mPlayer.setShuffle(true);
                    mPlayer.play("spotify:user:spotify:playlist:2U3mZqDktE7UJ1gE4eVoUv");
                } else if (mood.equals("frustration")) {
                    img.setImageResource(R.drawable.frustration);
                    mPlayer.setShuffle(true);
                    mPlayer.play("spotify:user:spotify:playlist:5eSMIpsnkXJhXEPyRQCTSc");
                } else if (mood.equals("engagement")) {
                    img.setImageResource(R.drawable.engagement);
                    mPlayer.setShuffle(true);
                    mPlayer.play("spotify:user:spotify:playlist:5cdgwETxybr7tWcr7RTiCO");
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                final Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                // Check if result comes from the correct activity
                Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer = player;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addPlayerNotificationCallback(MainActivity.this);
                       /* mPlayer.setShuffle(true);
                        mPlayer.play("spotify:user:spotify:playlist:1oXl0OHlE1mPDChMa8Y0Ax");*/
                        checkMood();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        };
    /*    Button happyButton = (Button) findViewById(R.id.happyButton);
        happyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.setShuffle(true);
                mPlayer.play("spotify:user:spotify:playlist:1B9o7mER9kfxbmsRH9ko4z");
                mood = "engagement"; //debugging
            }
        });
*/

        Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.skipToNext();
            }
        });
        Button pauseButton = (Button) findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.pause();
            }
        });

    }


    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    void connect() {

        socket = new IOSocket("http://aipservers.com:3001", new MessageCallback() {

            @Override
            public void onMessage(String message) {
                // Handle simple messages
            }

            @Override
            public void onConnect() {
                // Socket connection opened
            }

            @Override
            public void onDisconnect() {
                // Socket connection closed
                Log.d("Disconnect", "Disconnect");
            }

            @Override
            public void on(String event, JSONObject... data) {

                mood = event;
                count++;

                Log.d("test123", String.valueOf(count));
                if(count>=120)
                {
                    count=0;
                    //call function
                    Log.d("mood",event);
                    checkMood();
                }
            }

            @Override
            public void onMessage(JSONObject json) {
                Log.d("test123","json");
            }

            @Override
            public void onConnectFailure() {

            }
        });

        socket.connect();
    }
    public void checkMood()
    {
        //if mood changes change music otherwise stay the same
        moodState = (TextView) findViewById(R.id.textView);
        if(!previosMood.equals(mood)) {
            Log.d("previous",previosMood);

            if (mood.equals("meditation")) {
                img.setImageResource(R.drawable.meditation);
                mPlayer.setShuffle(true);
                mPlayer.play("spotify:user:spotify:playlist:1oXl0OHlE1mPDChMa8Y0Ax");
                Log.d("currentMood", mood);
                moodState.setText(mood);
                previosMood=mood;
            } else if (mood.equals("excitement")) {
                img.setImageResource(R.drawable.happy);
                mPlayer.setShuffle(true);
                mPlayer.play("spotify:user:spotify:playlist:1B9o7mER9kfxbmsRH9ko4z");
                Log.d("currentMood", mood);
                moodState.setText(mood);
                previosMood=mood;
            } else if (mood.equals("bored")) {
                img.setImageResource(R.drawable.bored);
                mPlayer.setShuffle(true);
                mPlayer.play("spotify:user:spotify:playlist:2U3mZqDktE7UJ1gE4eVoUv");
                Log.d("currentMood", mood);
                moodState.setText(mood);
                previosMood=mood;
            } else if (mood.equals("frustration")) {
                img.setImageResource(R.drawable.frustration);
                mPlayer.setShuffle(true);
                mPlayer.play("spotify:user:spotify:playlist:5eSMIpsnkXJhXEPyRQCTSc");
                moodState.setText(mood);
                previosMood=mood;
            } else if (mood.equals("engagement")) {
                img.setImageResource(R.drawable.engagement);
                mPlayer.setShuffle(true);
                mPlayer.play("spotify:user:spotify:playlist:5cdgwETxybr7tWcr7RTiCO");
                Log.d("currentMood", mood);
                moodState.setText(mood);
                previosMood=mood;
            }

        }
    }
}
