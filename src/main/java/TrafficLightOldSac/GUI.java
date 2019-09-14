package TrafficLightOldSac;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame implements EventListener{

    int globalTime = 0;
    int trafficLight = 1;

    //List of the cars in each direction
    List<Car> S2E,W2S,E2W;
    List<Car> S2W, E2S, W2E;

    //List of the cars passed to each direction
    List<Car> passedToE, passedToS, passedToW;

    //The Top line y co-ordinate in the T
    int topHorizontalY = 300;
    int bottomHorizontalY = 300 + Constants.WIDTH_OF_LANE;

    //Vertical Lines x co-ordinate in the T
    int leftVerticalX = 350;
    int rightVerticalX = 350 + Constants.WIDTH_OF_LANE;

    //Padding of car from the lane
    int paddingFromLane = (Constants.WIDTH_OF_LANE - 3*Constants.WIDTH_OF_CAR)/4;

    //Padding of the car from the next car
    int paddingFromCar = Constants.WIDTH_OF_CAR/5;

    //Initialize everything
    public GUI() {
        super("Lines Drawing Demo");

        //Size of the window
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

    //Draw the roads
    void drawLines(Graphics2D g2d) {

        //Horizontal top
        g2d.drawLine(0, topHorizontalY, 800, topHorizontalY);

        //Horizontal bottom
        g2d.drawLine(0, bottomHorizontalY, leftVerticalX, bottomHorizontalY);
        g2d.drawLine(rightVerticalX, bottomHorizontalY, 800, bottomHorizontalY);

        //Vertical Lines
        g2d.drawLine(leftVerticalX, bottomHorizontalY, leftVerticalX, 800);
        g2d.drawLine(rightVerticalX, bottomHorizontalY, rightVerticalX, 800);

    }

    //Draw the legends on top right corner
    void drawLegends(Graphics2D g2d) {
        g2d.drawRect(750 - paddingFromCar - Constants.LENGTH_OF_CAR, 25 + paddingFromLane + Constants.WIDTH_OF_CAR, Constants.LENGTH_OF_CAR, Constants.WIDTH_OF_CAR);
        g2d.setColor(Color.GREEN);
        g2d.drawRect(750 - paddingFromCar - Constants.LENGTH_OF_CAR, 25 + 2*paddingFromLane + 2*Constants.WIDTH_OF_CAR, Constants.LENGTH_OF_CAR, Constants.WIDTH_OF_CAR);
        g2d.setColor(Color.BLUE);
        g2d.drawRect(750 - paddingFromCar - Constants.LENGTH_OF_CAR, 25 + 3*paddingFromLane + 3*Constants.WIDTH_OF_CAR, Constants.LENGTH_OF_CAR, Constants.WIDTH_OF_CAR);


        Font currentFont = g2d.getFont();
        Font newFont = currentFont.deriveFont(15.0F);
        g2d.setFont(newFont);
        FontMetrics fontMetrics = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        String legend = "Waiting";
        int totalWidthOfString = fontMetrics.stringWidth(legend);
        int heightOfLabel = 25 + paddingFromLane + Constants.WIDTH_OF_CAR + fontMetrics.getAscent();
        g2d.drawString(legend,740 - totalWidthOfString - paddingFromCar - Constants.LENGTH_OF_CAR, heightOfLabel );
        legend = "Passing";
        totalWidthOfString = fontMetrics.stringWidth(legend);
        heightOfLabel += paddingFromLane + Constants.WIDTH_OF_CAR;
        g2d.drawString(legend,740 - totalWidthOfString - paddingFromCar - Constants.LENGTH_OF_CAR, heightOfLabel);
        legend = "Passed";
        totalWidthOfString = fontMetrics.stringWidth(legend);
        heightOfLabel += paddingFromLane + Constants.WIDTH_OF_CAR;
        g2d.drawString(legend,740 - totalWidthOfString - paddingFromCar - Constants.LENGTH_OF_CAR, heightOfLabel);

    }

    //Draw the traffic lights with color
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

    //Draw the timer
    void drawTimer(Graphics2D g2d){
        g2d.setColor(Color.BLACK);
        Font currentFont = g2d.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 2.5F);
        g2d.setFont(newFont);
        g2d.drawString("Time: " + globalTime, 25, 100);
    }

    //Draw the cars from the lists
    void renderCars(Graphics2D g2d) {
        for(int i = 0;i<W2E.size();i++){
            drawACar(g2d, W2E.get(i),leftVerticalX - (i+1)*Constants.LENGTH_OF_CAR - i*paddingFromCar, topHorizontalY + paddingFromLane,
                    Constants.LENGTH_OF_CAR, Constants.WIDTH_OF_CAR );
        }

        for(int i = 0;i<W2S.size();i++){
            drawACar(g2d, W2S.get(i), leftVerticalX - (i+1)*Constants.LENGTH_OF_CAR - i*paddingFromCar, topHorizontalY + 2*paddingFromLane+ Constants.WIDTH_OF_CAR
                    , Constants.LENGTH_OF_CAR, Constants.WIDTH_OF_CAR );
        }
        int n = passedToW.size();
        for(int i = n-1;i>=0;i--){
            drawACar(g2d, passedToW.get(i), leftVerticalX - (n-i)*Constants.LENGTH_OF_CAR - (n-i-1)*paddingFromCar, topHorizontalY + 3*paddingFromLane+ 2*Constants.WIDTH_OF_CAR
                    , Constants.LENGTH_OF_CAR, Constants.WIDTH_OF_CAR );
        }
        for(int i = 0;i<E2S.size();i++){
            drawACar(g2d, E2S.get(i), rightVerticalX + i*Constants.LENGTH_OF_CAR + i*paddingFromCar, bottomHorizontalY - paddingFromLane - Constants.WIDTH_OF_CAR
                    , Constants.LENGTH_OF_CAR, Constants.WIDTH_OF_CAR );
        }
        for(int i = 0;i<E2W.size();i++){
            drawACar(g2d, E2W.get(i), rightVerticalX + i*Constants.LENGTH_OF_CAR + i*paddingFromCar, bottomHorizontalY - 2*paddingFromLane - 2*Constants.WIDTH_OF_CAR
                    , Constants.LENGTH_OF_CAR, Constants.WIDTH_OF_CAR );
        }

        n = passedToE.size();
        for(int i = n-1;i>=0;i--){
            drawACar(g2d, passedToE.get(i), rightVerticalX + (n-i-1)*Constants.LENGTH_OF_CAR + (n-i-1)*paddingFromCar, bottomHorizontalY - 3*paddingFromLane - 3*Constants.WIDTH_OF_CAR
                    , Constants.LENGTH_OF_CAR, Constants.WIDTH_OF_CAR  );
        }

        for(int i = 0;i<S2W.size();i++){
            drawACar(g2d, S2W.get(i), leftVerticalX + paddingFromLane, bottomHorizontalY + i*paddingFromCar + i*Constants.LENGTH_OF_CAR
                    , Constants.WIDTH_OF_CAR, Constants.LENGTH_OF_CAR  );
        }
        for(int i = 0;i<S2E.size();i++){
            drawACar(g2d, S2E.get(i), leftVerticalX + 2*paddingFromLane + Constants.WIDTH_OF_CAR, bottomHorizontalY + i*paddingFromCar + i*Constants.LENGTH_OF_CAR
                    , Constants.WIDTH_OF_CAR, Constants.LENGTH_OF_CAR  );
        }

        n = passedToS.size();
        for(int i = n-1;i>=0;i--){
            drawACar(g2d, passedToS.get(i), leftVerticalX + 3*paddingFromLane + 2*Constants.WIDTH_OF_CAR, bottomHorizontalY + (n-i-1)*paddingFromCar + (n-i-1)*Constants.LENGTH_OF_CAR
                    , Constants.WIDTH_OF_CAR, Constants.LENGTH_OF_CAR  );
        }
    }

    //Draw a single car with id
    private void drawACar(Graphics2D g2d, Car car, int x, int y, int length, int width){
        if(car.getLeavingTime() - globalTime <0)
            g2d.setColor(Color.BLUE);
        else if(car.getLeavingTime() - globalTime <= 6)
            g2d.setColor(Color.GREEN);

        g2d.drawRect(x, y, length, width);
        String id = String.valueOf(car.getId());

        g2d.setColor(Color.BLACK);
        Font currentFont = g2d.getFont();
        Font newFont = currentFont.deriveFont(15.0F);
        g2d.setFont(newFont);
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int totalWidthOfString = fontMetrics.stringWidth(id);

        int idX = x + (length - totalWidthOfString) / 2;
        int idY = y + (width - fontMetrics.getHeight())/2 + fontMetrics.getAscent();

        g2d.drawString(id, idX, idY);
    }


    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        drawLines(g2d);
        drawTrafficLights(g2d);
        drawTimer(g2d);
        renderCars(g2d);
        drawLegends(g2d);
    }

    //This function adds a car in the respective list
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

    //This function sets the traffic lights colors
    @Override
    public void setTrafficLight(int trafficLight) {
        this.trafficLight = trafficLight;
        this.repaint();
    }

    //This function passes a car and adds and removes it from the corresponding list
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

    //This function updates the timer in each second
    @Override
    public void updateTimer(int globalTime) {
        if(this.globalTime != globalTime){
            this.globalTime = globalTime;
            repaint(0, 0, 200, 200);
        }
    }
}
