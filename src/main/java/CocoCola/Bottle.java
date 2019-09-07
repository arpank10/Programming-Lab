package CocoCola;

public class Bottle {
    private int id;
    private BottleType bottleType;
    private boolean isUnprocessed;
    private boolean isPackaged;
    private boolean isSealed;
    private boolean isInGoDown;

    public Bottle(int id, BottleType bottleType){
        this.id = id;
        this.bottleType = bottleType;
        isUnprocessed = true;
        isPackaged = false;
        isSealed = false;
        isInGoDown = false;
    }

    public boolean isUnprocessed() {
        return isUnprocessed;
    }

    public void setUnprocessed(boolean unprocessed) {
        isUnprocessed = unprocessed;
    }

    public boolean isPackaged() {
        return isPackaged;
    }

    public void setPackaged(boolean packaged) {
        isPackaged = packaged;
    }

    public boolean isSealed() {
        return isSealed;
    }

    public void setSealed(boolean sealed) {
        isSealed = sealed;
    }

    public boolean isInGoDown() {
        return isInGoDown;
    }

    public void setInGoDown(boolean inGoDown) {
        isInGoDown = inGoDown;
    }

    public BottleType getBottleType() {
        return bottleType;
    }

    public void setBottleType(BottleType bottleType) {
        this.bottleType = bottleType;
    }
}
