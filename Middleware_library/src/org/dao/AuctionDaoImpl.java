/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.bind.JAXBException;
import org.XMLGen.AuctionBidLog;
import org.XMLGen.AuctionStatusLog;
import org.XMLGen.SingleBidAuction;
import org.XMLGen.SingleStatusAuction;
import org.dto.AuctionBid;
import org.dto.AuctionStatus;
import org.util.MessageUtil;
import org.util.XMLDataFactory;

/**
 *
 * @author marcx87
 */
public class AuctionDaoImpl implements AuctionDao{

    /**
     * Metodo per salvare lo stato attuale di un’asta
     * @param status Stato dell'asta
     * @param processId Identificativo del processo che effettua il salvataggio
     * @throws JAXBException
     * @throws IOException
     */
    public synchronized void LogAuctionStatus(AuctionStatus status, String processId) throws JAXBException, IOException {
       SingleStatusAuction singleStatusAuction = MessageUtil.prepareAuctionStatusLogData(status, processId);
       AuctionStatusLog rootElement;
       try{
           rootElement = XMLDataFactory.getRootASElement(processId);
       } catch(JAXBException ex){
           rootElement = new AuctionStatusLog();
                }
                if(rootElement.getSingleStatusAuction().contains(singleStatusAuction)){
                    int index =rootElement.getSingleStatusAuction().indexOf(singleStatusAuction);
                    if(rootElement.getSingleStatusAuction().get(index).getAction().equals(singleStatusAuction.getAction()) &&
                           rootElement.getSingleStatusAuction().get(index).getCurrentPrice() == singleStatusAuction.getCurrentPrice() &&
                           rootElement.getSingleStatusAuction().get(index).getCurrentWinner().equals(singleStatusAuction.getCurrentWinner()) &&
                           rootElement.getSingleStatusAuction().get(index).getTypeAuction().equals(singleStatusAuction.getTypeAuction())){
                        return;
                    }
                    else{
                        rootElement.getSingleStatusAuction().remove(singleStatusAuction);
                        rootElement.getSingleStatusAuction().add(singleStatusAuction);
                    }
                } else{
                    rootElement.getSingleStatusAuction().add(singleStatusAuction);
                }

		// store the new data
		XMLDataFactory.marshallLogASData(rootElement, processId);
    }
    /**
     * Metodo per salvare l’offerta corrente di una determinata asta
     * @param bid ultima Offerta dell'asta
     * @param processId Identificativo del processo che effettua il salvataggio
     * @throws JAXBException
     * @throws IOException
     */
    public synchronized void LogAuctionBid(AuctionBid bid, String processId) throws JAXBException, IOException {
       SingleBidAuction singleBidAuction = MessageUtil.prepareAuctionBidLogData(bid, processId);
       AuctionBidLog rootElement;
       try{
           rootElement = XMLDataFactory.getRootABElement(processId);
       } catch(JAXBException ex){
           rootElement = new AuctionBidLog();
                }
                if(rootElement.getSingleBidAuction().contains(singleBidAuction)){
                    int index =rootElement.getSingleBidAuction().indexOf(singleBidAuction);
                    if(rootElement.getSingleBidAuction().get(index).getPriceOffered() == singleBidAuction.getPriceOffered() &&
                           rootElement.getSingleBidAuction().get(index).getAuctionType().equals(singleBidAuction.getAuctionType())){
                        return;
                    }
                    else{
                        rootElement.getSingleBidAuction().remove(singleBidAuction);
                        rootElement.getSingleBidAuction().add(singleBidAuction);
                    }
                } else{
                    rootElement.getSingleBidAuction().add(singleBidAuction);
                }
		// store the new data
		XMLDataFactory.marshallLogABData(rootElement, processId);
    }
    /**
     * Metodo per caricare lo stato di tutte le aste fin’ora sostenute
     * @param processId Identificativo del processo
     * @return Lista dello status delle varie aste sostenute
     * @throws JAXBException
     * @throws IOException
     */
    public ArrayList<AuctionStatus> getAllAuctionStatus(String processId) throws JAXBException, IOException {
        ArrayList<AuctionStatus> result = new ArrayList<AuctionStatus>();
        AuctionStatus as;
        AuctionStatusLog rootElement;
        SingleStatusAuction ssa;
        try{
           rootElement = XMLDataFactory.getRootASElement(processId);
        } catch(JAXBException ex){
           rootElement = new AuctionStatusLog();
        }
        Iterator<SingleStatusAuction> it = rootElement.getSingleStatusAuction().iterator();
        while(it.hasNext()){
            ssa = it.next();
            as = MessageUtil.prepareAuctionStatusDTO(ssa, processId);
            result.add(as);
        }
        return result;
    }
    /**
     * Metodo per caricare le offerte ricevute durante tutte le aste a cui si è preso parte
     * @param processId Identificativo del processo
     * @return Lista delle offerte ricevute durante tutte le aste a cui si è preso parte
     * @throws JAXBException
     * @throws IOException
     */
    public ArrayList<AuctionBid> getAllAuctionBid(String processId) throws JAXBException, IOException {
        ArrayList<AuctionBid> result = new ArrayList<AuctionBid>();
        AuctionBid ab;
        AuctionBidLog rootElement;
        SingleBidAuction sba;
        try{
           rootElement = XMLDataFactory.getRootABElement(processId);
        } catch(JAXBException ex){
           rootElement = new AuctionBidLog();
        }
        Iterator<SingleBidAuction> it = rootElement.getSingleBidAuction().iterator();
        while(it.hasNext()){
            sba = it.next();
            ab = MessageUtil.prepareAuctionBidDTO(sba, processId);
            result.add(ab);
        }
        return result;
    }
    /**
     * Metodo per cancellare lo stato delle aste ormai completate
     * @param status Lo stato da eliminare
     * @param processId L'indificatio del processo
     * @throws JAXBException
     * @throws IOException
     */
    public synchronized void clearAuctionStatus(AuctionStatus status, String processId) throws JAXBException, IOException {
        SingleStatusAuction sas = MessageUtil.prepareAuctionStatusLogData(status, processId);
        AuctionStatusLog rootElement;
        try{
            rootElement = XMLDataFactory.getRootASElement(processId);
        }catch(JAXBException ex){
            rootElement = new AuctionStatusLog();
        }
        if(rootElement.getSingleStatusAuction().contains(sas)){
            rootElement.getSingleStatusAuction().remove(sas);
        }
        XMLDataFactory.marshallLogASData(rootElement, processId);
    }
    /**
     * Metodo per cancellare le offerte relative ad aste ormai concluse
     * @param bid L'offerta da eliminare
     * @param processId Identificativo del processo
     * @throws JAXBException
     * @throws IOException
     */
    public synchronized void clearBidStatus(AuctionBid bid, String processId) throws JAXBException, IOException {
        SingleBidAuction sba = MessageUtil.prepareAuctionBidLogData(bid, processId);
        AuctionBidLog rootElement;
        try{
            rootElement = XMLDataFactory.getRootABElement(processId);
        }catch(JAXBException ex){
            rootElement = new AuctionBidLog();
        }
        if(rootElement.getSingleBidAuction().contains(sba)){
            rootElement.getSingleBidAuction().remove(sba);
        }
        XMLDataFactory.marshallLogABData(rootElement, processId);
    }
}
