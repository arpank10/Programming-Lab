package Alcher;

//POJO for order
//Contains the attributes (private members) of the Order Class and the corresponding getter and setter functions
public class Order {

    private int orderNumber;

    private OrderType orderType;

    private int quantity;

    public Order(int orderNumber, OrderType orderType, int quantity) {
        this.orderNumber = orderNumber;
        this.orderType = orderType;
        this.quantity = quantity;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public int getQuantity() {
        return quantity;
    }
}
