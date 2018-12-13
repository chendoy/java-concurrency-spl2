package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicleEvent implements Event<DeliveryVehicle> {

    private DeliveryVehicle vehicle;

    public ReleaseVehicleEvent(DeliveryVehicle vehicle){
        this.vehicle=vehicle;
    }


    public DeliveryVehicle getVehicle() {
        return vehicle;
    }
}
