package CocoCola;

import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class BottleManufacturingSystem {

    //Variables taken as input
    private int timeToObserveSystem;
    private int bottleTypeB1;
    private int bottleTypeB2;

    //Global Time variable which keeps track of time
    private int globalTime;

    //Keeps track of the number of bottles packaged/selaed/in godown
    private int finishedBottlesB1;
    private int finishedBottlesB2;
    private int packagedBottlesB1;
    private int packagedBottlesB2;
    private int sealedBottlesB1;
    private int sealedBottlesB2;

    //Unfinished trays for B1 and B2
    private CopyOnWriteArrayList<Bottle> unfinishedB1tray;
    private CopyOnWriteArrayList<Bottle> unfinishedB2tray;

    //Trays for packaging unit with size 2(B1) and 3(B2)
    private CopyOnWriteArrayList<Bottle> sealedTrayB1;
    private CopyOnWriteArrayList<Bottle> sealedTrayB2;

    //Tray for sealing unit with size 2
    private CopyOnWriteArrayList<Bottle> packagedTrays;

    //The two units
    private PackagingUnit packagingUnit;
    private SealingUnit sealingUnit;

    //Synchronization variables
    ReentrantLock lock;
    CyclicBarrier cyclicBarrier;


    //Initialize all in constuctor
    public BottleManufacturingSystem(){
        unfinishedB1tray = new CopyOnWriteArrayList<>();
        unfinishedB2tray = new CopyOnWriteArrayList<>();

        sealedTrayB1 = new CopyOnWriteArrayList<>();
        sealedTrayB2 = new CopyOnWriteArrayList<>();

        packagedTrays = new CopyOnWriteArrayList<>();

        packagingUnit = new PackagingUnit(this);
        sealingUnit = new SealingUnit(this);

        lock = new ReentrantLock();

        //When both threads have finished the work for a second
        //Cyclic barrier increase the global time
        //Initialized with 2(number of units/threads) and a runnable that increases the global time to keep track of time
        //This ensures that the time is same in both the threads
        cyclicBarrier = new CyclicBarrier(2, () -> {
            if(globalTime == timeToObserveSystem)
                printStatus();
            globalTime++;
        });

        globalTime = 1;
        finishedBottlesB1 = 0;
        finishedBottlesB2 = 0;
        packagedBottlesB1 = 0;
        packagedBottlesB2 = 0;
        sealedBottlesB1 = 0;
        sealedBottlesB2 = 0;
    }

    //Function to get the system started
    public void startSystem() {
        takeInput();
        initBottles();
        runSystem();
    }

    //Function which takes input in the form "B1 B2 time"
    private void takeInput() {
        System.out.println("Enter bottles of type B1, B2 and time to Observe");
        Scanner reader = new Scanner(System.in);
        //Setting number of unprocessed bottles in unfinished tray
        bottleTypeB1 = reader.nextInt();
        bottleTypeB2 = reader.nextInt();

        //time to observer the system
        timeToObserveSystem = reader.nextInt();
    }

    //Push the bottles in the unfinished trays
    private void initBottles(){
        //Adding bottles of type B1 in tray
        for(int i=0;i<bottleTypeB1;i++)
            unfinishedB1tray.add(new Bottle(i+1, BottleType.B1));
        //Adding bottles of type B2 in tray
        for(int i=0;i<bottleTypeB2;i++)
            unfinishedB2tray.add(new Bottle(bottleTypeB1+i+1, BottleType.B2));
    }

    //Start both the systems
    private void runSystem(){
        packagingUnit.runUnit(timeToObserveSystem, cyclicBarrier);
        sealingUnit.runUnit(timeToObserveSystem, cyclicBarrier);
    }

    //This gets the next bottle to be packaged from the trays
    //If sealedTrayB1 and sealedTrayB2 are not empty then take from them
    //Otherwise take from unfinished tray
    //Take in order of priority, if empty take the other
    public Bottle getNextBottleForPackagingUnit(int priority){
        Bottle nextBottleToPack = null;
        switch (priority) {
            case 1:
                if (!sealedTrayB1.isEmpty()) {
                    nextBottleToPack = sealedTrayB1.get(0);
                    sealedTrayB1.remove(0);
                } else if (!sealedTrayB2.isEmpty()){
                    nextBottleToPack = sealedTrayB2.get(0);
                    sealedTrayB2.remove(0);
                }
                else if (!unfinishedB1tray.isEmpty()) {
                    nextBottleToPack = unfinishedB1tray.get(0);
                    unfinishedB1tray.remove(0);
                }
                else if(!unfinishedB2tray.isEmpty()) {
                    nextBottleToPack = unfinishedB2tray.get(0);
                    unfinishedB2tray.remove(0);
                }
                break;
            case 2:
                if (!sealedTrayB2.isEmpty()) {
                    nextBottleToPack = sealedTrayB2.get(0);
                    sealedTrayB2.remove(0);
                }
                else if (!sealedTrayB1.isEmpty()) {
                    nextBottleToPack = sealedTrayB1.get(0);
                    sealedTrayB1.remove(0);
                }
                else if(!unfinishedB2tray.isEmpty()) {
                    nextBottleToPack = unfinishedB2tray.get(0);
                    unfinishedB2tray.remove(0);
                }
                else if (!unfinishedB1tray.isEmpty()) {
                    nextBottleToPack = unfinishedB1tray.get(0);
                    unfinishedB1tray.remove(0);
                }
                break;
            default: nextBottleToPack =  null;
        }
        return nextBottleToPack;
    }

    //This gets the next bottle to be sealed from the trays
    //If packagedTray is not empty then take from them
    //Otherwise take from unfinished tray
    //Take in order of priority, if empty take the other
    public Bottle getNextBottleForSealingUnit(int priority){
        Bottle nextBottleToSeal = null;
        if(!packagedTrays.isEmpty()){
            nextBottleToSeal = packagedTrays.get(0);
            packagedTrays.remove(0);
        }
        else{
            switch (priority) {
                case 1:
                    if (!unfinishedB1tray.isEmpty()) {
                        nextBottleToSeal = unfinishedB1tray.get(0);
                        unfinishedB1tray.remove(0);
                    }
                    else if(!unfinishedB2tray.isEmpty()) {
                    nextBottleToSeal = unfinishedB2tray.get(0);
                    unfinishedB2tray.remove(0);
                }
                    break;
                case 2:
                    if(!unfinishedB2tray.isEmpty()) {
                        nextBottleToSeal = unfinishedB2tray.get(0);
                        unfinishedB2tray.remove(0);
                    }
                    else if (!unfinishedB1tray.isEmpty()) {
                        nextBottleToSeal = unfinishedB1tray.get(0);
                        unfinishedB1tray.remove(0);
                    }
                    break;
                default: nextBottleToSeal =  null;
            }
        }
        return nextBottleToSeal;
    }

    //This function is called by both the units once they are done with a bottle
    //The bottle if both sealed and packaged goes into the godown
    //The bottle if only sealed goes into one of the sealedTrays according to the type
    //The bottle if only packaged goes into the packagedTray
    //If the respective tray is full, this function returns false and in the next second, the unit again tries to push it
    //Otherwise the function returns true
    //For synchronization lock is acquired so that at a time only one unit is able to use this function
    public boolean handleBottle(Bottle bottle, int callingUnit){
        boolean bottleHandled = false;
        try{
            lock.lock();
            if(bottle.isPackaged() && bottle.isSealed()){
                bottle.setInGoDown(true);
                if(bottle.getBottleType() == BottleType.B1) {
                    if(callingUnit == 1) packagedBottlesB1++; else sealedBottlesB1++;
                    finishedBottlesB1++;
                }
                else {
                    if(callingUnit == 1) packagedBottlesB2++; else sealedBottlesB2++;
                    finishedBottlesB2++;
                }
                bottleHandled = true;
                System.out.println(globalTime);
                System.out.println("BOTTLE OF TYPE = " + bottle.getBottleType() + " is sealed and packaged of id " + bottle.getId());
            }
            else if(bottle.isSealed()){
                if(bottle.getBottleType().equals(BottleType.B1)) {
                    if(sealedTrayB1.size() < 2){
                        sealedTrayB1.add(bottle);
                        bottleHandled = true;
                        sealedBottlesB1++;
                        System.out.println(globalTime);
                        System.out.println("BOTTLE OF TYPE = " + bottle.getBottleType() + " is sealed of id " + bottle.getId());
                    }
                    else bottleHandled = false;
                }
                else {
                    if(sealedTrayB2.size() < 3){
                        sealedTrayB2.add(bottle);
                        sealedBottlesB2++;
                        bottleHandled = true;
                        System.out.println(globalTime);
                        System.out.println("BOTTLE OF TYPE = " + bottle.getBottleType() + " is sealed of id " + bottle.getId());
                    }
                    else bottleHandled = false;
                }
            }
            else if(bottle.isPackaged()){
                if(packagedTrays.size() < 2){
                    packagedTrays.add(bottle);
                    if(bottle.getBottleType() == BottleType.B1) packagedBottlesB1++;
                    else packagedBottlesB2++;
                    bottleHandled = true;
                    System.out.println(globalTime);
                    System.out.println("BOTTLE OF TYPE = " + bottle.getBottleType() + " is packaged of id " + bottle.getId());
                }
                else bottleHandled = false;
            }
        } finally {
            lock.unlock();
        }
        return bottleHandled;
    }

    //Print the final status of the bottles in a tabular format at the end of observed time
    private void printStatus(){
        System.out.println("-----------------------------------RESULTS--------------------------------------");
        System.out.println(String.format("%-20s %-20s %-20s","BOTTLETYPE", "STATUS", "COUNT"));
        System.out.println(String.format("%-20s %-20s %-20s","B1", "PACKAGED", packagedBottlesB1));
        System.out.println(String.format("%-20s %-20s %-20s","B1", "SEALED", sealedBottlesB1));
        System.out.println(String.format("%-20s %-20s %-20s","B1", "IN GODOWN", finishedBottlesB1));
        System.out.println(String.format("%-20s %-20s %-20s","B2", "PACKAGED", packagedBottlesB2));
        System.out.println(String.format("%-20s %-20s %-20s","B2", "SEALED", sealedBottlesB2));
        System.out.println(String.format("%-20s %-20s %-20s","B2", "IN GODOWN", finishedBottlesB2));
        System.out.println("---------------------------------------------------------------------------------");
        System.exit(0);
    }
}
