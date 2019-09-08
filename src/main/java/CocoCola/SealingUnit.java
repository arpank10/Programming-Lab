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
            Bottle bottle = mainSystem.getNextBottleForSealingUnit(priority);
            while(localTime <= timeToObserve) {
                try {
                    timeForBottle++;
                    localTime++;
                    if(timeForBottle == Constants.TIME_TO_SEAL && bottle != null){
                        bottle = sealBottle(bottle);
                        boolean bottleHandled = mainSystem.handleBottle(bottle, 2);
                        timeForBottle = 0;
                        cyclicBarrier.await();
                        priority = bottle.getBottleType() == BottleType.B1? 2:1;
                        bottle = bottleHandled ? mainSystem.getNextBottleForSealingUnit(priority) : bottle;
                    } else cyclicBarrier.await();
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
