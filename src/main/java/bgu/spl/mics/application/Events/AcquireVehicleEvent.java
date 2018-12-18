package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class AcquireVehicleEvent implements Event<Future<DeliveryVehicle>> {

    private DeliveryVehicle vehicle;

    public AcquireVehicleEvent(){
        this.vehicle=null;
    }

}
