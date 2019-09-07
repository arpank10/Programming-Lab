package CocoCola;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BottleManufacturingSystem {
    private int timeToObserveSystem;
    private int bottleTypeB1;
    private int bottleTypeB2;
    private int globalTime;

    private int finishedBottles;
    private int flagForSealingUnit = 0;
    private int flagForPackagingUnit = 0;

    private List<Bottle> unfinishedB1tray;
    private List<Bottle> unfinishedB2tray;

    private List<Bottle> sealedTrayB1;
    private List<Bottle> sealedTrayB2;

    private List<Bottle> packagedTrays;


    private PackagingUnit packagingUnit;
    private SealingUnit sealingUnit;

    public BottleManufacturingSystem(){
        unfinishedB1tray = new ArrayList<>();
        unfinishedB2tray = new ArrayList<>();

        sealedTrayB1 = new ArrayList<>(2);
        sealedTrayB2 = new ArrayList<>(3);

        packagedTrays = new ArrayList<>(2);

        packagingUnit = new PackagingUnit(this);
        sealingUnit = new SealingUnit(this);

        globalTime = 0;
        finishedBottles = 0;
    }

    public void startSytem() {
        takeInput();
        initBottles();

        runSystem();
    }

    private void takeInput() {
        System.out.println("Enter bottles of type B1, B2 and time to Observe");
        Scanner reader = new Scanner(System.in);
        //Setting number of unprocessed bottles in unfinished tray
        bottleTypeB1 = reader.nextInt();
        bottleTypeB2 = reader.nextInt();

        //time to observer the system
        timeToObserveSystem = reader.nextInt();
    }

    private void initBottles(){
        //Adding bottles of type B1 in tray
        for(int i=0;i<bottleTypeB1;i++)
            unfinishedB1tray.add(new Bottle(i+1, BottleType.B1));
        //Adding bottles of type B2 in tray
        for(int i=0;i<bottleTypeB2;i++)
            unfinishedB2tray.add(new Bottle(i+1, BottleType.B2));
    }

    private void runSystem(){
        while(globalTime <= timeToObserveSystem){
            packagingUnit.runUnit(timeToObserveSystem);
            sealingUnit.runUnit(timeToObserveSystem);
            globalTime++;
        }
    }

    public Bottle getNextBottleForPackagingUnit(int priority){
        Bottle nextBottleToPack = null;
        switch (priority) {
            case 1:
                if (!sealedTrayB1.isEmpty()) {
                    nextBottleToPack = sealedTrayB1.get(0);
                } else if (!unfinishedB1tray.isEmpty()) {
                    nextBottleToPack = unfinishedB1tray.get(0);
                }
                break;
            case 2:
                if (!sealedTrayB2.isEmpty()) {
                    nextBottleToPack = sealedTrayB2.get(0);
                }
                else if(!unfinishedB2tray.isEmpty()) {
                    nextBottleToPack = unfinishedB2tray.get(0);
                }
                break;
            default: nextBottleToPack =  null;
        }
        return nextBottleToPack;
    }

    public Bottle getNextBottleForSealingUnit(int priority){
        Bottle nextBottleToSeal = null;
        if(!packagedTrays.isEmpty())
            nextBottleToSeal = packagedTrays.get(0);
        else{
            switch (priority) {
                case 1:
                    if (!unfinishedB1tray.isEmpty()) {
                        nextBottleToSeal = unfinishedB1tray.get(0);
                    }
                    break;
                case 2:
                    if(!unfinishedB2tray.isEmpty()) {
                        nextBottleToSeal = unfinishedB2tray.get(0);
                    }
                    break;
                default: nextBottleToSeal =  null;
            }
        }
        return nextBottleToSeal;
    }

    public void handleBottle(Bottle bottle){
        if(bottle.isPackaged() && bottle.isSealed()){
            bottle.setInGoDown(true);
            finishedBottles++;
        }
        else if(bottle.isSealed()){
            if(bottle.getBottleType().equals(BottleType.B1))
                sealedTrayB1.add(bottle);
            else sealedTrayB2.add(bottle);
        }
        else if(bottle.isPackaged()){
            packagedTrays.add(bottle);
        }
    }

    public int getGlobalTime(){
        if(flagForPackagingUnit == 1 && flagForSealingUnit == 1){
            globalTime++;
            flagForSealingUnit = 0;
            flagForPackagingUnit = 0;
        }
     return globalTime;
    }

    public void setGlobalTime(int callingUnit){
        if(callingUnit == 1)
            flagForPackagingUnit = 1;
        else if(callingUnit == 2)
            flagForSealingUnit = 1;
    }
}
