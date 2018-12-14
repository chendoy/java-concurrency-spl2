package bgu.spl.mics.application;

import bgu.spl.mics.Coordinator;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {

    //------------------------CLASS RESOURCES------------------------//

    private static BookInventoryInfo[] books;
    private static DeliveryVehicle[] vehicles;
    private static TimeService timeService;
    private static SellingService[]sellingServices;
    private static InventoryService[] inventoryServices;
    private static LogisticsService[] logisticsServices;
    private static ResourceService[] resourceServices;
    private static Customer[] customers;
    private static MoneyRegister moneyRegister;
    private static ResourcesHolder resourcesHolder;
    private static Inventory inventory;
    private static APIService[]webApis;
    private static CountDownLatch latchObject;
    private static Coordinator coordinator;
    private static HashMap<Integer,Customer> customerHashMap;
    private static int numOfServices;

    //^^^^^^^^^^^^^^^^^^^^^^^^CLASS RESOURCES^^^^^^^^^^^^^^^^^^^^^//





    //------------------------JSON PARSING------------------------//


    public static void main(String[] args) {

        //initializing singleton objects
        moneyRegister=MoneyRegister.getInstance();
        resourcesHolder=ResourcesHolder.getInstance();
        inventory=Inventory.getInstance();

        Gson gson=new Gson();
        File jsonFile= Paths.get(args[0]).toFile();
        try {
            JsonObject jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);

            //first will count the numbers of services to init CountDownLatch\
            calculateNumOfServices(jsonObject);
            latchObject=new CountDownLatch(numOfServices);


            //getting the whole 'initialInventory' array
            JsonArray bookInventoryInfoArray=jsonObject.getAsJsonArray("initialInventory");
            books=new BookInventoryInfo[bookInventoryInfoArray.size()];


            //prasing the array into bookInventoryInfo
            for(int i=0;i<bookInventoryInfoArray.size();i++)
            {
                JsonObject currentBook=bookInventoryInfoArray.get(i).getAsJsonObject();
                String bookTitle=currentBook.get("bookTitle").getAsString();
                int amount=currentBook.get("amount").getAsInt();
                int price=currentBook.get("price").getAsInt();
                BookInventoryInfo newBook=new BookInventoryInfo(bookTitle,amount,price);
                books[i]=newBook;
            }

            //getting the whole 'initialResources' array
            JsonArray initialResourcesArray=jsonObject.getAsJsonArray("initialResources");
            JsonElement vehiclesElement=initialResourcesArray.get(0);
            JsonArray vehiclesArray=vehiclesElement.getAsJsonObject().getAsJsonArray("vehicles");
            vehicles=new DeliveryVehicle[vehiclesArray.size()];

            //parsing the array into vehicles
            for(int i=0;i<vehiclesArray.size();i++)
            {
                JsonObject currentVehicle=vehiclesArray.get(i).getAsJsonObject();
                int license=currentVehicle.get("license").getAsInt();
                int speed=currentVehicle.get("speed").getAsInt();
                DeliveryVehicle newVehicle=new DeliveryVehicle(license,speed);
                vehicles[i]=newVehicle;
            }

            //getting whole services object
            JsonObject servicesObject=jsonObject.getAsJsonObject("services");

            //parsing time service
            JsonObject timeObject=servicesObject.getAsJsonObject("time");
            int speed=timeObject.get("speed").getAsInt();
            int duration=timeObject.get("duration").getAsInt();
            timeService=new TimeService(speed,duration);

            //parsing selling services
            int numOfSellers=servicesObject.get("selling").getAsInt();
            sellingServices=new SellingService[numOfSellers];

            //creating the selling services
            for (int i=1;i<=numOfSellers;i++)
                sellingServices[i-1]=new SellingService(i,moneyRegister,latchObject);

            //parsing inventory services
            int numOfInventoryServices=servicesObject.get("inventoryService").getAsInt();
            inventoryServices=new InventoryService[numOfInventoryServices];

            //creating the inventory services
            for (int i=1;i<=numOfInventoryServices;i++)
                inventoryServices[i-1]=new InventoryService(i,latchObject);

            //parsing logistics services
            int numOfLogisticsServices=servicesObject.get("logistics").getAsInt();
            logisticsServices=new LogisticsService[numOfLogisticsServices];

            //creating the logistics services
            for (int i=1;i<=numOfLogisticsServices;i++)
                logisticsServices[i-1]=new LogisticsService(i,latchObject);

            //parsing resources services
            int numOfResourcesServices=servicesObject.get("resourcesService").getAsInt();
            resourceServices=new ResourceService[numOfResourcesServices];

            //creating the resources services
            for (int i=1;i<=numOfResourcesServices;i++)
                resourceServices[i-1]=new ResourceService(i,resourcesHolder,latchObject);

            JsonArray customersArray=servicesObject.getAsJsonArray("customers");
            customers =new Customer[customersArray.size()];


            //parsing each customer
            for (int i = 0; i< customers.length; i++)
            {
                JsonObject currentElement=customersArray.get(i).getAsJsonObject();
                int id=currentElement.get("id").getAsInt();
                String name=currentElement.get("name").getAsString();
                String address=currentElement.get("address").getAsString();
                int distance=currentElement.get("distance").getAsInt();
                int creditCardNumber=currentElement.get("creditCard").getAsJsonObject().get("number").getAsInt();
                int creditCardAmount=currentElement.get("creditCard").getAsJsonObject().get("amount").getAsInt();

                //parsing customer's order schedule
                List<Pair<String,Integer>> orderScheduleList=new LinkedList<>();
                JsonArray orderSchedule=currentElement.getAsJsonArray("orderSchedule");
                for(int j=0;j<orderSchedule.size();j++)
                {
                    String bookTitle=orderSchedule.get(j).getAsJsonObject().get("bookTitle").getAsString();
                    int tick=orderSchedule.get(j).getAsJsonObject().get("tick").getAsInt();
                    Pair<String,Integer> orderScheduleElement=new Pair(bookTitle,tick);
                    ((LinkedList<Pair<String, Integer>>) orderScheduleList).addFirst(orderScheduleElement);
                }

                Customer newCustomer=new Customer(id,name,address,distance,creditCardNumber,creditCardAmount,orderScheduleList);
                customers[i]=newCustomer;
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found: "+e.getMessage());
        }

        //------------------------SERVICES LAUNCHING------------------------//


        inventory.load(books);

        //creating API service per customer
        webApis=new APIService[customers.length];
        for(int i=0;i<webApis.length;i++)
            webApis[i]=new APIService(customers[i],i+1,latchObject);


        coordinator=new Coordinator(latchObject);


        new Thread(coordinator).start();
        for(int i=0;i<sellingServices.length;i=i+1)
            new Thread(sellingServices[i]).start();

        for(int i=0;i<webApis.length;i=i+1)
            new Thread(webApis[i]).start();

        for(int i=0;i<inventoryServices.length;i=i+1)
            new Thread(inventoryServices[i]).start();

        for(int i=0;i<logisticsServices.length;i=i+1)
            new Thread(logisticsServices[i]).start();

        for(int i=0;i<resourceServices.length;i=i+1)
            new Thread(resourceServices[i]).start();


        while (latchObject.getCount()!=0) {
            System.out.println(latchObject.getCount());

        }



        //now all the threads that need to get Ticks initialized, so we can initialize TimeService (and the rest of the threads)
        new Thread(timeService).start();





        //------------------------GENERATING OUTPUT FILES------------------------//

        createCustomersHashMap();
        printCustomersToFile(args[1]);
        inventory.printInventoryToFile(args[2]);
        moneyRegister.printOrderReceipts(args[3]);
        printMoneyRegisterObject(args[4]);
    }


    //---------------------HELPER STATIC METHODS----------------//

    private static void printCustomersToFile(String filename) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(customerHashMap);
            out.close();
            fileOut.close();
        } catch (Exception e) {
            System.out.println("Error in customers printing: " + e.toString());
        }
    }

    private static void createCustomersHashMap() {
        customerHashMap=new HashMap<>();
        for (int i=0;i<customers.length;i++)
            customerHashMap.put(customers[i].getId(),customers[i]);
    }

    private static void printMoneyRegisterObject(String filename) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(moneyRegister);
            out.close();
            fileOut.close();
        } catch (Exception e) {
            System.out.println("Error in moneyRegister printing: " + e.toString());
        }
    }

    private static void calculateNumOfServices(JsonObject jsonObject) {

        //first "pass" over json file to figure out the overall number of services
        JsonObject servicesObject_pre=jsonObject.getAsJsonObject("services");
        int numOfSellers_pre=servicesObject_pre.get("selling").getAsInt();
        numOfServices+=+numOfSellers_pre;
        int numOfInventoryServices_pre=servicesObject_pre.get("inventoryService").getAsInt();
        numOfServices+=numOfInventoryServices_pre;
        int numOfLogisticsServices_pre=servicesObject_pre.get("logistics").getAsInt();
        numOfServices+=numOfLogisticsServices_pre;
        int numOfResourcesServices_pre=servicesObject_pre.get("resourcesService").getAsInt();
        numOfServices+=numOfResourcesServices_pre;
        JsonArray customersArray_pre=servicesObject_pre.getAsJsonArray("customers");
        int numOfCustomer_pre=customersArray_pre.size();
        numOfServices+=numOfCustomer_pre;

    }
}