package CocoCola;

import java.util.InputMismatchException;

public class BottleManufacturingController {

    public static void main(String[] args){
        //Starting the manufacturing system
        try {
            new BottleManufacturingSystem().startSystem();
        } catch (InputMismatchException e){
            System.out.println("Wrong input format");
        }
    }
}
