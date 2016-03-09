import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        //Get input from command.
        String textFile = args[0];
        String directory = args[1];

        //Parse text file.
        String category = null;
        String st = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(textFile));

        while ((st=br.readLine()) != null) {
            //Case: line sets category.
            if(st.charAt(0) == '@') {
                if(st.length() == 1) {
                    category = null;
                } else {
                    category = st.substring(1);
                }
            //Case: normal query, launch a new thread to download.
            } else {
                Worker w = null;
                if (category == null) {
                    w = new Worker(st, directory);
                } else {
                    w = new Worker(st, category, directory);
                }
                Thread t = new Thread(w);
                t.start();
            }
        }
        } catch (FileNotFoundException e) {
            System.err.println("Can't find specified text file.");
        } catch (IOException e) {
            System.err.println("There's something wrong with your text file.");
        }
    }

}
