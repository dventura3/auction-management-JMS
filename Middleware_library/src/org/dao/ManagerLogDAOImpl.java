/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dao;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.XMLGen.ManagerLog;
import org.dto.VectorClock;
import org.util.Pair;
import org.util.XMLDataFactory;

/**
 *
 * @author marcx87
 */
public class ManagerLogDAOImpl implements ManagerLogDAO{
     /**
     * Metodo per salvare identificativo del Manager e vector clock corrente
     * @param managerId Identificativo del manager
     * @param vectorClock Vector clock corrente
     * @throws JAXBException
     * @throws IOException
     */
    public synchronized void storeManagerLogData(String managerId, VectorClock vectorClock) throws JAXBException, IOException {
        ManagerLog rootElement;
        try{
            rootElement = XMLDataFactory.getRootMLElement(managerId);
        } catch(JAXBException ex){
            rootElement = new ManagerLog();
        }
        rootElement.setManagerId(managerId);
        rootElement.setVectorClock(vectorClock);
        XMLDataFactory.marshallMLData(rootElement, managerId);
    }
    /**
     * Metodo per caricare identificativo del Manager e l’ultimo vector clock utilizzato
     * @param filename Nome del file di configurazione
     * @return Identificativo del manager e il rispettivo vector clock
     * @throws JAXBException
     * @throws IOException
     */
    public Pair<String, VectorClock> getManagerLogData(String filename) throws JAXBException, IOException {
        String clientId = null;
        VectorClock vectorClock = null;
        ManagerLog rootElement;
        try{
            rootElement = XMLDataFactory.getRootMLElement(filename.split("_")[1].split("\\.")[0]);
        } catch(JAXBException ex){
            rootElement = new ManagerLog();
        }
        clientId = rootElement.getManagerId();
        vectorClock = rootElement.getVectorClock();
        return new Pair<String, VectorClock>(clientId, vectorClock);
    }
}
