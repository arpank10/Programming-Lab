package TrafficLightOldSac;

import java.util.ArrayList;
import java.util.List;

//POJO for the traffic light
//Contains the waiting and the passed lists
public class TrafficLight {
    private int currentLight; //Red 0 Green 1
    private List<Car> waitingCarsAtSignal;
    private List<Car> passedCarsAtSignal;

    public TrafficLight() {
        this.currentLight = 0;
        this.waitingCarsAtSignal = new ArrayList<>();
        this.passedCarsAtSignal = new ArrayList<>();
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

    //When a car passes remove it from waiting and add it to passed
    public void handleCarPassing(Car car){
        waitingCarsAtSignal.remove(car);
        car.setStatus(1);
        passedCarsAtSignal.add(car);
    }

    //Get the next car to pass, if no cars, return null
    public Car getNextCarToPass() {
        if(waitingCarsAtSignal.size() == 0)
            return null;
        return waitingCarsAtSignal.get(0);
    }

}
