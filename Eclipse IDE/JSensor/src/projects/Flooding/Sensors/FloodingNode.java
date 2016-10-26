package projects.Flooding.Sensors;

import java.util.LinkedList;

import jsensor.runtime.Jsensor;
import jsensor.nodes.Node;
import jsensor.nodes.messages.Inbox;
import jsensor.nodes.messages.Message;
import projects.Flooding.Messages.FloodingMessage;
import projects.Flooding.Timers.FloodingTimer;


/**
 *
 * @author danniel & Matheus
 */
public class FloodingNode extends Node{
	public static int discharge = 0;
    public LinkedList<Long> messagesIDs;
    private int energy;

    @Override
    public void handleMessages(Inbox inbox) {    	
    	
    	if(this.energy <= 0) {
    		return;
    	}
    	
    	while(inbox.hasMoreMessages())
       {
    	   
           Message message = inbox.getNextMessage();
           this.energy -= 1;
           
           if(energy <= 0) {
        	   FloodingNode.discharge += 1;
        	   Jsensor.log("Sensor " + this.ID + " without energy. discharge:" + FloodingNode.discharge);
        	   break;
           }
           
          
           if(message instanceof FloodingMessage)
           {
        	   FloodingMessage floodingMessage = (FloodingMessage) message;
        	     
        	   int h = floodingMessage.getHops();
        	   if(h>20){
        		   //System.out.println("Message dead");
        		   continue;
        	   }
        	   	
               if(this.messagesIDs.contains(floodingMessage.getID()))
               {
                   continue;
               }
               
               this.messagesIDs.add(floodingMessage.getID());
               
               if(floodingMessage.getDestination().equals(this))
               {
            	   Jsensor.log("time: "+ Jsensor.currentTime +
            			   "\t sensorID: " +this.ID+
            			   "\t receivedFrom: " +floodingMessage.getSender().getID()+
            			   "\t hops: "+ floodingMessage.getHops() +
            			   "\t msg: " +floodingMessage.getMsg().concat(this.ID+""));
               }
               else
               {
				    int n = 999999;
				    int cont=0;
				    for (int i=1;i<=n;i++ ){
				    	if(n%i == 0)
				    		cont=cont+1;
				    }
				    
				    if (cont > 0){
				    	this.energy -= 2;
	            	   floodingMessage.setMsg(floodingMessage.getMsg().concat(this.ID+ " - "));
	                   this.multicast(message);
				    }
               }
           }        
       }
    }

    @Override
    public void onCreation() 
    {
    	//initializes the list of messages received by the node.
        this.messagesIDs = new LinkedList<Long>();
        
        //initializes the sensor with full energy.
        this.energy = 100;
 
        //sends the first messages if is one of the selected nodes
        if(this.ID < 10)
        {
        	int time = 10 + this.ID * 10;
        	FloodingTimer ft = new FloodingTimer();
            ft.startRelative(time, this);
        }
    }
}
