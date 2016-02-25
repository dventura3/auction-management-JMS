/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dao;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.xml.bind.JAXBException;
import org.dto.QueueItemDTO;
import org.util.Pair;
import org.util.TwoPCState;

/**
 * Interfaccia che espone i metodi del data access layer
 * @author marcx87
 */
public interface QueueManagerDAO extends MiddlewareDAO{
       /**
        * Metodo per salvare gli oggetti contenuti in una determinata coda gestita da un Manager nelle
         * repliche o per salvare una data operazione nella coda delle richieste gestita da un Transaction Manager
        * @param data Elemento da salvare
        * @param processId Identificativo del processo
        * @param type Tipo del processo
        * @throws JAXBException
        * @throws IOException
        */
	public void StoreSingleQueueData (QueueItemDTO data, String processId, boolean type) throws JAXBException, IOException;
        /**
         * Metodo per salvare lo stato di una transazione e le repliche che ne prendevano parte
         * @param data Elemento da salvare
         * @param state Stato della transazione
         * @param processId Ide del processo
         * @param replicaManager Identificativo delle repliche
         * @throws JAXBException
         * @throws IOException
         */
        public void LogSingleQueueData(QueueItemDTO data, TwoPCState state, String processId, String... replicaManager) throws JAXBException, IOException;
        /**
         * Metodo per caricare tutti i workflow che erano contenuti in una determinata coda gestita da un Manager prima del crash
         * @param queueItem Oggetto che identifica la coda da caricare
         * @param processId Identificativo del processo
         * @return Lista dei workflow in coda
         * @throws JAXBException
         * @throws IOException
         * @throws ParseException
         */
	public ArrayList<QueueItemDTO> getAllQueueStoreData (QueueItemDTO queueItem, String processId) throws JAXBException, IOException, ParseException;
        /**
         * Metodo per caricare lo stato dell’ultima transazione che si stava eseguendo prima di un crash
         * @param processId Ide ntificativo del processo
         * @return Oggetto del transazione e stato corrente
         * @throws JAXBException
         * @throws IOException
         * @throws ParseException
         */
        public Pair<QueueItemDTO, TwoPCState> getLogData(String processId) throws JAXBException, IOException, ParseException;
        /**
         * Metodo per caricare le operazioni contenute nella coda delle richieste gestite da un Transaction Manager
         * @param processId Identificativo del processo
         * @return Lista ordinata degli elementi in coda
         * @throws JAXBException
         * @throws IOException
         * @throws ParseException
         */
        public LinkedList<QueueItemDTO> getAllQueueData(String processId) throws JAXBException, IOException, ParseException;
        /**
         * Metod per cancellare il file di log contenente lo stato dell’ultima transazione una volta completata
         * @param processId Identificativo del processo
         * @throws JAXBException
         * @throws IOException
         */

        public void clearLogData(String processId) throws JAXBException, IOException;
        /**
         * Metodo per eliminare un workflow in coda quando si effettua un’operazione di dequeue da un Manager
         * @param data Oggetto da eliminare
         * @param processId Identificativo del processo
         * @param type Tipo del processo
         * @throws JAXBException
         * @throws IOException
         */
	public void clearQueueItemData (QueueItemDTO data, String processId, boolean type) throws JAXBException, IOException;

        public void setTransactionManagerId(String id) throws JAXBException, IOException;
        public String getTransactionManagerId(String filename) throws JAXBException, IOException;

        public void setReplicaId(String id) throws JAXBException, IOException;
        public String getReplicaId(String filename) throws JAXBException, IOException;
}
