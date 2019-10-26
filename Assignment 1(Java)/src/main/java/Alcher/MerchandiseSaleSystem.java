package Alcher;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class MerchandiseSaleSystem {

    //Number of orders to be made
    private int orderCount;

    //Number of orders currently processed
    //Atomic Integer is used for this purpose as it is accessed/incremented by more than one thread at a time
    private AtomicInteger processedOrders = new AtomicInteger(0);

    //Orders are stored in this list
    private List<Order> orders;

    //This variable states the maximum number of orders you process at once
    //Set this to orderCount when taking input to process all orders at once
    private static final int MAX_BATCH_ORDERS = 3;

    //ConcurrentHashMap is a synchronized data structure, counterpart of HashMap
    //This is used to store and update the inventory
    private ConcurrentHashMap<OrderType, Integer> inventory;

    //Countdown latches
    //startingLatch is used to start all the threads at once to ensure concurrent programming
    //WaitingLatch ensures that only MAX_BATCH_ORDERS number of threads are running at once, the next inputs are processed
    //once the other threads finish processing
    private CountDownLatch startingLatch, waitingLatch;

    //Empty constructor
    public MerchandiseSaleSystem() {

    }

    //This function initializes the variables, and starts the system
    //
    public void startSystem() throws InputMismatchException{
        //List which contains orders
        orders = new ArrayList<>();

        //Take Inputs as Orders
        Scanner reader = new Scanner(System.in);
        System.out.println("Number of students ordering: ");
        orderCount = reader.nextInt();

        //Initialize inventory with input values
        initInventory(reader);

        //Print the initial inventory
        printInventory();

        System.out.println("Enter orders");

        try {
            takeOrders(reader);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initInventory(Scanner reader) throws InputMismatchException {
        inventory = new ConcurrentHashMap<>();
        //Take input the inventory
        System.out.println("Input Inventory");
        System.out.println("Number of Small Tshirts");
        inventory.put(OrderType.S, reader.nextInt());
        System.out.println("Number of Medium Tshirts");
        inventory.put(OrderType.M, reader.nextInt());
        System.out.println("Number of Large Tshirts");
        inventory.put(OrderType.L, reader.nextInt());
        System.out.println("Number of Caps");
        inventory.put(OrderType.C, reader.nextInt());
    }


    //This function takes input the orders in form "OrderNumber OrderType OrderQuantity"
    //Processes the orders in batch of MAX_BATCH_ORDERS size
    private void takeOrders(Scanner reader) throws InterruptedException, InputMismatchException {
        for(int i=0;i<orderCount; i++) {
            int orderNumber = reader.nextInt();
            String orderTypeSymbol = reader.next();
            OrderType orderType = OrderType.valueOf(orderTypeSymbol);
            int orderQuantity = reader.nextInt();
            orders.add(new Order(orderNumber, orderType, orderQuantity));

            //If batch is full then start processing
            if(orders.size() - processedOrders.get() == MAX_BATCH_ORDERS)
                processOrders();
        }

        //If some orders are left at the end to be processed(in case orderCount%BatchCount != 0)
        if( processedOrders.get() < orderCount )
            processOrders();
    }

    //The multiple orders in batch are processed by passing them into different threads
    private void processOrders() throws InterruptedException {
        //Number of orders to process
        int numberOfThreads = orders.size() - processedOrders.get();

        //Initialize the latches with number of threads
        startingLatch = new CountDownLatch(numberOfThreads);
        waitingLatch = new CountDownLatch(numberOfThreads);

        //Process the orders by starting all of them at the same time using the startingLatch
        for(int i = processedOrders.get();i< orders.size(); i++) {
            Order order = orders.get(i);
            Thread newThread = new Thread(getNewRunnable(order));
            newThread.start();
            startingLatch.countDown();
        }

        //Proceed when all the orders have been processed
        waitingLatch.await();

    }

    //This function prints the inventory in a tabular format
    private synchronized void printInventory() {
        List<OrderType> orderTypes = Arrays.asList(OrderType.values());
        synchronized (System.out) {
            for (OrderType orderType : orderTypes) {
                System.out.print(String.format("%-10s%-10s", "|", orderType));
            }
            System.out.print("|");
            System.out.println();
            for (OrderType orderType : orderTypes) {
                System.out.print(String.format("%-10s%-10s", "|", inventory.get(orderType)));
            }
            System.out.print("|");
            System.out.println();
        }
    }

    //Print order status along with inventory
    //synchronized so that the prints from different orders do not get interleaved
    private synchronized void printOrderStatus(Order order, boolean success){
        Thread t = Thread.currentThread();
        System.out.println("Order with order number " + order.getOrderNumber() + (success?" passed!":" failed!") + " in thread "
        + t.getId());
        printInventory();
    }

    //This function returns the runnable to be run when passed into a thread with the particular order
    private Runnable getNewRunnable(Order order){
        Runnable runnable = () -> {
            try {
                startingLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean result = updateInventory(order);
            processedOrders.incrementAndGet();
            printOrderStatus(order, result);
            waitingLatch.countDown();
        };
        return runnable;
    }


    //This function takes input an order and updates the inventory
    //Returns true if the order is successful else false
    //Should be synchronized because multiple threads are processing orders at same time
    private synchronized boolean updateInventory(Order order){
        OrderType orderType = order.getOrderType();
        int orderQuantity = order.getQuantity();
        int presentQuantity = inventory.get(orderType);
        if (presentQuantity>= orderQuantity) {
            inventory.put(orderType, presentQuantity - orderQuantity);
            return true;
        }
        return false;
    }
}
