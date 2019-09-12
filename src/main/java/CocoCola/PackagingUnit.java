package CocoCola;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class PackagingUnit {
    private int priority;
    private int localTime;
    private BottleManufacturingSystem mainSystem;



    public PackagingUnit(BottleManufacturingSystem mainSystem){
        localTime = 0;
        priority = 1;
        this.mainSystem = mainSystem;
    }

    private void togglePriority(){
        this.priority = priority == 1? 2:1;
    }

    private Bottle packageBottle(Bottle bottle){
        bottle.setPackaged(true);
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
                        bottle = mainSystem.getNextBottleForPackagingUnit(priority);
                    timeForBottle++;
                    localTime++;
                    if(timeForBottle == Constants.TIME_TO_PACKAGE){
                        bottle = packageBottle(bottle);
                        boolean bottleHandled = mainSystem.handleBottle(bottle, 1);
                        timeForBottle = bottleHandled?0:Constants.TIME_TO_PACKAGE - 1;
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