package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {

    private static BookInventoryInfo[] books;
    private static DeliveryVehicle[] vehicles;

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
                int amount=Integer.parseInt(currentBook.get("amount").getAsString());
                int price=Integer.parseInt(currentBook.get("price").getAsString());
                BookInventoryInfo newBook=new BookInventoryInfo(bookTitle,amount,price);
                books[i]=newBook;
            }

            //getting the whole 'initialResources' array
            JsonArray initialResourcesArray=jsonObject.getAsJsonArray("initialResources");
            JsonElement vehiclesElement=initialResourcesArray.get(0);
            System.out.println(vehiclesElement);
            //JsonArray vehiclesArray=vehiclesElement.getAsJsonArray();


        }
        catch (FileNotFoundException exp)
        {
            System.out.println("File not found: "+exp.getMessage());
        }

    }
}
