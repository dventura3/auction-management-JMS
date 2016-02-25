/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dao;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.bind.JAXBException;
import org.XMLGen.ManagedQueues;
import org.XMLGen.ReplicaConfig;
import org.XMLGen.SingleQueue;
import org.XMLGen.SingleQueueTwoPC;
import org.XMLGen.TransactionManagerConfig;
import org.XMLGen.TwoPhaseCommitLog;
import org.dto.QueueItemDTO;
import org.util.MessageUtil;
import org.util.Pair;
import org.util.TwoPCState;
import org.util.XMLDataFactory;

/**
 * Data Access class
 * @author marcx87
 */
public class QueueManagerDAOImpl implements QueueManagerDAO{
   /**
        * Metodo per salvare gli oggetti contenuti in una determinata coda gestita da un Manager nelle
         * repliche o per salvare una data operazione nella coda delle richieste gestita da un Transaction Manager
        * @param data Elemento da salvare
        * @param processId Identificativo del processo
        * @param type Tipo del processo
        * @throws JAXBException
        * @throws IOException
        */
    public synchronized void StoreSingleQueueData(QueueItemDTO data, String processId, boolean type) throws JAXBException, IOException {
                SingleQueue singleQueue = MessageUtil.prepareManagedQueueData(data);
                ManagedQueues rootElement;
                try{
		 rootElement = XMLDataFactory.getRootElement(processId);
                } catch(JAXBException ex){
                    rootElement = new ManagedQueues();
                }
                if(rootElement.getSingleQueue().contains(singleQueue)){
                    int index =rootElement.getSingleQueue().indexOf(singleQueue);
                    if(rootElement.getSingleQueue().get(index).getWorkflows().contains(singleQueue.getWorkflows().get(0)) && rootElement.getSingleQueue().get(index).getRequestType().equals(singleQueue.getRequestType())){
                        return;
                    }
                    else if(type){
                        if(!rootElement.getSingleQueue().get(index).getWorkflows().contains(singleQueue.getWorkflows().get(0))){
                            rootElement.getSingleQueue().get(index).getWorkflows().add(singleQueue.getWorkflows().get(0));
                        }
                        rootElement.getSingleQueue().get(index).setRequestType(singleQueue.getRequestType());
                        rootElement.getSingleQueue().get(index).setLastUpdateTime(singleQueue.getLastUpdateTime());
                    } else{
                        rootElement.getSingleQueue().add(singleQueue);
                    }
                } else{
                    rootElement.getSingleQueue().add(singleQueue);
                }
		// store the new data
		XMLDataFactory.marshallData(rootElement, processId);
    }
    /**
     * Metodo per salvare lo stato di una transazione e le repliche che ne prendevano parte
     * @param data Elemento da salvare
     * @param state Stato della transazione
     * @param processId Ide del processo
     * @param replicaManager Identificativo delle repliche
     * @throws JAXBException
     * @throws IOException
     */
    public synchronized void LogSingleQueueData(QueueItemDTO data, TwoPCState state, String processId, String... replicaManager ) throws JAXBException, IOException {
                SingleQueueTwoPC singleQueue = MessageUtil.prepareLog2PCData(data);
                singleQueue.setTwoPCState(state.toString());
		TwoPhaseCommitLog rootElement;
                 try{
                    rootElement = XMLDataFactory.getRElement(processId);
                } catch(JAXBException ex){
                    rootElement = new TwoPhaseCommitLog();
                }
                if(rootElement.getSingleQueue().contains(singleQueue)){
                    int index =rootElement.getSingleQueue().indexOf(singleQueue);
                    if(rootElement.getSingleQueue().get(index).getWorkflows().contains(singleQueue.getWorkflows().get(0)) && rootElement.getSingleQueue().get(index).getTwoPCState().equals(singleQueue.getTwoPCState()) && rootElement.getSingleQueue().get(index).getRequestType().equals(singleQueue.getRequestType())){
                        return;
                    }
                    else{
                        rootElement.getSingleQueue().remove(singleQueue);
                        rootElement.getSingleQueue().add(singleQueue);
                    }
                } else{
                    rootElement.getSingleQueue().add(singleQueue);
                }
                //aggiungiamo i replica manager che partecipano alla transazione (solo TM)
                if(replicaManager != null && replicaManager.length != 0){
                    rootElement.getSingleQueue().get(0).getReplicaManagers().addAll(Arrays.asList(replicaManager));
                }
		// store the new data
		XMLDataFactory.marshallLogData(rootElement, processId);
    }
      /**
         * Metodo per caricare il file di log di una transazione che si stava eseguendo prima del crash.
         * @param processId Identificativo del processo
         * @return Coppia di valori: (Oggetto della transazione , Stato della transazione)
         * @throws JAXBException
         * @throws IOException
         * @throws ParseException
         */
    public Pair<QueueItemDTO, TwoPCState> getLogData(String processId) throws JAXBException, IOException, ParseException{
        Pair<QueueItemDTO, TwoPCState> items = null;
        TwoPhaseCommitLog rootElement;
        try{
            rootElement = XMLDataFactory.getRElement(processId);
        } catch(JAXBException ex){
            rootElement = new TwoPhaseCommitLog();
        }
        Iterator<SingleQueueTwoPC> it = rootElement.getSingleQueue().iterator();
        while(it.hasNext()){
            SingleQueueTwoPC sq = it.next();
            items = MessageUtil.prepareQueueItemDTO2PC(sq);
        }
        return items;
    }
     /**
     * Metodo per cancellare lo stato dell’ultima transazione
     * @param processId Ide ntificativo del processo
     * @throws JAXBException
     * @throws IOException
     */
    public synchronized void clearLogData(String processId) throws JAXBException, IOException{
        TwoPhaseCommitLog rootElement;
        try{
            rootElement= XMLDataFactory.getRElement(processId);
        } catch(JAXBException ex){
            rootElement = new TwoPhaseCommitLog();
        }
        rootElement.getSingleQueue().clear();
        XMLDataFactory.marshallLogData(rootElement, processId);
    }
    /**
     * Metodo per caricare i dati conenuti nel file xml gestito da un Replica Manager
     * @param processId Identificativo del processo
     * @return Lista ordinata degli elementi in coda
     * @throws JAXBException
     * @throws IOException
     * @throws ParseException
     */
    public ArrayList<QueueItemDTO> getAllQueueStoreData(QueueItemDTO queueItem, String processId) throws JAXBException, IOException, ParseException  {
             ManagedQueues rootElement;
             try{
                rootElement = XMLDataFactory.getRootElement(processId);
             } catch(JAXBException ex){
                rootElement = new ManagedQueues();
             }
             SingleQueue singleQueue = MessageUtil.prepareManagedQueueData(queueItem);
             SingleQueue result = null;
             if(rootElement.getSingleQueue().contains(singleQueue)){
                 int index =rootElement.getSingleQueue().indexOf(singleQueue);
                 result = rootElement.getSingleQueue().get(index);
             } else{
                 return null;
             }
            return MessageUtil.prepareQueueItemsDTO(result);
    }
    /**
     * Metodo per caricare le operazioni contenute nella coda delle richieste gestite da un Transaction Manager
     * @param processId Identificativo del processo
     * @throws JAXBException
     * @throws IOException
     */
    public LinkedList<QueueItemDTO> getAllQueueData(String processId) throws JAXBException, IOException, ParseException{
        LinkedList<QueueItemDTO> result = new LinkedList<QueueItemDTO>();
        ManagedQueues rootElement;
        try{
           rootElement = XMLDataFactory.getRootElement(processId);
        } catch(JAXBException ex){
           rootElement = new ManagedQueues();
        }
        for(SingleQueue item : rootElement.getSingleQueue()){
            result.addAll(MessageUtil.prepareQueueItemsDTO(item));
        }
        return result;
    }
    /**
         * Metodo per eliminare un workflow in coda quando si effettua un’operazione di dequeue da un Manager
         * @param data Oggetto da eliminare
         * @param processId Identificativo del processo
         * @param type Tipo del processo
         * @throws JAXBException
         * @throws IOException
         */
    public synchronized void clearQueueItemData(QueueItemDTO data, String processId, boolean type) throws JAXBException, IOException {
                ManagedQueues rootElement;
                try{
                    rootElement = XMLDataFactory.getRootElement(processId);
                } catch (JAXBException ex){
                    rootElement = new ManagedQueues();
                }
                SingleQueue singleQueue = MessageUtil.prepareManagedQueueData(data);
                if(rootElement.getSingleQueue().contains(singleQueue)){
                    if(type){
                        int index = rootElement.getSingleQueue().indexOf(singleQueue);
                        if(rootElement.getSingleQueue().get(index).getWorkflows().contains(singleQueue.getWorkflows().get(0))){
                            rootElement.getSingleQueue().get(index).getWorkflows().remove(singleQueue.getWorkflows().get(0));
                            rootElement.getSingleQueue().get(index).setLastUpdateTime(singleQueue.getLastUpdateTime());
                            rootElement.getSingleQueue().get(index).setRequestType(singleQueue.getRequestType());
                        }else{
                            return;
                        }
                    }else{
                        for(SingleQueue item : rootElement.getSingleQueue()){
                            if(!item.getWorkflows().isEmpty()){
                                if(item.equals(singleQueue)
                                        && item.getRequestType().equals(singleQueue.getRequestType())
                                        && item.getWorkflows().get(0).equals(singleQueue.getWorkflows().get(0))){
                                    rootElement.getSingleQueue().remove(item);
                                    break;
                                }
                            }else{
                                if(item.equals(singleQueue)
                                        && item.getRequestType().equals(singleQueue.getRequestType())){
                                    rootElement.getSingleQueue().remove(item);
                                    break;
                                }
                            }
                        }
                    }
                }
		XMLDataFactory.marshallData(rootElement, processId);
    }

    public void setTransactionManagerId(String id) throws JAXBException, IOException {
        TransactionManagerConfig rootElement;
        try{
            rootElement = XMLDataFactory.getRootTMCElement(id);
        }catch(JAXBException ex){
            rootElement = new TransactionManagerConfig();
        }
        rootElement.setTransactionManagerId(id);
        XMLDataFactory.marshallTMCData(rootElement, id);
    }

    public String getTransactionManagerId(String filename) throws JAXBException, IOException {
        TransactionManagerConfig rootElement;
        try{
            rootElement = XMLDataFactory.getRootTMCElement(filename.split("_")[1].split("\\.")[0]);
        } catch(JAXBException ex){
            rootElement = new TransactionManagerConfig();
            return null;
        }
        return rootElement.getTransactionManagerId();
    }

    public void setReplicaId(String id) throws JAXBException, IOException {
        ReplicaConfig rootElement;
        try{
            rootElement = XMLDataFactory.getRootRCElement(id);
        }catch(JAXBException ex){
            rootElement = new ReplicaConfig();
        }
        rootElement.setReplicaId(id);
        XMLDataFactory.marshallRCData(rootElement, id);
    }

    public String getReplicaId(String filename) throws JAXBException, IOException {
        ReplicaConfig rootElement;
        try{
            rootElement = XMLDataFactory.getRootRCElement(filename.split("_")[1].split("\\.")[0]);
        } catch(JAXBException ex){
            rootElement = new ReplicaConfig();
            return null;
        }
        return rootElement.getReplicaId();
    }

}
