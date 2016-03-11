import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        //Get input from command.
        String textFile = args[0];
        String directory = args[1];

        System.out.println("Arguments are valid. Continuing...");

        try {
            File f = new File(directory);
            if (!f.exists()) {
                throw new FileNotFoundException();
            }
            //Parse text file.
            String category = null;
            String st = null;
            BufferedReader br = null;

            br = new BufferedReader(new FileReader(textFile));
            System.out.println("Text file parsed. Beginning downloads...");

            //Create thread pool of size 1
            ExecutorService executor = Executors.newFixedThreadPool(5);

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
                    executor.execute(w);
                }
            }
            executor.shutdown();

        } catch (FileNotFoundException e) {
             System.err.println("Can't find specified text file or directory.");
        } catch (IOException e) {
             System.err.println("There's something wrong with your text file.");
        }
        System.out.println("Complete!");
    }

}
