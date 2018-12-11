package bgu.spl.mics.application.Broadcasts;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;

public class TickBroadcast implements Broadcast {

    private int curClockTick;
    public TickBroadcast(int clockTick) {
        curClockTick=clockTick;
    }
    public int getCurClockTick() {
        return curClockTick;
    }

}
