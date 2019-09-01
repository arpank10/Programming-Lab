package Alcher;

//POJO for order
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
