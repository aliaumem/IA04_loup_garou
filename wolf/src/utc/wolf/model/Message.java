package utc.wolf.model;

import jade.lang.acl.ACLMessage;

/**
 * Created with IntelliJ IDEA.
 * User: home
 * Date: 02/06/2014
 * Time: 22:17
 * To change this template use File | Settings | File Templates.
 */
abstract class Message extends ACLMessage
{
    public abstract Message createMessage();
}

class TwoMessage extends Message{
    static
    {
        MessageFactory msgFacto = MessageFactory.getInstance();
        msgFacto.registerMessage("TwoMsg", new TwoMessage());
    }

    @Override
    public Message createMessage() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}


class OneMessage extends Message
{
    static
    {
        MessageFactory msgFacto = MessageFactory.getInstance();
        msgFacto.registerMessage("OneMsg", new OneMessage());
    }

    @Override
    public OneMessage createMessage()
    {

        OneMessage test = new OneMessage();
        //test.addReceiver();
        return test;
    }
    //...
}






