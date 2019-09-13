package TrafficLightOldSac;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.Phaser;

public class TrafficSystem {
    private TrafficLight T1SE, T2WS, T3EW;
    private List<Car> S2W, E2S, W2E;
    private int globalCarId = 0;
    private int globalTime = 0;
    private int currentTrafficLight = 1;
    Phaser phaser;
    GUI gui;

    public TrafficSystem(){
        T1SE = new TrafficLight();
        T2WS = new TrafficLight();
        T3EW = new TrafficLight();
        S2W = new ArrayList<>();
        E2S = new ArrayList<>();
        W2E = new ArrayList<>();
        gui = new GUI();
//        phaser = new Phaser(0);
    }

    public void startSystem() {
        startTrafficSystem();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("Entered");
                gui.setVisible(true);
            }
        });
        takeInput();
    }

    private void takeInput(){
        System.out.println("Enter Car or Show Status");

        Scanner reader = new Scanner(System.in);
        while(true){
            String lineInput = reader.nextLine();

            if(lineInput.contains(Constants.SHOW_STATUS_MESSAGE)){
//                phaser.register();
                printTrafficLightStatus();
                printCarStatus();
//                phaser.arriveAndAwaitAdvance();
//                phaser.arriveAndDeregister();
            }
            else addVehicle(lineInput);
        }
    }

    private void addVehicle(String input) {
//        phaser.register();
//        System.out.println(phaser.getRegisteredParties());
        List<String> tokens = Arrays.asList(input.split(" "));
        if(tokens.size() < 2) {
            System.out.println(Constants.WRONG_INPUT_FORMAT);
            return;
        }
        Direction sourceDirection = getDirection(tokens.get(0));
        Direction destinationDirection = getDirection(tokens.get(2));
        if(sourceDirection == null || destinationDirection == null) {
            System.out.println(Constants.WRONG_INPUT_FORMAT);
            return;
        }
        if(sourceDirection == destinationDirection){
            System.out.println(Constants.WRONG_DIRECTION_FORMAT);
            return;
        }
        Car car = new Car(++globalCarId, sourceDirection, destinationDirection, globalTime);
        assignTrafficLightToVehicles(car);
//        phaser.arriveAndAwaitAdvance();
//        phaser.arriveAndDeregister();
    }

    private void startTrafficSystem(){
        Runnable runnable = () -> {
//            phaser.register();
            T1SE.setCurrentLight(1);
            int timeForASingleVehicle = 0;
            Car carToBePassed = getCarToPass(getCurrentTrafficLight());
            while(true){
                try {
                    Thread.sleep(1000);
//                    phaser.arriveAndAwaitAdvance();
                    passCarsThatDontRequireSignal();
                    if(carToBePassed == null){
                        carToBePassed = getCarToPass(getCurrentTrafficLight());
                        if(carToBePassed != null){

                            System.out.println("Car fetched at time " + globalTime);
                            timeForASingleVehicle = 1;
                        }
                    }
                    else timeForASingleVehicle++;
                    globalTime++;
                    gui.updateTimer(globalTime);

                    if(timeForASingleVehicle == Constants.TIME_FOR_VEHICLE_TO_PASS && carToBePassed != null) {
                        getCurrentTrafficLight().handleCarPassing(carToBePassed);
                        gui.passCar(carToBePassed);
                        System.out.println(String.format("Car %s passed from %s to %s at time %s",
                                carToBePassed.getId(),
                                carToBePassed.getSourceDirection(),
                                carToBePassed.getDestinationDirection(),
                                globalTime));
                        carToBePassed = getCarToPass(getCurrentTrafficLight());
                        timeForASingleVehicle = 0;
                    }
                    if(globalTime % Constants.TIME_FOR_EACH_TRAFFIC_LIGHT == 0)
                    {
                        System.out.println(globalTime);
                        timeForASingleVehicle = 0;
                        currentTrafficLight = currentTrafficLight%3 + 1;
                        setTrafficLightStatus();
                        carToBePassed = getCarToPass(getCurrentTrafficLight());
                        gui.setTrafficLight(currentTrafficLight);
                    }
                } catch (InterruptedException e) {
                        e.printStackTrace();
//                        phaser.arriveAndDeregister();
                    }
            }
        };
        Thread trafficLightThread = new Thread(runnable);
        trafficLightThread.start();
    }

    private void passCarsThatDontRequireSignal(){
        for(Car car:S2W){
            if(car.getLeavingTime() == globalTime){
                car.setStatus(1);
                gui.passCar(car);
            }
            else if(car.getLeavingTime() > globalTime)
                break;
        }
        for(Car car:E2S){
            if(car.getLeavingTime() == globalTime){
                car.setStatus(1);
                gui.passCar(car);
            }
            else if(car.getLeavingTime() > globalTime)
                break;
        }
        for(Car car:W2E){
            if(car.getLeavingTime() == globalTime){
                car.setStatus(1);
                gui.passCar(car);
            }
            else if(car.getLeavingTime() > globalTime)
                break;
        }
    }


    private Car getCarToPass(TrafficLight trafficLight) {
        return trafficLight.getNextCarToPass();
    }

    private void setTrafficLightStatus() {
        if(currentTrafficLight == 1) {
            T1SE.setCurrentLight(1);
            T2WS.setCurrentLight(0);
            T3EW.setCurrentLight(0);
        }else if(currentTrafficLight == 2) {
            T1SE.setCurrentLight(0);
            T2WS.setCurrentLight(1);
            T3EW.setCurrentLight(0);
        }else if(currentTrafficLight == 3) {
            T1SE.setCurrentLight(0);
            T2WS.setCurrentLight(0);
            T3EW.setCurrentLight(1);
        }
    }

    private TrafficLight getCurrentTrafficLight() {
        if(currentTrafficLight == 1)
            return T1SE;
        if(currentTrafficLight == 2)
            return T2WS;
        if(currentTrafficLight == 3)
            return T3EW;
        return T1SE;
    }

    private Direction getDirection(String token) {
        switch (token.toUpperCase()){
            case "EAST":
                return Direction.EAST;
            case "WEST":
                return Direction.WEST;
            case "NORTH":
                return Direction.NORTH;
            case "SOUTH":
                return Direction.SOUTH;
            default: return null;
        }
    }

    private void assignTrafficLightToVehicles(Car car) {
        System.out.println("Enter Global Time " + globalTime);
        Direction sourceDirection = car.getSourceDirection();
        Direction destinationDirection = car.getDestinationDirection();
        int leavingTime = 6;
        if(sourceDirection == Direction.SOUTH && destinationDirection == Direction.EAST){
            if(currentTrafficLight == 3) leavingTime+= 60 - globalTime%60;
            else if(currentTrafficLight == 2) leavingTime+=120 - globalTime%60;
            leavingTime += T1SE.getWaitingCarsAtSignal().size() * 6;
            car.setLeavingTime(leavingTime + globalTime);
            T1SE.addCarToWaitingList(car);
            gui.addCar(car);
        }
        else if(sourceDirection == Direction.WEST && destinationDirection == Direction.SOUTH){
            if(currentTrafficLight == 1) leavingTime+=60 - globalTime%60;
            else if(currentTrafficLight == 3) leavingTime+=120 - globalTime%60;
            leavingTime += T2WS.getWaitingCarsAtSignal().size() * 6;
            car.setLeavingTime(leavingTime + globalTime);
            T2WS.addCarToWaitingList(car);
            gui.addCar(car);
        }
        else if(sourceDirection == Direction.EAST && destinationDirection == Direction.WEST){
            if(currentTrafficLight == 2) leavingTime+=60 - globalTime%60;
            else if(currentTrafficLight == 1) leavingTime+=120 - globalTime%60;
            leavingTime += T3EW.getWaitingCarsAtSignal().size() * 6;
            car.setLeavingTime(leavingTime + globalTime);
            T3EW.addCarToWaitingList(car);
            gui.addCar(car);
        }
        else if(sourceDirection == Direction.SOUTH && destinationDirection == Direction.WEST){
            car.setLeavingTime(leavingTime + S2W.size()*6 + globalTime);
            S2W.add(car);
            gui.addCar(car);
        }
        else if(sourceDirection == Direction.EAST && destinationDirection == Direction.SOUTH){
            car.setLeavingTime(leavingTime + E2S.size()*6 + globalTime);
            E2S.add(car);
            gui.addCar(car);
        }
        else if(sourceDirection == Direction.WEST && destinationDirection == Direction.EAST){
            car.setLeavingTime(leavingTime + W2E.size()*6 + globalTime);
            W2E.add(car);
            gui.addCar(car);
        }
        else {
            System.out.println(Constants.WRONG_DIRECTION_FORMAT);
        }
        System.out.println("Exit Global Time " + globalTime);
        System.out.println(String.format("Leaving time for car %s is %s", car.getId(), car.getLeavingTime()));

    }

    private void printTrafficLightStatus(){
        System.out.println("-----------------------------------TRAFFIC LIGHT STATUS--------------------------------------");
        System.out.println(String.format("%-20s %-20s %-20s","TRAFFIC LIGHT", "STATUS", "TIME"));
        System.out.println(String.format("%-20s %-20s %-20s", "T1",
                T1SE.getCurrentLight() == 0? "RED" : "GREEN",
                T1SE.getCurrentLight() == 0? "--" : globalTime%Constants.TIME_FOR_EACH_TRAFFIC_LIGHT));
        System.out.println(String.format("%-20s %-20s %-20s", "T2",
                T2WS.getCurrentLight() == 0? "RED" : "GREEN",
                T2WS.getCurrentLight() == 0? "--" : globalTime%Constants.TIME_FOR_EACH_TRAFFIC_LIGHT));
        System.out.println(String.format("%-20s %-20s %-20s", "T3",
                T3EW.getCurrentLight() == 0? "RED" : "GREEN",
                T3EW.getCurrentLight() == 0? "--" : globalTime%Constants.TIME_FOR_EACH_TRAFFIC_LIGHT));
        System.out.println("----------------------------------------------------------------------------------------------");
    }

    private void printCarStatus() {
        System.out.println("-----------------------------------CAR STATUS--------------------------------------");
        System.out.println(String.format("%-20s %-20s %-20s %-20s %-20s","VEHICLE", "SOURCE", "DESTINATION", "STATUS", "REMAINING TIME"));
        List<Car> allCars = S2W;
        allCars.addAll(E2S);
        allCars.addAll(W2E);
        allCars.addAll(T1SE.getWaitingCarsAtSignal());
        allCars.addAll(T1SE.getPassedCarsAtSignal());
        allCars.addAll(T2WS.getWaitingCarsAtSignal());
        allCars.addAll(T2WS.getPassedCarsAtSignal());
        allCars.addAll(T3EW.getWaitingCarsAtSignal());
        allCars.addAll(T3EW.getPassedCarsAtSignal());
        for(Car car : allCars) {
            System.out.println(String.format("%-20s %-20s %-20s %-20s %-20s",
                    car.getId(),
                    car.getSourceDirection(),
                    car.getDestinationDirection(),
                    car.getStatus() == 1? "PASS": "WAIT",
                    car.getLeavingTime() - globalTime >= 0? car.getLeavingTime() - globalTime: "--"
                    ));
        }
        System.out.println("------------------------------------------------------------------------------------");
    }
}
