package bgu.spl.mics;

/**
 * A "Marker" interface extending {@link Message}. When sending a Broadcasts message
 * using the {@link MessageBus}, it will be received by all the subscribers of this
 * Broadcasts-message type (the message Class).
 */
public interface Broadcast extends Message {

}
