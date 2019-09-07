package CocoCola;

public class SealingUnit {
    private int priority;
    private int localTime;
    private BottleManufacturingSystem mainSystem;


    public SealingUnit(BottleManufacturingSystem mainSystem){
        priority = 1;
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

    public void runUnit(int timeToObserve){
        Thread thread = new Thread(() -> {
            int timeForBottle = 0;
            while(localTime <= timeToObserve) {
                if(timeForBottle == Constants.TIME_TO_SEAL){
                    Bottle bottle = mainSystem.getNextBottleForSealingUnit(priority);
                    bottle = sealBottle(bottle);
                    timeForBottle = 0;
                    mainSystem.handleBottle(bottle);
                }
                if(mainSystem.getGlobalTime() == localTime){
                    System.out.println("TIME IN LOCAL UNIT FOR SEALING UNIT = " + localTime+1);
                    timeForBottle++;
                    localTime++;
                }
                mainSystem.setGlobalTime(2);
            }
        });
        thread.start();
    }
}
