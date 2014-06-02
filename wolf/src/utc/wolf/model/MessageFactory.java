package utc.wolf.model;

import jade.util.leap.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Matthieu Hanne
 * Date: 02/06/2014
 * Time: 22:10
 * To change this template use File | Settings | File Templates.
 */
public class MessageFactory {
    private HashMap m_RegisteredProducts = new HashMap();

    public void registerProduct (String MessageType, Class MessageClass)
    {
        m_RegisteredProducts.put(MessageType, MessageClass);
    }

    public Product createProduct(String productID)
    {
        Class productClass = (Class)m_RegisteredProducts.get(productID);
        Constructor productConstructor = cClass.getDeclaredConstructor(new Class[] { String.class });
        return (Product)productConstructor.newInstance(new Object[] { });
    }
}
