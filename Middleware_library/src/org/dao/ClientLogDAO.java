/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dao;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.dto.VectorClock;
import org.util.Pair;

/**
 *
 * @author marcx87
 */
public interface ClientLogDAO extends MiddlewareDAO{
    /**
     * Metodo pèr salvare identificativo del Client e vector clock corrente
     * @param clientId Identificativo del Client
     * @param vectorClock valore del vector clock corrente
     * @throws JAXBException
     * @throws IOException
     */
    public void storeClientLogData(String clientId, VectorClock vectorClock) throws JAXBException, IOException;
    /**
     * Metodo per caricare identificativo del Client e l’ultimo vector clock utilizzato
     * @param filename Nome del file di configurazione
     * @return Identificativo del Client e vector clock
     * @throws JAXBException
     * @throws IOException
     */
    public Pair<String, VectorClock> getClientLogData(String filename) throws JAXBException, IOException;
}
