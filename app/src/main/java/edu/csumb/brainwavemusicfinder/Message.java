package edu.csumb.brainwavemusicfinder;

/**
 * Created by Andre on 21.11.2015.
 */
public class Message extends IOMessage{

    public Message(String message){
        super(IOMessage.MESSAGE, -1, "", message);
    }
}
