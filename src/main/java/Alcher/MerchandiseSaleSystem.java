package Alcher;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MerchandiseSaleSystem {
    private int orderCount;
    private AtomicInteger processedOrders = new AtomicInteger(0);
    private List<Order> orders;
    private static final int MAX_BATCH_ORDERS = 3;
    private ConcurrentHashMap<OrderType, Integer> inventory;

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
            printInventory();
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
        List<Thread> threads = new ArrayList<>();
        int upperLimit = orderCount > orders.size()? orders.size(): orderCount;
        for(int i = processedOrders.get();i< upperLimit ;i++) {
            Order order = orders.get(i);
            Thread newThread = new Thread(getNewRunnable(order));
            newThread.start();
            threads.add(newThread);
        }
        for(Thread thread: threads)
            thread.join();
    }

    private synchronized void printInventory() {
        List<OrderType> orderTypes = Arrays.asList(OrderType.values());
        synchronized (System.out) {
            for (OrderType orderType : orderTypes) {
                System.out.print("  |  ");
                System.out.print(orderType);
            }
            System.out.print("  |");
            System.out.println();
            for (OrderType orderType : orderTypes) {
                System.out.print("  |  ");
                System.out.print(inventory.get(orderType));
            }
            System.out.print("  |");
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
        };
        return runnable;
    }
}
