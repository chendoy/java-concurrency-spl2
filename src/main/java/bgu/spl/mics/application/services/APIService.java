package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Pair;

import java.util.LinkedList;
import java.util.List;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	private String id;
	private String name;
	private String address;
	private int distance;
	private String creditCardNumber;
	private int creditCardAmount;
	private List<Pair<String,Integer>> orderSchedule;


	public APIService(String id, String name, String address, int distance, String creditCardNumber, int creditCardAmount, List<Pair<String,Integer>> orderSchedule, int i) {
		super("api "+i);
		this.id=id;
		this.name=name;
		this.address=address;
		this.distance=distance;
		this.creditCardNumber=creditCardNumber;
		this.creditCardAmount=creditCardAmount;
		this.orderSchedule=orderSchedule;
	}

	@Override
	protected void initialize() {
		// TODO Implement this
		
	}

}
