package TrafficLightOldSac;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame implements EventListener{

    int globalTime = 0;
    int trafficLight = 1;

    List<Car> S2E,W2S,E2W;
    List<Car> S2W, E2S, W2E;
    List<Car> passedToE, passedToS, passedToW;


    int topHorizontalY = 300;
    int bottomHorizontalY = 300 + Constants.WIDTH_OF_LANE;

    int leftVerticalX = 350;
    int rightVerticalX = 350 + Constants.WIDTH_OF_LANE;

    int paddingFromLane = (Constants.WIDTH_OF_LANE - 3*Constants.WIDTH_OF_CAR)/4;
    int paddingFromCar = Constants.WIDTH_OF_CAR/5;

    public GUI() {
        super("Lines Drawing Demo");

        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        S2E = new ArrayList<>();
        W2S = new ArrayList<>();
        E2W = new ArrayList<>();
        S2W = new ArrayList<>();
        E2S = new ArrayList<>();
        W2E = new ArrayList<>();

        passedToE = new ArrayList<>();
        passedToS = new ArrayList<>();
        passedToW = new ArrayList<>();

    }

    void drawLines(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        //Horizontal top
        g2d.drawLine(0, topHorizontalY, 800, topHorizontalY);

        //Horizontal bottom
        g2d.drawLine(0, bottomHorizontalY, leftVerticalX, bottomHorizontalY);
        g2d.drawLine(rightVerticalX, bottomHorizontalY, 800, bottomHorizontalY);

        //Vertical Lines
        g2d.drawLine(leftVerticalX, bottomHorizontalY, leftVerticalX, 800);
        g2d.drawLine(rightVerticalX, bottomHorizontalY, rightVerticalX, 800);

        drawTrafficLights(g2d);
        drawTimer(g2d);
        renderCars(g2d);
//        g2d.drawString("TIME " + );
    }

    void drawTrafficLights(Graphics2D g2d){
        g2d.setColor(Color.RED);
        if(trafficLight == 1) {
            g2d.setColor(Color.GREEN);
        }
        g2d.fillOval(375, 225, 50, 50);
        g2d.setColor(Color.RED);
        if(trafficLight == 2) {
            g2d.setColor(Color.GREEN);
        }
        g2d.fillOval(475, 425, 50, 50);
        g2d.setColor(Color.RED);
        if(trafficLight == 3) {
            g2d.setColor(Color.GREEN);
        }
        g2d.fillOval(275, 425, 50, 50);
    }

    void drawTimer(Graphics2D g2d){
        g2d.setColor(Color.BLACK);
        Font currentFont = g2d.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 2.5F);
        g2d.setFont(newFont);
        g2d.drawString("Time: " + globalTime, 25, 100);
    }

    void renderCars(Graphics2D g2d) {
        for(int i = 0;i<W2E.size();i++){
            if(W2E.get(i).getLeavingTime() - globalTime <= 6)
                g2d.setColor(Color.GREEN);
            //West top lane 1
            g2d.drawRect(leftVerticalX - (i+1)*Constants.LENGTH_OF_CAR - i*paddingFromCar, topHorizontalY + paddingFromLane,
                    Constants.LENGTH_OF_CAR, Constants.WIDTH_OF_CAR );
            g2d.setColor(Color.BLACK);
        }

        for(int i = 0;i<W2S.size();i++){
            if(W2S.get(i).getLeavingTime() - globalTime <= 6)
                g2d.setColor(Color.GREEN);
            //West top lane2
            g2d.drawRect(leftVerticalX - (i+1)*Constants.LENGTH_OF_CAR - i*paddingFromCar, topHorizontalY + 2*paddingFromLane+ Constants.WIDTH_OF_CAR
                    , Constants.LENGTH_OF_CAR, Constants.WIDTH_OF_CAR );
            g2d.setColor(Color.BLACK);
        }
        int n = passedToW.size();
        for(int i = n-1;i>=0;i--){
            g2d.setColor(Color.GREEN);
            //West top lane 3
            g2d.drawRect(leftVerticalX - (n-i)*Constants.LENGTH_OF_CAR - (n-i-1)*paddingFromCar, topHorizontalY + 3*paddingFromLane+ 2*Constants.WIDTH_OF_CAR
                    , Constants.LENGTH_OF_CAR, Constants.WIDTH_OF_CAR );
        }
        for(int i = 0;i<E2S.size();i++){
            if(E2S.get(i).getLeavingTime() - globalTime <= 6)
                g2d.setColor(Color.GREEN);
            //East bottom lane 1
            g2d.drawRect(rightVerticalX + i*Constants.LENGTH_OF_CAR + i*paddingFromCar, bottomHorizontalY - paddingFromLane - Constants.WIDTH_OF_CAR
                    , Constants.LENGTH_OF_CAR, Constants.WIDTH_OF_CAR );
            g2d.setColor(Color.BLACK);
        }
        for(int i = 0;i<E2W.size();i++){
            if(E2W.get(i).getLeavingTime() - globalTime <= 6)
                g2d.setColor(Color.BLUE);
            //East bottom lane 2
            g2d.drawRect(rightVerticalX + i*Constants.LENGTH_OF_CAR + i*paddingFromCar, bottomHorizontalY - 2*paddingFromLane - 2*Constants.WIDTH_OF_CAR
                    , Constants.LENGTH_OF_CAR, Constants.WIDTH_OF_CAR );
            g2d.setColor(Color.BLACK);
        }

        n = passedToE.size();
        for(int i = n-1;i>=0;i--){
            g2d.setColor(Color.BLUE);
            //East bottom lane 3
            g2d.drawRect(rightVerticalX + (n-i-1)*Constants.LENGTH_OF_CAR + (n-i-1)*paddingFromCar, bottomHorizontalY - 3*paddingFromLane - 3*Constants.WIDTH_OF_CAR
                    , Constants.LENGTH_OF_CAR, Constants.WIDTH_OF_CAR );
        }

        for(int i = 0;i<S2W.size();i++){
            if(S2W.get(i).getLeavingTime() - globalTime <= 6)
                g2d.setColor(Color.GREEN);
            //South left lane 1
            g2d.drawRect(leftVerticalX + paddingFromLane, bottomHorizontalY + i*paddingFromCar + i*Constants.WIDTH_OF_CAR
                    , Constants.WIDTH_OF_CAR, Constants.LENGTH_OF_CAR );
            g2d.setColor(Color.BLACK);
        }
        for(int i = 0;i<S2E.size();i++){
            if(S2E.get(i).getLeavingTime() - globalTime <= 6)
                g2d.setColor(Color.GREEN);
            //South left lane 2
            g2d.drawRect(leftVerticalX + 2*paddingFromLane + Constants.WIDTH_OF_CAR, bottomHorizontalY + i*paddingFromCar + i*Constants.WIDTH_OF_CAR
                    , Constants.WIDTH_OF_CAR, Constants.LENGTH_OF_CAR );
            g2d.setColor(Color.BLACK);
        }


        n = passedToS.size();
        for(int i = n-1;i>=0;i--){
            g2d.setColor(Color.BLUE);
            //South left lane 3
            g2d.drawRect(leftVerticalX + 3*paddingFromLane + 2*Constants.WIDTH_OF_CAR, bottomHorizontalY + (n-i-1)*paddingFromCar + (n-i-1)*Constants.WIDTH_OF_CAR
                    , Constants.WIDTH_OF_CAR, Constants.LENGTH_OF_CAR );
        }
    }


    public void paint(Graphics g) {
        super.paint(g);
        drawLines(g);
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("Entered");
                GUI gui = new GUI();
                gui.setVisible(true);
            }
        });
    }

    @Override
    public void addCar(Car car) {
        Direction sourceDirection = car.getSourceDirection();
        Direction destinationDirection = car.getDestinationDirection();
        if(sourceDirection == Direction.SOUTH && destinationDirection == Direction.EAST){
            S2E.add(car);
        }
        else if(sourceDirection == Direction.WEST && destinationDirection == Direction.SOUTH){
            W2S.add(car);
        }
        else if(sourceDirection == Direction.EAST && destinationDirection == Direction.WEST){
            E2W.add(car);
        }
        else if(sourceDirection == Direction.SOUTH && destinationDirection == Direction.WEST){
            S2W.add(car);
        }
        else if(sourceDirection == Direction.EAST && destinationDirection == Direction.SOUTH){
            E2S.add(car);
        }
        else if(sourceDirection == Direction.WEST && destinationDirection == Direction.EAST){
            W2E.add(car);
        }
        else {
            System.out.println(Constants.WRONG_DIRECTION_FORMAT);
        }

        repaint();


    }

    @Override
    public void setTrafficLight(int trafficLight) {
        this.trafficLight = trafficLight;
        this.repaint();
    }

    @Override
    public void passCar(Car car) {
        Direction sourceDirection = car.getSourceDirection();
        Direction destinationDirection = car.getDestinationDirection();
        if(sourceDirection == Direction.SOUTH && destinationDirection == Direction.EAST){
            S2E.remove(car);
            passedToE.add(car);
        }
        else if(sourceDirection == Direction.WEST && destinationDirection == Direction.SOUTH){
            W2S.remove(car);
            passedToS.add(car);
        }
        else if(sourceDirection == Direction.EAST && destinationDirection == Direction.WEST){
            E2W.remove(car);
            passedToW.add(car);
        }
        else if(sourceDirection == Direction.SOUTH && destinationDirection == Direction.WEST){
            S2W.remove(car);
            passedToW.add(car);
        }
        else if(sourceDirection == Direction.EAST && destinationDirection == Direction.SOUTH){
            E2S.remove(car);
            passedToS.add(car);
        }
        else if(sourceDirection == Direction.WEST && destinationDirection == Direction.EAST){
            W2E.remove(car);
            passedToE.add(car);
        }
        else {
            System.out.println(Constants.WRONG_DIRECTION_FORMAT);
        }
        repaint();
    }

    @Override
    public void updateTimer(int globalTime) {
        if(this.globalTime != globalTime){
            this.globalTime = globalTime;
            repaint();
        }
    }
}
