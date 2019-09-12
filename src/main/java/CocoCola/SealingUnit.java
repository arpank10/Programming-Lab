package CocoCola;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class SealingUnit {
    private int priority;
    private int localTime;
    private BottleManufacturingSystem mainSystem;


    public SealingUnit(BottleManufacturingSystem mainSystem){
        priority = 2;
        localTime = 0;
        this.mainSystem = mainSystem;
    }

    private void togglePriority(){
        this.priority = priority == 1? 2:1;
    }

    private Bottle sealBottle(Bottle bottle){
        bottle.setSealed(true);
        bottle.setUnprocessed(false);
        togglePriority();
        return bottle;
    }


    public void runUnit(int timeToObserve, CyclicBarrier cyclicBarrier){
        Thread thread = new Thread(() -> {
            int timeForBottle = 0;
            Bottle bottle = null;
            while(localTime <= timeToObserve) {
                try {
                    if(timeForBottle == 0)
                        bottle = mainSystem.getNextBottleForSealingUnit(priority);
                    timeForBottle++;
                    localTime++;
                    if(timeForBottle == Constants.TIME_TO_SEAL ){
                        bottle = sealBottle(bottle);
                        boolean bottleHandled = mainSystem.handleBottle(bottle, 2);
                        timeForBottle = bottleHandled?0:Constants.TIME_TO_SEAL - 1;
                        priority = bottle.getBottleType() == BottleType.B1? 2:1;
                    }
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
