package edu.csumb.brainwavemusicfinder;

/**
 * Created by Andre on 21.11.2015.
 */
import java.util.TimerTask;

public class IOBeat extends TimerTask {

    private IOWebSocket socket;
    private boolean running = false;

    public IOBeat(IOWebSocket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        if(running){
            socket.send("2::"); //send heartbeat;
            System.out.println("HeartBeat Written to server");
        }
    }

    public void start(){
        running = true;
    }

    public void stop(){
        running = false;
    }

    public boolean isRunning(){
        return running;
    }

}