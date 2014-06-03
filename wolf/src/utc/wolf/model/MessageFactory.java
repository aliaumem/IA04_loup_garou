package utc.wolf.model;

import jade.util.leap.HashMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created with IntelliJ IDEA.
 * User: Matthieu Hanne
 * Date: 02/06/2014
 * Time: 22:10
 * To change this template use File | Settings | File Templates.
 */
public class MessageFactory {

    private static MessageFactory instance = null;

    protected MessageFactory(){/*Exists only to defeat instantiation.*/}

    public static MessageFactory getInstance(){
        if(instance == null){
            instance = new MessageFactory();
        }
        return instance;
    }

    private HashMap m_RegisteredMessages = new HashMap();

    public void registerMessage(String MessageID, Message msg)    {
        m_RegisteredMessages.put(MessageID, msg);
    }

    public Message createMessage(String MessageID){
       return  ((Message)m_RegisteredMessages.get(MessageID)).createMessage();
    }
}
