package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

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

    //^^^^^^^^^^^^^^^^^^^^^^^^CLASS RESOURCES^^^^^^^^^^^^^^^^^^^^^//

    public static void main(String[] args) {


        Gson gson=new Gson();
        File jsonFile= Paths.get(args[0]).toFile();
        try {
            JsonObject jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);

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
                sellingServices[i-1]=new SellingService(i);

            //parsing inventory services
            int numOfInventoryServices=servicesObject.get("inventoryService").getAsInt();
            inventoryServices=new InventoryService[numOfInventoryServices];

            //creating the inventory services
            for (int i=1;i<=numOfInventoryServices;i++)
                inventoryServices[i-1]=new InventoryService(i);

            //parsing logistics services
            int numOfLogisticsServices=servicesObject.get("logistics").getAsInt();
            logisticsServices=new LogisticsService[numOfLogisticsServices];

            //creating the logistics services
            for (int i=1;i<=numOfLogisticsServices;i++)
                logisticsServices[i-1]=new LogisticsService(i);

            //parsing resources services
            int numOfResourcesServices=servicesObject.get("resourcesService").getAsInt();
            resourceServices=new ResourceService[numOfResourcesServices];

            //creating the resources services
            for (int i=1;i<=numOfResourcesServices;i++)
                resourceServices[i-1]=new ResourceService(i);

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

    }
}
