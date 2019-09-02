package CocoCola;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BottleManufacturingSystem {
    private int timeToObserveSystem;
    private int bottleTypeB1;
    private int bottleTypeB2;
    private int globalTime;

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

        packagingUnit = new PackagingUnit();
        sealingUnit = new SealingUnit();

        globalTime = 0;
    }

    private void startSytem() {
        takeInput();
        initBottles();
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
}
