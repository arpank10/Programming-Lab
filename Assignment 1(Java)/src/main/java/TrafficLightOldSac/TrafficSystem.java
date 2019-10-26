package TrafficLightOldSac;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.Phaser;

import static java.lang.Integer.max;

public class TrafficSystem {
    //The traffic lights
    private TrafficLight T1SE, T2WS, T3EW;

    //List of cars waiting in the directions that do not require any traffic lights
    private List<Car> S2W, E2S, W2E;

    //Increment the car id when a new car is inserted
    private int globalCarId = 0;

    //Time which increases one per second
    private int globalTime = 0;

    //1 for T1, 2 for T2, 3 for T3
    private int currentTrafficLight = 1;

    //Stores the last leaving times for the cars in each of the 6 possible paths
    private int lastLeavingTimeSE, lastLeavingTimeWS, lastLeavingTimeEW, lastLeavingTimeSW, lastLeavingTimeES, lastLeavingTimeWE;

    //The number of times the lights are changed
    private int trafficCycle;

    //Instance of the GUI
    GUI gui;

    Phaser phaser;

    //Initialize everything
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

        phaser = new Phaser(1);
    }

    //This function starts the system
    //Starts the trafficSystem thread
    //Starts the GUI thread
    public void startSystem() {
        startTrafficSystem();
        SwingUtilities.invokeLater(() ->
            gui.setVisible(true)
        );
        takeInput();
    }

    //This function takes input in a while loop in the main thread
    private void takeInput(){
        System.out.println("Enter Car or Show Status");

        Scanner reader = new Scanner(System.in);
        while(true){
            String lineInput = reader.nextLine();

            //Show Status of the system at this instant
            if(lineInput.contains(Constants.SHOW_STATUS_MESSAGE)){
                phaser.register();
                printTrafficLightStatus();
                printCarStatus();
                phaser.arriveAndDeregister();
            }
            //Add a vehicle to the System
            else addVehicle(lineInput);
        }
    }

    //This function adds a car to the respective trafficLight
    private void addVehicle(String input) {
        phaser.register();
        List<String> tokens = Arrays.asList(input.split(" "));
        if(tokens.size() < 2) {
            System.out.println(Constants.WRONG_INPUT_FORMAT);
            return;
        }
        Direction sourceDirection = getDirection(tokens.get(0));
        Direction destinationDirection = getDirection(tokens.get(2));
        if(sourceDirection == null || destinationDirection == null) {
            System.out.println(Constants.WRONG_INPUT_FORMAT);
            phaser.arriveAndDeregister();
            return;
        }
        if(sourceDirection == destinationDirection){
            System.out.println(Constants.WRONG_DIRECTION_FORMAT);
            phaser.arriveAndDeregister();
            return;
        }

        //Create a new car with new id and add it to one of the lists
        Car car = new Car(++globalCarId, sourceDirection, destinationDirection, globalTime);
        assignTrafficLightToVehicles(car);
        phaser.arriveAndDeregister();
    }

    //This contains the thread that handles the changing of traffic lights and passing of cars in the traffic system
    private void startTrafficSystem(){
        Runnable runnable = () -> {
            //Start with T1
            T1SE.setCurrentLight(1);

            //Time for a vehicle to pass
            int timeForASingleVehicle = 0;

            //Get the initial car to be passed, will be null as all lists are empty
            Car carToBePassed = getCarToPass(getCurrentTrafficLight());
            while(true){
                try {
                    //sleep for a second
                    Thread.sleep(1000);
                    phaser.arriveAndAwaitAdvance();

                    passCarsThatDontRequireSignal();

                    if(carToBePassed == null){
                        //try to get a car again
                        carToBePassed = getCarToPass(getCurrentTrafficLight());
                        if(carToBePassed != null){
                            timeForASingleVehicle = 1;
                        }
                    }
                    else timeForASingleVehicle++;

                    //Increase global time in gui also
                    globalTime++;
                    gui.updateTimer(globalTime);

                    //Time to change traffic lights
                    if(globalTime % Constants.TIME_FOR_EACH_TRAFFIC_LIGHT == 0)
                    {
                        trafficCycle++;
                        timeForASingleVehicle = 0;
                        currentTrafficLight = currentTrafficLight%3 + 1;
                        setTrafficLightStatus();
                        carToBePassed = getCarToPass(getCurrentTrafficLight());
                        gui.setTrafficLight(currentTrafficLight);
                    }
                    //Time to pass a car through the crossing
                    if(timeForASingleVehicle == Constants.TIME_FOR_VEHICLE_TO_PASS && carToBePassed != null) {
                        getCurrentTrafficLight().handleCarPassing(carToBePassed);
                        gui.passCar(carToBePassed);
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

    //This checks in the three lists at each second and passes each car which is ready to pass at that time
    //Also updates the GUI
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

    //This function gets the next car to pass from the trafficLight
    private Car getCarToPass(TrafficLight trafficLight) {
        return trafficLight.getNextCarToPass();
    }

    //Sets the current trafficLightStatus in the trafficLight POJO
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

    //Get the direction object for the corresponding string
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

    //Assigns cars to a traffic light and sets their leaving time
    //Adds them to the respective lists
    private void assignTrafficLightToVehicles(Car car) {
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
    }

    //This function calculates the leaving Time of the car when the car is entered into the system
    //This can be precalculated according the number of cars that came before it
    private int getLeavingTime(int direction, int lastLeavingTime) {
        int leavingTime = 6;
        //If current traffic light is for it
        if(trafficCycle%3 == direction - 1) {
            int t = max(globalTime, lastLeavingTime);
            int allotedCycle = t/ Constants.TIME_FOR_EACH_TRAFFIC_LIGHT;
            if(leavingTime + t - allotedCycle*Constants.TIME_FOR_EACH_TRAFFIC_LIGHT <= 59){
                leavingTime += t;
            }
            else leavingTime += (allotedCycle + 3) * Constants.TIME_FOR_EACH_TRAFFIC_LIGHT;
        }
        //wait for it to turn green
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

    //This function gets the next cycle in which the car can actually pass
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

    //This function prints the status of each traffic light
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

    //This function prints the status of each car
    private void printCarStatus() {
        System.out.println("-----------------------------------CAR STATUS-----------------------------------------------------------");
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
                    car.getLeavingTime() <= globalTime? "PASS": "WAIT",
                    car.getLeavingTime() > globalTime? car.getLeavingTime() - globalTime: "--"
                    ));
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
    }
}
