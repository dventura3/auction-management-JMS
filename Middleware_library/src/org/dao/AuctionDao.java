/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dao;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.bind.JAXBException;
import org.dto.AuctionBid;
import org.dto.AuctionStatus;

/**
 *
 * @author marcx87
 */
public interface AuctionDao extends MiddlewareDAO{
    /**
     * Metodo per salvare lo stato attuale di un’asta
     * @param status Stato dell'asta
     * @param processId Identificativo del processo che effettua il salvataggio
     * @throws JAXBException
     * @throws IOException
     */
    public void LogAuctionStatus(AuctionStatus status, String processId) throws JAXBException, IOException;
    /**
     * Metodo per salvare l’offerta corrente di una determinata asta
     * @param bid ultima Offerta dell'asta
     * @param processId Identificativo del processo che effettua il salvataggio
     * @throws JAXBException
     * @throws IOException
     */
    public void LogAuctionBid(AuctionBid bid, String processId) throws JAXBException, IOException;
    /**
     * Metodo per caricare lo stato di tutte le aste fin’ora sostenute
     * @param processId Identificativo del processo
     * @return Lista dello status delle varie aste sostenute
     * @throws JAXBException
     * @throws IOException
     */
    public ArrayList<AuctionStatus> getAllAuctionStatus(String processId) throws JAXBException, IOException;
    /**
     * Metodo per caricare le offerte ricevute durante tutte le aste a cui si è preso parte
     * @param processId Identificativo del processo
     * @return Lista delle offerte ricevute durante tutte le aste a cui si è preso parte
     * @throws JAXBException
     * @throws IOException
     */
    public ArrayList<AuctionBid> getAllAuctionBid(String processId) throws JAXBException, IOException;
    /**
     * Metodo per cancellare lo stato delle aste ormai completate
     * @param status Lo stato da eliminare
     * @param processId L'indificatio del processo
     * @throws JAXBException
     * @throws IOException
     */
    public void clearAuctionStatus(AuctionStatus status, String processId) throws JAXBException, IOException;
    /**
     * Metodo per cancellare le offerte relative ad aste ormai concluse
     * @param bid L'offerta da eliminare
     * @param processId Identificativo del processo
     * @throws JAXBException
     * @throws IOException
     */
    public void clearBidStatus(AuctionBid bid, String processId) throws JAXBException, IOException;
}
