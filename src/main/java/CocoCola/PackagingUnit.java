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
            Bottle bottle = mainSystem.getNextBottleForPackagingUnit(priority);
            while(localTime <= timeToObserve) {
                try {
                    timeForBottle++;
                    localTime++;
                    if(timeForBottle == Constants.TIME_TO_PACKAGE && bottle!=null){
                        bottle = packageBottle(bottle);
                        boolean bottleHandled = mainSystem.handleBottle(bottle, 1);
                        timeForBottle = bottleHandled?0:Constants.TIME_TO_PACKAGE - 1;
                        cyclicBarrier.await();
                        priority = bottle.getBottleType() == BottleType.B1? 2:1;
                        bottle = bottleHandled? mainSystem.getNextBottleForPackagingUnit(priority) : bottle;
                    }
                    else cyclicBarrier.await();
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