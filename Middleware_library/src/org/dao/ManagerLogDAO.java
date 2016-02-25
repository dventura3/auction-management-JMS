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
public interface ManagerLogDAO extends MiddlewareDAO{
    /**
     * Metodo per salvare identificativo del Manager e vector clock corrente
     * @param managerId Identificativo del manager
     * @param vectorClock Vector clock corrente
     * @throws JAXBException
     * @throws IOException
     */
    public void storeManagerLogData(String managerId, VectorClock vectorClock) throws JAXBException, IOException;
    /**
     * Metodo per caricare identificativo del Manager e l’ultimo vector clock utilizzato
     * @param filename Nome del file di configurazione
     * @return Identificativo del manager e il rispettivo vector clock
     * @throws JAXBException
     * @throws IOException
     */
    public Pair<String, VectorClock> getManagerLogData(String filename) throws JAXBException, IOException;
}
