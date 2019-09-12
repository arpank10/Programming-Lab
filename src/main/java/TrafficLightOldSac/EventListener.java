package TrafficLightOldSac;

public interface EventListener {
    public void addCar(Car car);

    public void setTrafficLight(int trafficLight);

    public void passCar(Car car);

    public void updateTimer(int globalTime);
}
