package shorturl;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;
import org.jasypt.util.text.BasicTextEncryptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner; 


public class test1 {
	
	
	
	public static String urlInsert(String url, String mongoUrl, String encryptionPass) {
		
		MongoClientURI mongoURI = new MongoClientURI(mongoUrl);
		
		MongoClient mongoClient = new MongoClient(mongoURI);
		
		MongoDatabase mongoDatabase= mongoClient.getDatabase("email_conf");
		MongoCollection collection = mongoDatabase.getCollection("shorturl");
		BasicTextEncryptor cryption = new BasicTextEncryptor();
		cryption.setPassword(encryptionPass);
		
		String urlencrypt = cryption.encrypt(url);
		String shorturl="";
		Boolean exists=true;
		int count = 0;
		while(exists==true) {
			//Document found = (Document) collection.find(new Document("url", url)).first();
			Document urlfound = (Document) collection.find(eq("url",url)).first();
			if(urlfound==null) {
				shorturl = urlencrypt.substring(count, 6+count);
				
				Document findEncrypt = (Document) collection.find(eq("encryption", shorturl)).first();
				
				if(findEncrypt!=null) {
					
					count++;
					
				} else {
					
					Document newRecord = new Document("url", url)
					.append("encryption", shorturl);
										
					collection.insertOne(newRecord);
					exists=false;
					}
				}

			 else {
				shorturl = (String) urlfound.get("encryption");
				exists=false;
			}
		}
		return shorturl;
	}
	
	public static String findURL(String bit, String mongoUrl, String encryptionPass) {
		MongoClientURI mongoURI = new MongoClientURI(mongoUrl);
		
		MongoClient mongoClient = new MongoClient(mongoURI);
		
		MongoDatabase mongoDatabase= mongoClient.getDatabase("email_conf");
		MongoCollection collection = mongoDatabase.getCollection("shorturl");
		BasicTextEncryptor cryption = new BasicTextEncryptor();
		cryption.setPassword(encryptionPass);
		Document found = (Document) collection.find(new Document("encryption", bit)).first();
		if(found!=null) {
			return (String) found.get("url");
		} else {
			return "None";
		}
	}
	
	
	
	public static void main(String[] args) throws IOException {
		Properties props = new Properties();
		InputStream is = new FileInputStream("config.properties");
		props.load(is);
		
		
		Scanner Selection = new Scanner(System.in);
		Boolean operational = true;
		
		System.out.println("Options: [shorten] or [decrypt]?");
		String option = Selection.nextLine();
		switch(option.toLowerCase()) {
		case "shorten":
			System.out.println("Enter url");
			option = Selection.nextLine();
			System.out.println("Encrypted url path: "+urlInsert(option, props.getProperty("mongodb"), props.getProperty("encryptionPass")));				
			break;
		case "decrypt":
			System.out.println("Enter encrypted path");
			option = Selection.nextLine();
			System.out.println("Result: "+findURL(option, props.getProperty("mongodb"), props.getProperty("encryptionPass")));
			break;
		default:
			System.out.println("Invalid option. Try again.");
		}
			
		
		
	}
	
	
}