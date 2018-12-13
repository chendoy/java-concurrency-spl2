package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class AcquireVehicleEvent implements Event<AcquireVehicleEvent> {

    private DeliveryVehicle vehicle;

    public AcquireVehicleEvent(){
        this.vehicle=null;
    }

    public void setVehicle(DeliveryVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public DeliveryVehicle getVehicle() {
        return vehicle;
    }
}
