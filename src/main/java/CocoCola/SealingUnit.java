package CocoCola;

import java.util.ArrayList;
import java.util.List;

public class SealingUnit {
    private int priority;
    private int localTime;


    public SealingUnit(){
        priority = 1;
        localTime = 0;
    }

    private void togglePriority(){
        this.priority = priority == 1? 2:1;
    }

    public void sealBottlee(Bottle bottle){
        if(priority == 1){
            togglePriority();
        }
        else if(priority == 2){
            togglePriority();
        }
        localTime+=3;
    }
}
