package Alcher;

import java.util.InputMismatchException;

public class MerchandiseSaleController {

    public static void main(String[] args) {
        //Start the system
        try {
            new MerchandiseSaleSystem().startSystem();
        } catch (InputMismatchException e){
            System.out.println("Wrong Input Format");
        }
    }
}
