/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.util;

import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import org.XMLGen.AuctionBidLog;
import org.XMLGen.AuctionStatusLog;
import org.XMLGen.ClientLog;
import org.XMLGen.ManagedQueues;
import org.XMLGen.ManagerLog;
import org.XMLGen.ObjectFactory;
import org.XMLGen.ReplicaConfig;
import org.XMLGen.TransactionManagerConfig;
import org.XMLGen.TwoPhaseCommitLog;

/**
 * Factory class for creating XML core elements
 * @author marcx87
 */
public class XMLDataFactory {
    private static JAXBContext jc = null;
    private static JAXBElement<ManagedQueues> rootElement = null;
    private static JAXBElement<TwoPhaseCommitLog> rElement = null;
    private static JAXBElement<AuctionStatusLog> rootASElement = null;
    private static JAXBElement<AuctionBidLog> rootABElement = null;
    private static JAXBElement<ManagerLog> rootMLElement = null;
    private static JAXBElement<ClientLog> rootCLElement = null;
    private static JAXBElement<TransactionManagerConfig> rootTMCElement = null;
    private static JAXBElement<ReplicaConfig> rootRCElement = null;
    private static Marshaller marshaller = null;
    private static Unmarshaller unmarshaller = null;
    private static String xmlDataStorePath = "store";
    private static String xmlTwoPhaseCommitLog = "twoPCLog";
    private static String xmlAuctionStatusLog = "auctionStatus";
    private static String xmlAuctionBidLog = "auctionBid";
    private static String xmlManagerLog = "managerLog";
    private static String xmlClientLog = "clientLog";
    private static String xmlTransactionManagerConfig = "transactionManagerConfig";
    private static String xmlReplicaConfig = "replicaConfig";
    /**
     * Initializes XML un-marshaller
     * @throws JAXBException
     */
    private static void initUnMarshaller(String processId)  throws JAXBException, IOException{
    	// create a JAXBContext
        ObjectFactory of = new ObjectFactory();
        File file = new File(xmlDataStorePath + "_" + processId +".xml");
    	jc = JAXBContext.newInstance("org.XMLGen");
        unmarshaller = jc.createUnmarshaller();
        if(file.exists()){
            rootElement = of.createManagedElement((ManagedQueues) unmarshaller.unmarshal(file));
        } else{
            file.createNewFile();
            rootElement = of.createManagedElement((ManagedQueues) unmarshaller.unmarshal(file));
        }
    }

    private static void initUnMarshallerAS(String processId) throws JAXBException, IOException{
        // create a JAXBContext
        ObjectFactory of = new ObjectFactory();
        File file = new File(xmlAuctionStatusLog + "_" + processId+".xml");
    	jc = JAXBContext.newInstance("org.XMLGen");
        unmarshaller = jc.createUnmarshaller();
        if(file.exists()){
            rootASElement = of.createAuctionStatusLogElement((AuctionStatusLog) unmarshaller.unmarshal(file));
        } else{
            file.createNewFile();
            rootASElement = of.createAuctionStatusLogElement((AuctionStatusLog) unmarshaller.unmarshal(file));
        }
    }

    private static void initUnMarshallerAB(String processId) throws JAXBException, IOException{
        // create a JAXBContext
        ObjectFactory of = new ObjectFactory();
        File file = new File(xmlAuctionBidLog + "_" + processId+".xml");
    	jc = JAXBContext.newInstance("org.XMLGen");
        unmarshaller = jc.createUnmarshaller();
        if(file.exists()){
            rootABElement = of.createAuctionBidLogElement((AuctionBidLog) unmarshaller.unmarshal(file));
        } else{
            file.createNewFile();
            rootABElement = of.createAuctionBidLogElement((AuctionBidLog) unmarshaller.unmarshal(file));
        }
    }

    private static void initUnMarshaller2PC(String processId) throws JAXBException, IOException{
        // create a JAXBContext
        ObjectFactory of = new ObjectFactory();
        File file = new File(xmlTwoPhaseCommitLog + "_" + processId+".xml");
        jc = JAXBContext.newInstance("org.XMLGen");
        unmarshaller = jc.createUnmarshaller();
        if(file.exists()){
            rElement = of.createTwoPhaseCommitLogElement((TwoPhaseCommitLog) unmarshaller.unmarshal(file));
        } else{
            file.createNewFile();
            rElement = of.createTwoPhaseCommitLogElement((TwoPhaseCommitLog) unmarshaller.unmarshal(file));
        }
    }

    private static void initUnMarshallerML(String processId) throws JAXBException, IOException{
         // create a JAXBContext
        ObjectFactory of = new ObjectFactory();
        File file = new File(xmlManagerLog + "_" + processId+".xml");
        jc = JAXBContext.newInstance("org.XMLGen");
        unmarshaller = jc.createUnmarshaller();
        if(file.exists()){
            rootMLElement = of.createManagerLogElement((ManagerLog) unmarshaller.unmarshal(file));
        } else{
            file.createNewFile();
            rootMLElement = of.createManagerLogElement((ManagerLog) unmarshaller.unmarshal(file));
        }
    }

    private static void initUnMarshallerCL(String processId) throws JAXBException, IOException{
         // create a JAXBContext
        ObjectFactory of = new ObjectFactory();
        File file = new File(xmlClientLog + "_" + processId+".xml");
        jc = JAXBContext.newInstance("org.XMLGen");
        unmarshaller = jc.createUnmarshaller();
        if(file.exists()){
            rootCLElement = of.createClientLogElement((ClientLog) unmarshaller.unmarshal(file));
        } else{
            file.createNewFile();
            rootCLElement = of.createClientLogElement((ClientLog) unmarshaller.unmarshal(file));
        }
    }

    private static void initUnMarshallerTMC(String processId) throws JAXBException, IOException{
         // create a JAXBContext
        ObjectFactory of = new ObjectFactory();
        File file = new File(xmlTransactionManagerConfig + "_" + processId+".xml");
        jc = JAXBContext.newInstance("org.XMLGen");
        unmarshaller = jc.createUnmarshaller();
        if(file.exists()){
            rootTMCElement = of.createTransactionManagerConfigElement((TransactionManagerConfig) unmarshaller.unmarshal(file));
        } else{
            file.createNewFile();
            rootTMCElement = of.createTransactionManagerConfigElement((TransactionManagerConfig) unmarshaller.unmarshal(file));
        }
    }

    private static void initUnMarshallerRC(String processId) throws JAXBException, IOException{
         // create a JAXBContext
        ObjectFactory of = new ObjectFactory();
        File file = new File(xmlReplicaConfig + "_" + processId+".xml");
        jc = JAXBContext.newInstance("org.XMLGen");
        unmarshaller = jc.createUnmarshaller();
        if(file.exists()){
            rootRCElement = of.createReplicaConfigElement((ReplicaConfig) unmarshaller.unmarshal(file));
        } else{
            file.createNewFile();
            rootRCElement = of.createReplicaConfigElement((ReplicaConfig) unmarshaller.unmarshal(file));
        }
    }
    /**
     * Returns root element of xml data store
     * @return root element
     * @throws JAXBException
     */
    public static ManagedQueues getRootElement(String processId) throws JAXBException, IOException {
    	if (rootElement == null) {
    		initUnMarshaller(processId);
    	}
        ManagedQueues queues = (ManagedQueues)rootElement.getValue();
        return queues;
    }

    public static TwoPhaseCommitLog getRElement(String processId) throws JAXBException, IOException{
        if (rElement == null) {
    		initUnMarshaller2PC(processId);
    	}
        TwoPhaseCommitLog log = (TwoPhaseCommitLog)rElement.getValue();
        return log;
    }

    public static AuctionStatusLog getRootASElement(String processId) throws JAXBException, IOException{
        if (rootASElement == null) {
    		initUnMarshallerAS(processId);
    	}
        AuctionStatusLog log = (AuctionStatusLog)rootASElement.getValue();
        return log;
    }
    
    public static AuctionBidLog getRootABElement(String processId) throws JAXBException, IOException{
        if (rootABElement == null) {
    		initUnMarshallerAB(processId);
    	}
        AuctionBidLog log = (AuctionBidLog)rootABElement.getValue();
        return log;
    }

    public static ManagerLog getRootMLElement(String processId) throws JAXBException, IOException{
        if(rootMLElement == null){
            initUnMarshallerML(processId);
        }
        ManagerLog log = (ManagerLog) rootMLElement.getValue();
        return log;
    }

    public static ClientLog getRootCLElement(String processId) throws JAXBException, IOException{
        if(rootCLElement == null){
            initUnMarshallerCL(processId);
        }
        ClientLog log = (ClientLog) rootCLElement.getValue();
        return log;
    }

    public static TransactionManagerConfig getRootTMCElement(String processId) throws JAXBException, IOException{
        if(rootTMCElement == null){
            initUnMarshallerTMC(processId);
        }
        TransactionManagerConfig log = (TransactionManagerConfig) rootTMCElement.getValue();
        return log;
    }

    public static ReplicaConfig getRootRCElement(String processId) throws JAXBException, IOException{
        if(rootRCElement == null){
            initUnMarshallerRC(processId);
        }
        ReplicaConfig log = (ReplicaConfig) rootRCElement.getValue();
        return log;
    }
    /**
     * Initializes XML marshaller
     * @throws JAXBException
     */
    private static void initMarshaller() throws JAXBException {
    	// create a JAXBContext
    	jc = JAXBContext.newInstance("org.XMLGen");
        marshaller = jc.createMarshaller();
    }
    /**
     * Marshalls managed queues data
     * @param queuesData
     * @throws JAXBException
     */
    public static void marshallData(ManagedQueues queuesData, String processId) throws JAXBException, IOException {
            File file = new File(xmlDataStorePath + "_" +processId+".xml");
            if (marshaller == null) {
                    initMarshaller ();
            }
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            if(file.exists()){
                marshaller.marshal(queuesData, file);
            } else{
                file.createNewFile();
                marshaller.marshal(queuesData, file);
            }
	}

    public static void marshallLogData(TwoPhaseCommitLog logData, String processId) throws JAXBException, IOException {
            File file = new File(xmlTwoPhaseCommitLog + "_" +processId+".xml");
            if (marshaller == null) {
                    initMarshaller ();
            }
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            if(file.exists()){
            marshaller.marshal(logData, file);
            } else{
                 file.createNewFile();
                 marshaller.marshal(logData, file);
            }
	}

    public static void marshallLogASData(AuctionStatusLog logData, String processId) throws JAXBException, IOException {
            File file = new File(xmlAuctionStatusLog + "_" +processId+".xml");
            if (marshaller == null) {
                    initMarshaller ();
            }
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            if(file.exists()){
            marshaller.marshal(logData, file);
            } else{
                 file.createNewFile();
                 marshaller.marshal(logData, file);
            }
	}

    public static void marshallLogABData(AuctionBidLog logData, String processId) throws JAXBException, IOException {
            File file = new File(xmlAuctionBidLog + "_" +processId+".xml");
            if (marshaller == null) {
                    initMarshaller ();
            }
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            if(file.exists()){
            marshaller.marshal(logData, file);
            } else{
                 file.createNewFile();
                 marshaller.marshal(logData, file);
            }
	}

    public static void marshallMLData(ManagerLog logData, String processId) throws JAXBException, IOException{
        File file = new File(xmlManagerLog + "_" +processId+".xml");
        if (marshaller == null) {
                initMarshaller ();
        }
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        if(file.exists()){
        marshaller.marshal(logData, file);
        } else{
             file.createNewFile();
             marshaller.marshal(logData, file);
        }
    }

    public static void marshallCLData(ClientLog logData, String processId) throws JAXBException, IOException{
        File file = new File(xmlClientLog + "_" +processId+".xml");
        if (marshaller == null) {
                initMarshaller ();
        }
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        if(file.exists()){
        marshaller.marshal(logData, file);
        } else{
             file.createNewFile();
             marshaller.marshal(logData, file);
        }
    }

    public static void marshallTMCData(TransactionManagerConfig logData, String processId) throws JAXBException, IOException{
        File file = new File(xmlTransactionManagerConfig + "_" +processId+".xml");
        if (marshaller == null) {
                initMarshaller ();
        }
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        if(file.exists()){
        marshaller.marshal(logData, file);
        } else{
             file.createNewFile();
             marshaller.marshal(logData, file);
        }
    }

    public static void marshallRCData(ReplicaConfig logData, String processId) throws JAXBException, IOException{
        File file = new File(xmlReplicaConfig + "_" +processId+".xml");
        if (marshaller == null) {
                initMarshaller ();
        }
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        if(file.exists()){
        marshaller.marshal(logData, file);
        } else{
             file.createNewFile();
             marshaller.marshal(logData, file);
        }
    }
}
