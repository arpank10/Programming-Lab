package CocoCola;

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

    public void runUnit(int timeToObserve){
        Thread thread = new Thread(() -> {
            int timeForBottle = 0;
            while(localTime <= timeToObserve) {
                if(timeForBottle == Constants.TIME_TO_PACKAGE){
                    Bottle bottle = mainSystem.getNextBottleForPackagingUnit(priority);
                    bottle = packageBottle(bottle);
                    timeForBottle = 0;
                    mainSystem.handleBottle(bottle);
                }
                if(mainSystem.getGlobalTime() == localTime){
                    System.out.println("TIME IN LOCAL UNIT = " + localTime+1);
                    timeForBottle++;
                    localTime++;
                }
                mainSystem.setGlobalTime(1);
            }
        });
        thread.start();
    }
}