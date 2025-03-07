import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class ReadFile {
  public static void main(String[] args) {
    try {
      File myObj = new File("exp_assembly_local_energy_template_1677512126790.txt");
      Scanner myReader = new Scanner(myObj);
      Scanner myReader2 = new Scanner(myObj);

      double tot = 0;
      int count = 0;
      int number_of_rows = 0;
      
      while (myReader.hasNextLine()) {
          String data = myReader.nextLine();
        number_of_rows++;
      }
      
      System.out.println("number_of_rows : " + number_of_rows);
      
      int to_exclude = number_of_rows/100;
      System.out.println("to_exclude : " + to_exclude);

      while (myReader2.hasNextLine()) {
          String[] data = myReader2.nextLine().split(" ");
          String value = data[1];
          //System.out.println(value);
          
          if(count>=to_exclude) {
            tot += Double.valueOf(value);
          }
          count++;
        }
      
      System.out.println("\n\n");
      System.out.println("tot : " + tot);
      System.out.println("count : " + (count-to_exclude));

      double mean = (double) tot/(count-to_exclude);
      System.out.println("mean : " + mean);

      
      myReader.close();
      myReader2.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }
}