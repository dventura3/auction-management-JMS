/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.XMLGen.QueueItem;
import org.XMLGen.SingleBidAuction;
import org.XMLGen.SingleQueue;
import org.XMLGen.SingleQueueTwoPC;
import org.XMLGen.SingleStatusAuction;
import org.XMLGen.TaskItem;
import org.dto.ActionTYPE;
import org.dto.AuctionBid;
import org.dto.AuctionStatus;
import org.dto.AuctionTYPE;
import org.dto.OSTYPE;
import org.dto.QueueItemDTO;
import org.dto.RequestTYPE;
import org.dto.TaskDescriptor;
import org.dto.TaskTYPE;
import org.dto.VectorClock;
import org.dto.Workflow;

/**
 * Utility class che prepara i dati in base all'input
 * @author marcx87
 */
public class MessageUtil {
    /**
     * Converte gli item dto ai dati del modello.
     * @param queueItem Dato da convertire.
     * @return Il dato convertito.
     */
    public static SingleQueue prepareManagedQueueData(QueueItemDTO queueItem){
        SingleQueue singleQueue = new SingleQueue();
        QueueItem qItem = new QueueItem();
        if(queueItem.getWorkflow() != null){
        Iterator<TaskDescriptor> it = queueItem.getWorkflow().getTasks().iterator();
        Iterator<String> ite;
        String inout;
        TaskDescriptor td;
        qItem.setID(queueItem.getWorkflow().getID());
        while(it.hasNext()){
            td = it.next();
            TaskItem taskItem = new TaskItem();
            taskItem.setID(td.getID());
            taskItem.setType(td.getType().toString());
            taskItem.setCommand(td.getCommand());
            taskItem.setCpuRequired(td.getCpuRequired());
            taskItem.setRamRequired(td.getRamRequired());
            taskItem.setSpaceRequired(td.getSpaceRequired());
            taskItem.setOperatingSystemRequired(td.getOperatingSystemRequired().toString());
            ite = td.getInputs().iterator();
            while(ite.hasNext()){
                inout = ite.next();
                taskItem.getInputs().add(inout);
            }
            ite = td.getOutputs().iterator();
            while(ite.hasNext()){
                inout = ite.next();
                taskItem.getOutputs().add(inout);
            }
            
            qItem.getTasks().add(taskItem);
        }
        singleQueue.setQueueID(queueItem.getID());
        singleQueue.setLastUpdateTime(queueItem.getTimestamp().toString());
        singleQueue.getWorkflows().add(qItem);
        singleQueue.setRequestType(queueItem.getRequestType().toString());
        } else{
            singleQueue.setQueueID(queueItem.getID());
            singleQueue.setLastUpdateTime(queueItem.getTimestamp().toString());
            singleQueue.setRequestType(queueItem.getRequestType().toString());
        }
        return singleQueue;
    }

    public static SingleQueueTwoPC prepareLog2PCData(QueueItemDTO queueItem){
        SingleQueueTwoPC singleQueue = new SingleQueueTwoPC();
        QueueItem qItem = new QueueItem();
        Iterator<TaskDescriptor> it = queueItem.getWorkflow().getTasks().iterator();
        Iterator<String> ite;
        String inout;
        TaskDescriptor td;
        qItem.setID(queueItem.getWorkflow().getID());
        while(it.hasNext()){
            td = it.next();
            TaskItem taskItem = new TaskItem();
            taskItem.setID(td.getID());
            taskItem.setType(td.getType().toString());
            taskItem.setCommand(td.getCommand());
            taskItem.setCpuRequired(td.getCpuRequired());
            taskItem.setRamRequired(td.getRamRequired());
            taskItem.setSpaceRequired(td.getSpaceRequired());
            taskItem.setOperatingSystemRequired(td.getOperatingSystemRequired().toString());
            ite = td.getInputs().iterator();
            while(ite.hasNext()){
                inout = ite.next();
                taskItem.getInputs().add(inout);
            }
            ite = td.getOutputs().iterator();
            while(ite.hasNext()){
                inout = ite.next();
                taskItem.getOutputs().add(inout);
            }
            qItem.getTasks().add(taskItem);
        }
        singleQueue.setQueueID(queueItem.getID());
        singleQueue.setLastUpdateTime(queueItem.getTimestamp().toString());
        singleQueue.getWorkflows().add(qItem);
        singleQueue.setRequestType(queueItem.getRequestType().toString());
        return singleQueue;
    }
    /**
	 * Converte i dati del modello in una lista di dati dto
	 * @param singleQueue
	 * @return queueItemDTOList
	 * @throws ParseException
	 */
    public static ArrayList<QueueItemDTO> prepareQueueItemsDTO (SingleQueue singleQueue) throws ParseException{
        ArrayList<QueueItemDTO> queueItemsDTO = new ArrayList<QueueItemDTO>();
        QueueItemDTO queueItemDTO;
        QueueItem queueItem;
        TaskItem ti;
        Workflow wf;
        TaskDescriptor td;
        Iterator<QueueItem> it = singleQueue.getWorkflows().iterator();
        Iterator<TaskItem> ite;
        while(it.hasNext()){
            queueItem = it.next();
            ite = queueItem.getTasks().iterator();
            wf = new Workflow();
            wf.setID(queueItem.getID());
            while(ite.hasNext()){
                ti= ite.next();
                td = new TaskDescriptor();
                td.setID(ti.getID());
                if(ti.getType().equals(TaskTYPE.Executable.toString())){
                    td.setType(TaskTYPE.Executable);
                } else{
                    td.setType(TaskTYPE.scriptBash);
                }
                td.setCommand(ti.getCommand());
                td.setCpuRequired(ti.getCpuRequired());
                td.setRamRequired(ti.getRamRequired());
                td.setSpaceRequired(ti.getSpaceRequired());
                td.setInputs(ti.getInputs());
                td.setOutputs(ti.getOutputs());
                if(ti.getOperantingSystemRequired().equals(OSTYPE.Unix.toString())){
                    td.setOperantingSystemRequired(OSTYPE.Unix);
                } else if(ti.getOperantingSystemRequired().equals(OSTYPE.Windows7.toString())){
                    td.setOperantingSystemRequired(OSTYPE.Windows7);
                } else if(ti.getOperantingSystemRequired().equals(OSTYPE.WindowsVista.toString())){
                    td.setOperantingSystemRequired(OSTYPE.WindowsVista);
                } else if(ti.getOperantingSystemRequired().equals(OSTYPE.WindowsXP.toString())){
                    td.setOperantingSystemRequired(OSTYPE.WindowsXP);
                }
                wf.addTask(td);

            }
            DateFormat formatter = new SimpleDateFormat("E MMM d HH:mm:ss z yyyy",new Locale("en"));
            Date inlineDate =formatter.parse(singleQueue.getLastUpdateTime());
            queueItemDTO = new QueueItemDTO(singleQueue.getQueueID(), wf, inlineDate);
            if(singleQueue.getRequestType().equals(RequestTYPE.Dequeue.toString())){
                queueItemDTO.setRequestType(RequestTYPE.Dequeue);
            } else if(singleQueue.getRequestType().equals(RequestTYPE.Enqueue.toString())){
                queueItemDTO.setRequestType(RequestTYPE.Enqueue);
            }
            queueItemsDTO.add(queueItemDTO);
        }
        return queueItemsDTO;
    }
      public static Pair<QueueItemDTO, TwoPCState> prepareQueueItemDTO2PC(SingleQueueTwoPC sq2pc) throws ParseException{
            Pair<QueueItemDTO, TwoPCState> result;
            TwoPCState state = null;
            QueueItemDTO queueItemDTO = null;
            QueueItem queueItem;
            TaskItem ti;
            Workflow wf;
            TaskDescriptor td;
            Iterator<QueueItem> it = sq2pc.getWorkflows().iterator();
            Iterator<TaskItem> ite;
            while(it.hasNext()){
                queueItem = it.next();
                ite = queueItem.getTasks().iterator();
                wf = new Workflow();
                wf.setID(queueItem.getID());
                while(ite.hasNext()){
                    ti= ite.next();
                    td = new TaskDescriptor();
                    td.setID(ti.getID());
                    if(ti.getType().equals(TaskTYPE.Executable.toString())){
                        td.setType(TaskTYPE.Executable);
                    } else{
                        td.setType(TaskTYPE.scriptBash);
                    }
                    td.setCommand(ti.getCommand());
                    td.setCpuRequired(ti.getCpuRequired());
                    td.setRamRequired(ti.getRamRequired());
                    td.setSpaceRequired(ti.getSpaceRequired());
                    td.setInputs(ti.getInputs());
                    td.setOutputs(ti.getOutputs());
                    if(ti.getOperantingSystemRequired().equals(OSTYPE.Unix.toString())){
                        td.setOperantingSystemRequired(OSTYPE.Unix);
                    } else if(ti.getOperantingSystemRequired().equals(OSTYPE.Windows7.toString())){
                        td.setOperantingSystemRequired(OSTYPE.Windows7);
                    } else if(ti.getOperantingSystemRequired().equals(OSTYPE.WindowsVista.toString())){
                        td.setOperantingSystemRequired(OSTYPE.WindowsVista);
                    } else if(ti.getOperantingSystemRequired().equals(OSTYPE.WindowsXP.toString())){
                        td.setOperantingSystemRequired(OSTYPE.WindowsXP);
                    }
                    wf.addTask(td);
                }
                 DateFormat formatter = new SimpleDateFormat("E MMM d HH:mm:ss z yyyy",new Locale("en"));
                 Date inlineDate =formatter.parse(sq2pc.getLastUpdateTime());
                queueItemDTO = new QueueItemDTO(sq2pc.getQueueID(), wf, inlineDate);
                if(!sq2pc.getReplicaManagers().isEmpty()){
                    queueItemDTO.setReplicaManager(sq2pc.getReplicaManagers());
                }
                if(sq2pc.getRequestType().equals(RequestTYPE.Dequeue.toString())){
                    queueItemDTO.setRequestType(RequestTYPE.Dequeue);
                } else if(sq2pc.getRequestType().equals(RequestTYPE.Enqueue.toString())){
                    queueItemDTO.setRequestType(RequestTYPE.Enqueue);
                }
                if(sq2pc.getTwoPCState().equals(TwoPCState.Abort.toString())){
                    state = TwoPCState.Abort;
                } else if(sq2pc.getTwoPCState().equals(TwoPCState.Commit.toString())){
                    state = TwoPCState.Commit;
                } else if(sq2pc.getTwoPCState().equals(TwoPCState.Complete.toString())){
                     state = TwoPCState.Complete;
                } else if(sq2pc.getTwoPCState().equals(TwoPCState.Prepare.toString())){
                    state = TwoPCState.Prepare;
                } else if(sq2pc.getTwoPCState().equals(TwoPCState.Ready.toString())){
                    state = TwoPCState.Ready;
                }
            }
            result = new Pair<QueueItemDTO, TwoPCState>(queueItemDTO, state);
            return result;
      }

      public static SingleStatusAuction prepareAuctionStatusLogData(AuctionStatus status, String processId){
          SingleStatusAuction ssa = new SingleStatusAuction();
          ssa.setAction(status.getAction().toString());
          ssa.setClientId(status.getClientId());
          ssa.setCurrentPrice(status.getCurrentPrice());
          ssa.setCurrentWinner(status.getCurrentWinner());
          ssa.setNumTaskDescriptor(status.getNumTaskDescriptor());
          ssa.setRandomValue(status.getRandomValue());
          ssa.setTypeAuction(status.getTypeAuction().toString());
          ssa.setWorkflowId(status.getWorkflowId());
          ssa.setLogicalClock(status.getVectorClock().get(processId));
          return ssa;
      }

      public static SingleBidAuction prepareAuctionBidLogData(AuctionBid bid, String processId){
          SingleBidAuction sba = new SingleBidAuction();
          sba.setAction(bid.getAction().toString());
          sba.setAuctionType(bid.getAuctionType().toString());
          sba.setManagerIP(bid.getManagerIP());
          sba.setManagerPort(bid.getManagerPort());
          sba.setManagerId(bid.getManagerId());
          sba.setNumTaskDescriptor(bid.getNumTaskDescriptor());
          sba.setPriceOffered(bid.getPriceOffered());
          sba.setWorkflowId(bid.getWorkflowId());
          sba.setLogicalClock(bid.getVectorClock().get(processId));
          return sba;
      }

      public static AuctionStatus prepareAuctionStatusDTO(SingleStatusAuction status, String processId){
          AuctionStatus as = new AuctionStatus();
          VectorClock vc = new VectorClock();
          vc.incrementClock(processId);
          vc.put(processId, status.getLogicalClock());
          if(status.getAction().equals(ActionTYPE.Status.toString())){
              as.setAction(ActionTYPE.Status);
          } else if(status.getAction().equals(ActionTYPE.EndAuction.toString())){
              as.setAction(ActionTYPE.EndAuction);
          }
          as.setClientId(status.getClientId());
          as.setCurrentPrice(status.getCurrentPrice());
          as.setCurrentWinner(status.getCurrentWinner());
          as.setNumTaskDescriptor(status.getNumTaskDescriptor());
          as.setRandomValue(status.getRandomValue());
          as.setWorkflowId(status.getWorkflowId());
          if(status.getTypeAuction().equals(AuctionTYPE.AscendingPriceAuction.toString())){
              as.setTypeAuction(AuctionTYPE.AscendingPriceAuction);
          }else if(status.getTypeAuction().equals(AuctionTYPE.DescendingPriceAuction.toString())){
              as.setTypeAuction(AuctionTYPE.DescendingPriceAuction);
          }
          as.setVectorClock(vc);
          return as;
      }

      public static AuctionBid prepareAuctionBidDTO(SingleBidAuction bid, String processId){
          AuctionBid ab = new AuctionBid();
          VectorClock vc = new VectorClock();
          vc.incrementClock(processId);
          vc.put(processId, bid.getLogicalClock());
          if(bid.getAction().equals(ActionTYPE.Bid.toString())){
              ab.setAction(ActionTYPE.Bid);
          }
          if(bid.getAuctionType().equals(AuctionTYPE.AscendingPriceAuction.toString())){
              ab.setAuctionType(AuctionTYPE.AscendingPriceAuction);
          } else if(bid.getAuctionType().equals(AuctionTYPE.DescendingPriceAuction.toString())){
              ab.setAuctionType(AuctionTYPE.DescendingPriceAuction);
          }
          ab.setManagerIP(bid.getManagerIP());
          ab.setManagerId(bid.getManagerId());
          ab.setManagerPort(bid.getManagerPort());
          ab.setNumTaskDescriptor(bid.getNumTaskDescriptor());
          ab.setPriceOffered(bid.getPriceOffered());
          ab.setWorkflowId(bid.getWorkflowId());
          ab.setVectorClock(vc);
          return ab;
      }
}
