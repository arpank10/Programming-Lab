package TrafficLightOldSac;

//Eventlistener which is implemented by the GUI and called by the TrafficSystem
public interface EventListener {
    void addCar(Car car);

    void setTrafficLight(int trafficLight);

    void passCar(Car car);

    void updateTimer(int globalTime);
}
