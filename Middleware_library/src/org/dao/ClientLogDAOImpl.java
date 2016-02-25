/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dao;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.XMLGen.ClientLog;
import org.dto.VectorClock;
import org.util.Pair;
import org.util.XMLDataFactory;

/**
 *
 * @author marcx87
 */
public class ClientLogDAOImpl implements ClientLogDAO{
     /**
     * Metodo pèr salvare identificativo del Client e vector clock corrente
     * @param clientId Identificativo del Client
     * @param vectorClock valore del vector clock corrente
     * @throws JAXBException
     * @throws IOException
     */
    public synchronized void storeClientLogData(String clientId, VectorClock vectorClock) throws JAXBException, IOException {
        ClientLog rootElement;
        try{
            rootElement = XMLDataFactory.getRootCLElement(clientId);
        } catch(JAXBException ex){
            rootElement = new ClientLog();
        }
        rootElement.setClientId(clientId);
        rootElement.setVectorClock(vectorClock);
        XMLDataFactory.marshallCLData(rootElement, clientId);
    }
    /**
     * Metodo per caricare identificativo del Client e l’ultimo vector clock utilizzato
     * @param filename Nome del file di configurazione
     * @return Identificativo del Client e vector clock
     * @throws JAXBException
     * @throws IOException
     */
    public Pair<String, VectorClock> getClientLogData(String filename) throws JAXBException, IOException {
        String clientId = null;
        VectorClock vectorClock = null;
        ClientLog rootElement;
        try{
            rootElement = XMLDataFactory.getRootCLElement(filename.split("_")[1].split("\\.")[0]);
        } catch(JAXBException ex){
            rootElement = new ClientLog();
        }
        clientId = rootElement.getClientId();
        vectorClock = rootElement.getVectorClock();
        return new Pair<String, VectorClock>(clientId, vectorClock);
    }

}
