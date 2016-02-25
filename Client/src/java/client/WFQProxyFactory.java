/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

/**
 *Classe che implementa il pattern Factory per instanziare il WFQueueManagerProxy
 * @author Daniela88, LuigxIV, marcx87
 */
public class WFQProxyFactory
{
    /**
     * L'istanza del factory
     */
    private static WFQProxyFactory instance = null;
    /**
     * Costruttore di default
     */
    public WFQProxyFactory() {}
    /**
     * Metodo per ottenere l'istanza del WFQProxyFactory
     * @return Istanza del WFQProxyFactory
     */
    public static synchronized WFQProxyFactory getInstance()
    {
        if(instance == null)
        {
            instance = new WFQProxyFactory();
        }
        return instance;
    }

    /**
     * Metodo che ritorna una nuova istanza del WFQueueManagerProxy
     * @return Istanza del WFQueueManagerProxy
     */
    public WFQueueManagerProxy getProxy()
    {
        return new WFQueueManagerProxy();
    }

}
