package TrafficLightOldSac;


//POJO for a car
public class Car {
    private int id;
    private Direction sourceDirection;
    private Direction destinationDirection;
    private int leavingTime;
    private int arrivedTime;
    private int status; //Pass 1 Wait 0

    public Car(int id, Direction sourceDirection, Direction destinationDirection, int arrivedTime) {
        this.id = id;
        this.sourceDirection = sourceDirection;
        this.destinationDirection = destinationDirection;
        this.arrivedTime = arrivedTime;
        this.leavingTime = 0;
        this.status = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Direction getSourceDirection() {
        return sourceDirection;
    }

    public void setSourceDirection(Direction sourceDirection) {
        this.sourceDirection = sourceDirection;
    }

    public Direction getDestinationDirection() {
        return destinationDirection;
    }

    public void setDestinationDirection(Direction destinationDirection) {
        this.destinationDirection = destinationDirection;
    }

    public int getLeavingTime() {
        return leavingTime;
    }

    public void setLeavingTime(int leavingTime) {
        this.leavingTime = leavingTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(int arrivedTime) {
        this.arrivedTime = arrivedTime;
    }
}
