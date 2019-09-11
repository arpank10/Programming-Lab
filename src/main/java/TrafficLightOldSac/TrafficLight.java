package TrafficLightOldSac;

import java.util.ArrayList;
import java.util.List;

public class TrafficLight {
    private Direction sourceDirectionItHandles;
    private Direction destinationDirectionItHandles;
    private int currentLight; //Red 0 Green 1
    private List<Car> waitingCarsAtSignal;
    private List<Car> passedCarsAtSignal;

    public TrafficLight(Direction sourceDirectionItHandles, Direction destinationDirectionItHandles) {
        this.sourceDirectionItHandles = sourceDirectionItHandles;
        this.destinationDirectionItHandles = destinationDirectionItHandles;
        this.currentLight = 0;
        this.waitingCarsAtSignal = new ArrayList<>();
        this.passedCarsAtSignal = new ArrayList<>();
    }

    public Direction getSourceDirectionItHandles() {
        return sourceDirectionItHandles;
    }

    public void setSourceDirectionItHandles(Direction sourceDirectionItHandles) {
        this.sourceDirectionItHandles = sourceDirectionItHandles;
    }

    public Direction getDestinationDirectionItHandles() {
        return destinationDirectionItHandles;
    }

    public void setDestinationDirectionItHandles(Direction destinationDirectionItHandles) {
        this.destinationDirectionItHandles = destinationDirectionItHandles;
    }

    public int getCurrentLight() {
        return currentLight;
    }

    public void setCurrentLight(int currentLight) {
        this.currentLight = currentLight;
    }

    public List<Car> getWaitingCarsAtSignal() {
        return waitingCarsAtSignal;
    }

    public void setWaitingCarsAtSignal(List<Car> waitingCarsAtSignal) {
        this.waitingCarsAtSignal = waitingCarsAtSignal;
    }

    public List<Car> getPassedCarsAtSignal() {
        return passedCarsAtSignal;
    }

    public void setPassedCarsAtSignal(List<Car> passedCarsAtSignal) {
        this.passedCarsAtSignal = passedCarsAtSignal;
    }

    public void addCarToWaitingList(Car car){
        waitingCarsAtSignal.add(car);
    }

    public void handleCarPassing(Car car){
        waitingCarsAtSignal.remove(car);
        car.setStatus(1);
        car.setLeavingTime(0);
        passedCarsAtSignal.add(car);
    }

    public Car getNextCarToPass() {
        if(waitingCarsAtSignal.size() == 0)
            return null;
        return waitingCarsAtSignal.get(0);
    }

}
