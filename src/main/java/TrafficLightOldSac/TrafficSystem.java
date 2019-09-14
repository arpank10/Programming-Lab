package TrafficLightOldSac;

import javax.swing.*;
import java.util.*;

import static java.lang.Integer.max;

public class TrafficSystem {
    private TrafficLight T1SE, T2WS, T3EW;
    private List<Car> S2W, E2S, W2E;
    private int globalCarId = 0;
    private int globalTime = 0;
    private int currentTrafficLight = 1;
    private int lastLeavingTimeSE, lastLeavingTimeWS, lastLeavingTimeEW, lastLeavingTimeSW, lastLeavingTimeES, lastLeavingTimeWE;
    private int trafficCycle;
    GUI gui;

    public TrafficSystem(){
        T1SE = new TrafficLight();
        T2WS = new TrafficLight();
        T3EW = new TrafficLight();

        S2W = new ArrayList<>();
        E2S = new ArrayList<>();
        W2E = new ArrayList<>();

        lastLeavingTimeSE = 0;
        lastLeavingTimeWS = 60;
        lastLeavingTimeEW = 120;

        lastLeavingTimeSW = 0;
        lastLeavingTimeES = 0;
        lastLeavingTimeWE = 0;

        trafficCycle = 0;
        gui = new GUI();
    }

    public void startSystem() {
        startTrafficSystem();
        SwingUtilities.invokeLater(() -> {
            gui.setVisible(true);
        });
        takeInput();
    }

    private void takeInput(){
        System.out.println("Enter Car or Show Status");

        Scanner reader = new Scanner(System.in);
        while(true){
            String lineInput = reader.nextLine();

            if(lineInput.contains(Constants.SHOW_STATUS_MESSAGE)){
                printTrafficLightStatus();
                printCarStatus();
            }
            else addVehicle(lineInput);
        }
    }

    private void addVehicle(String input) {
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
    }

    private void startTrafficSystem(){
        Runnable runnable = () -> {
            T1SE.setCurrentLight(1);
            int timeForASingleVehicle = 0;
            Car carToBePassed = getCarToPass(getCurrentTrafficLight());
            while(true){
                try {
                    Thread.sleep(1000);
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

                    if(globalTime % Constants.TIME_FOR_EACH_TRAFFIC_LIGHT == 0)
                    {
                        trafficCycle++;
                        System.out.println(globalTime);
                        timeForASingleVehicle = 0;
                        currentTrafficLight = currentTrafficLight%3 + 1;
                        setTrafficLightStatus();
                        carToBePassed = getCarToPass(getCurrentTrafficLight());
                        gui.setTrafficLight(currentTrafficLight);
                    }
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
                } catch (InterruptedException e) {
                        e.printStackTrace();
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
        int leavingTime = 0;
        if(sourceDirection == Direction.SOUTH && destinationDirection == Direction.EAST){
            leavingTime = getLeavingTime(1, lastLeavingTimeSE);
            lastLeavingTimeSE = leavingTime;
            car.setLeavingTime(leavingTime);
            T1SE.addCarToWaitingList(car);
            gui.addCar(car);
        }
        else if(sourceDirection == Direction.WEST && destinationDirection == Direction.SOUTH){
            leavingTime = getLeavingTime(2, lastLeavingTimeWS);
            lastLeavingTimeWS = leavingTime;
            car.setLeavingTime(leavingTime);
            T2WS.addCarToWaitingList(car);
            gui.addCar(car);
        }
        else if(sourceDirection == Direction.EAST && destinationDirection == Direction.WEST){
            leavingTime = getLeavingTime(3, lastLeavingTimeEW);
            lastLeavingTimeEW = leavingTime;
            car.setLeavingTime(leavingTime);
            T3EW.addCarToWaitingList(car);
            gui.addCar(car);
        }
        else if(sourceDirection == Direction.SOUTH && destinationDirection == Direction.WEST){
            leavingTime = max(globalTime, lastLeavingTimeSW) + Constants.TIME_FOR_VEHICLE_TO_PASS;
            lastLeavingTimeSW = leavingTime;
            car.setLeavingTime(leavingTime);
            S2W.add(car);
            gui.addCar(car);
        }
        else if(sourceDirection == Direction.EAST && destinationDirection == Direction.SOUTH){
            leavingTime = max(globalTime, lastLeavingTimeES) + Constants.TIME_FOR_VEHICLE_TO_PASS;
            lastLeavingTimeES = leavingTime;
            car.setLeavingTime(leavingTime);
            E2S.add(car);
            gui.addCar(car);
        }
        else if(sourceDirection == Direction.WEST && destinationDirection == Direction.EAST){
            leavingTime = max(globalTime, lastLeavingTimeWE) + Constants.TIME_FOR_VEHICLE_TO_PASS;
            lastLeavingTimeWE = leavingTime;
            car.setLeavingTime(leavingTime);
            W2E.add(car);
            gui.addCar(car);
        }
        else {
            System.out.println(Constants.WRONG_DIRECTION_FORMAT);
        }
        System.out.println("Exit Global Time " + globalTime);
        System.out.println(String.format("Leaving time for car %s is %s", car.getId(), car.getLeavingTime()));

    }

    private int getLeavingTime(int direction, int lastLeavingTime) {
        int leavingTime = 6;
        if(trafficCycle%3 == direction - 1) {
            int t = max(globalTime, lastLeavingTime);
            int allotedCycle = t/ Constants.TIME_FOR_EACH_TRAFFIC_LIGHT;
            if(leavingTime + t - allotedCycle*Constants.TIME_FOR_EACH_TRAFFIC_LIGHT <= 59){
                leavingTime += t;
            }
            else leavingTime += (allotedCycle + 3) * Constants.TIME_FOR_EACH_TRAFFIC_LIGHT;
        }
        else {
            int t = max(getNextCycleForCar(direction) * 60, lastLeavingTime);
            int allotedCycle = t/Constants.TIME_FOR_EACH_TRAFFIC_LIGHT;
            if(leavingTime + t - allotedCycle*Constants.TIME_FOR_EACH_TRAFFIC_LIGHT <= 59){
                leavingTime += t;
            }
            else leavingTime += (allotedCycle + 3) * Constants.TIME_FOR_EACH_TRAFFIC_LIGHT;
        }
        return leavingTime;
    }

    private int getNextCycleForCar(int carDirection) {
        switch (carDirection){
            case 1:
                return globalTime/180 * 3 + 3;
            case 2:
                if(currentTrafficLight == 1)
                    return trafficCycle+1;
                else return globalTime/180 * 3 + 4;
            case 3:
                if(currentTrafficLight == 1 || currentTrafficLight == 2)
                    return trafficCycle + currentTrafficLight;
        }
        return trafficCycle;
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
