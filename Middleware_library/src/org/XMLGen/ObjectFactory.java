/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.XMLGen;

import org.dto.Workflow;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the xmlgen package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.</p>
 * @author marcx87
 */
@XmlRegistry
public class ObjectFactory {
    private static final QName _MANAGED_QUEUES_QNAME = new QName("managedQueus");
    private static final QName _2PC_QUEUE_QNAME = new QName("twoPhaseCommitLog");
    private static final QName _AUCTION_STATUS_LOG_QNAME = new QName("auctionStatusLog");
    private static final QName _AUCTION_BID_LOG_QNAME = new QName("auctionBidLog");
    private static final QName _MANAGER_LOG_DATA_QNAME = new QName("managerLog");
    private static final QName _CLIENT_LOG_DATA_QNAME = new QName("clientLog");
    private static final QName _TRANSACTION_MANAGER_CONFIG_QNAME = new QName("transactionManagerConfig");
    private static final QName _REPLICA_CONFIG_QNAME = new QName("replicaConfig");
    /**
     * Create a new ObjectFactory that can be used to create new
     * instances of schema derived classes for package xmlgen
     */
    public ObjectFactory() {
    }
    /**
     * Create an instance of {@link ManagedQueues }
     * @return Instance of {@link ManagedQueues }
     */
    public ManagedQueues createManagedQueues() {
        return new ManagedQueues();
    }
    /**
     * Create an instance of {@link TwoPhaseCommitLog }
     * @return Instance of {@link TwoPhaseCommitLog }
     */
    public TwoPhaseCommitLog createTwoPhaseCommitLog(){
        return new TwoPhaseCommitLog();
    }
     /**
     * Create an instance of {@link AuctionStatusLog }
     * @return Instance of {@link AuctionStatusLog }
     */
    public AuctionStatusLog createAuctionStatusLog(){
        return new AuctionStatusLog();
    }
    /**
      * Create an instance of {@link AuctionBidLog }
     * @return Instance of {@link AuctionBidLog }
     */
    public AuctionBidLog createAuctionBidLog(){
        return new AuctionBidLog();
    }
    /**
     * Create an instance of {@link ManagerLog }
     * @return Instance of {@link ManagerLog }
     */
    public ManagerLog createManagerLog(){
        return new ManagerLog();
    }
    /**
     * Create an instance of {@link TransactionManagerConfig }
     * @return Instance of {@link TransactionManagerConfig }
     */
    public TransactionManagerConfig createTransactionManagerConfig(){
        return new TransactionManagerConfig();
    }
    /**
     * Create an instance of {@link ReplicaConfig }
     * @return Instance of {@link ReplicaConfig }
     */
    public ReplicaConfig createReplicaConfig(){
        return new ReplicaConfig();
    }
    /**
     * Create an instance of {@link ClientLog }
     * @return Instance of {@link ClientLog }
     */
    public ClientLog createClientLog(){
        return new ClientLog();
    }
    /**
      * Create an instance of {@link SingleQueue }
     * @return Instance of {@link SingleQueue }
     */
    public SingleQueue createSingleQueue(){
        return new SingleQueue();
    }
    /**
      * Create an instance of {@link SingleQueueTwoPC }
     * @return Instance of {@link SingleQueueTwoPC }
     */
    public SingleQueueTwoPC createSingleQueueTwoPC(){
        return new SingleQueueTwoPC();
    }
    /**
      * Create an instance of {@link QueueItem }
     * @return Instance of {@link QueueItem }
     */
    public QueueItem createWorkflow(){
        return new QueueItem();
    }
    /**
      * Create an instance of {@link TaskItem }
     * @return Instance of {@link TaskItem }
     */
    public TaskItem createTaskDescriptor(){
        return new TaskItem();
    }
    /**
     * Create an instance of {@link JAXBElement }
     * {@code <}{@link ManagedQueues }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ManagedQueues")
    public JAXBElement<ManagedQueues> createManagedElement(ManagedQueues value) {
        return new JAXBElement<ManagedQueues>(_MANAGED_QUEUES_QNAME, ManagedQueues.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }
     * {@code <}{@link TwoPhaseCommitLog }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "TwoPhaseCommitLog")
    public JAXBElement<TwoPhaseCommitLog> createTwoPhaseCommitLogElement(TwoPhaseCommitLog value){
        return new JAXBElement<TwoPhaseCommitLog>(_2PC_QUEUE_QNAME, TwoPhaseCommitLog.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }
     * {@code <}{@link AuctionStatusLog }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "AuctionStatusLog")
    public JAXBElement<AuctionStatusLog> createAuctionStatusLogElement(AuctionStatusLog value){
        return new JAXBElement<AuctionStatusLog>(_AUCTION_STATUS_LOG_QNAME, AuctionStatusLog.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }
     * {@code <}{@link AuctionBidLog }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "AuctionBidLog")
    public JAXBElement<AuctionBidLog> createAuctionBidLogElement(AuctionBidLog value){
        return new JAXBElement<AuctionBidLog>(_AUCTION_BID_LOG_QNAME, AuctionBidLog.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }
     * {@code <}{@link ManagerLog }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ManagerLogData")
    public JAXBElement<ManagerLog> createManagerLogElement(ManagerLog value){
        return new JAXBElement<ManagerLog>(_MANAGER_LOG_DATA_QNAME, ManagerLog.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }
     * {@code <}{@link ClientLog }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ClientLogData")
    public JAXBElement<ClientLog> createClientLogElement(ClientLog value){
        return new JAXBElement<ClientLog>(_CLIENT_LOG_DATA_QNAME, ClientLog.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }
     * {@code <}{@link TransactionManagerConfig }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "TransactionManagerConfig")
    public JAXBElement<TransactionManagerConfig> createTransactionManagerConfigElement(TransactionManagerConfig value){
        return new JAXBElement<TransactionManagerConfig>(_TRANSACTION_MANAGER_CONFIG_QNAME, TransactionManagerConfig.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }
     * {@code <}{@link ReplicaConfig }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ReplicaConfig")
    public JAXBElement<ReplicaConfig> createReplicaConfigElement(ReplicaConfig value){
        return new JAXBElement<ReplicaConfig>(_REPLICA_CONFIG_QNAME, ReplicaConfig.class, null, value);
    }
}
