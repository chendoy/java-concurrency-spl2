package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class DeliveryEvent implements Event<DeliveryEvent> {

    private Customer customer;

    public DeliveryEvent(Customer customer) {
        this.customer=customer;
    }

    public Customer getCustomer() {
        return customer;
    }
}
