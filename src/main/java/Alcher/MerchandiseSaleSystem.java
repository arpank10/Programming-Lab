package Alcher;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class MerchandiseSaleSystem {
    private int orderCount;
    private AtomicInteger processedOrders = new AtomicInteger(0);
    private List<Order> orders;
    private static final int MAX_BATCH_ORDERS = 3;
    private ConcurrentHashMap<OrderType, Integer> inventory;
    private CountDownLatch startingLatch, waitingLatch;

    public MerchandiseSaleSystem() {

    }

    public void startSystem() {
        //List which contains orders
        orders = new ArrayList<>();

        initInventory();



        //Take Inputs as Orders
        Scanner reader = new Scanner(System.in);
        System.out.println("Number of students ordering: ");
        orderCount = reader.nextInt();

        System.out.println("Input Inventory");
        System.out.println("Number of Small Tshirts");
        inventory.put(OrderType.S, reader.nextInt());
        System.out.println("Number of Medium Tshirts");
        inventory.put(OrderType.M, reader.nextInt());
        System.out.println("Number of Large Tshirts");
        inventory.put(OrderType.L, reader.nextInt());
        System.out.println("Number of Caps");
        inventory.put(OrderType.C, reader.nextInt());

        printInventory();

        System.out.println("Enter orders");

        try {
            takeOrders(reader);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initInventory() {
        inventory = new ConcurrentHashMap<>();
        inventory.put(OrderType.S, 5);
        inventory.put(OrderType.M, 6);
        inventory.put(OrderType.L, 4);
        inventory.put(OrderType.C, 3);
    }

    private void takeOrders(Scanner reader) throws InterruptedException {
        for(int i=0;i<orderCount; i++) {
            int orderNumber = reader.nextInt();
            String orderTypeSymbol = reader.next();
            OrderType orderType = OrderType.valueOf(orderTypeSymbol);
            int orderQuantity = reader.nextInt();
            orders.add(new Order(orderNumber, orderType, orderQuantity));
            if(orders.size() == MAX_BATCH_ORDERS)
                processOrders();
        }
        if( processedOrders.get() < orderCount )
            processOrders();
    }

    private void processOrders() throws InterruptedException {
        int upperLimit = orderCount > orders.size()? orders.size(): orderCount;
        int numberOfThreads = upperLimit - processedOrders.get();
        startingLatch = new CountDownLatch(numberOfThreads);
        waitingLatch = new CountDownLatch(numberOfThreads);
        for(int i = processedOrders.get();i< upperLimit ;i++) {
            Order order = orders.get(i);
            Thread newThread = new Thread(getNewRunnable(order));
            newThread.start();
            startingLatch.countDown();
        }
        waitingLatch.await();

    }

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

    private synchronized void printOrderStatus(Order order, boolean success){
        Thread t = Thread.currentThread();
        System.out.println("Order with order number " + order.getOrderNumber() + (success?" passed!":" failed!") + " in thread "
        + t.getId());
    }

    private Runnable getNewRunnable(Order order){
        Runnable runnable = () -> {
            try {
                startingLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            OrderType orderType = order.getOrderType();
            int orderQuantity = order.getQuantity();
            int quantityPresent = inventory.get(orderType);

            if(quantityPresent >= orderQuantity){
                inventory.put(orderType, quantityPresent - orderQuantity);
                printOrderStatus(order, true);
            }
            else {
                printOrderStatus(order, false);
            }
            processedOrders.incrementAndGet();
            printInventory();
            waitingLatch.countDown();
        };
        return runnable;
    }
}
