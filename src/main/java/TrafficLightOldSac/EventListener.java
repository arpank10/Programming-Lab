package TrafficLightOldSac;

public interface EventListener {
    void addCar(Car car);

    void setTrafficLight(int trafficLight);

    void passCar(Car car);

    void updateTimer(int globalTime);
}
