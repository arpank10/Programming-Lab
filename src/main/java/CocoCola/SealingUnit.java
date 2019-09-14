package CocoCola;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

//Contains the architecture of the packaging unit which runs in a different thread altogether
public class SealingUnit {

    //Priority as to which type of bottle it wants next
    private int priority;

    //A variable which determines the time in the sealing unit
    private int localTime;

    //We need some functions and lists from the mainSystem
    private BottleManufacturingSystem mainSystem;

    //Initialize the variables in the constructor
    public SealingUnit(BottleManufacturingSystem mainSystem){
        priority = 2;
        localTime = 0;
        this.mainSystem = mainSystem;
    }

    //Toggle the priority to get alternate types of bottles
    private void togglePriority(){
        this.priority = priority == 1? 2:1;
    }

    //Seal a bottle
    //Mark as sealed
    private Bottle sealBottle(Bottle bottle){
        bottle.setSealed(true);
        bottle.setUnprocessed(false);
        togglePriority();
        return bottle;
    }

    //The run function which runs in a new thread
    //timeToObserve is the time for which the system needs to be observed
    public void runUnit(int timeToObserve, CyclicBarrier cyclicBarrier){
        Thread thread = new Thread(() -> {
            int timeForBottle = 0;
            Bottle bottle = null;

            //Run the loop till time to observe
            while(localTime <= timeToObserve) {
                try {
                    //Get a new bottle to seal
                    if(timeForBottle == 0)
                        bottle = mainSystem.getNextBottleForSealingUnit(priority);

                    //Increment time for that bottle and the localTime
                    timeForBottle++;
                    localTime++;

                    //Sealing finished
                    //Try to send it to tray, if successfully pushed to buffer tray of packaging or godown, bottleHandled is true, else false
                    //If bottleHandled, then togglePriority and get the next bottle
                    //Else try to push the same bottle in the next second by decreasing timeForBottle by 1
                    if(timeForBottle == Constants.TIME_TO_SEAL ){
                        bottle = sealBottle(bottle);
                        boolean bottleHandled = mainSystem.handleBottle(bottle, 2);
                        timeForBottle = bottleHandled?0:Constants.TIME_TO_SEAL - 1;
                        priority = bottle.getBottleType() == BottleType.B1? 2:1;
                    }

                    //Sealing unit is done for this second, waits here till the Packaging unit is done for the second
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
