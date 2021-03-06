import sun.awt.image.FileImageSource;
import sun.awt.image.ImageFormatException;
import sun.awt.image.JPEGImageDecoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

public class Worker implements Runnable {

    final private String format = "jpg";

    private String url;
    private String query;
    private String encodedQuery;
    private String category;
    private String encodedCategory;
    private String defaultCategory = "uncategorized";
    private boolean isAmb;
    private String source;
    private String imgURL;
    private String directoryOrig;
    private String directory;
    private int imgIndex;

    //Takes query, directory, and explicit imgIndex.
    private Worker(String q, String d, int i) {
        query = q;
        encodedQuery = q.replace(" ", "%20");
        directory = d;
        directoryOrig = d;
        url = "https://www.google.com/search?q=" + encodedQuery +"&tbs=itp:photo,isz:lt,islt:xga&tbm=isch";
        imgIndex = i;
    }

    //No index assumes index of 0.
    public Worker(String q, String d) {
        this(q, d, 0);
    }

    //Category constructor with explicit imgIndex.
    private Worker(String q, String c, String d, int i) {
        this(q, d, i);
        category = c;
        encodedCategory = c.replace(" ", "%20");
        defaultCategory = c;
    }

    //Category constructor with assumed index of 0.
    public Worker(String q, String c, String d) {
        this(q, c, d, 0);
    }

    @Override
    public void run() {

        //Download source
        updateSource();

        //Ambiguity check, only run if we have a category.
        if (category != null) {
            isAmb = source.contains("class=\"_OQi\"");
            if (isAmb) {
                //Add category to query for greater specificity.
                url = "https://www.google.com/search?q=" + encodedQuery + "%20" + encodedCategory + "&tbs=itp:photo,isz:lt,islt:xga&tbm=isch";
                updateSource();
            }
        }

        //Find Nth occurence of "ou":"
        int index = source.indexOf("\"ou\":\"") + 6;
        for (int x = 0; x < imgIndex; x++) {
            source = source.substring(index);
            index = source.indexOf("\"ou\":\"") + 6;
        }

        //Build imgURL
        imgURL = "";
        while(source.charAt(index) != '\"') {
            imgURL += source.charAt(index);
            index++;
        }

        //Change directory
        directory += "\\" + defaultCategory;

        //Make this directory
        File f = new File(directory);
        f.mkdir();

        //Append file name to directory.
        directory += "\\" + query + "." + format;

        //Download image from url to file.
        try {
            saveImage(imgURL, directory);
            System.out.println("Downloaded: " + query + ".");
            if(!checkIntegrity(directory)) {
                throw new IOException();
            }
        } catch (Exception e) {
            //Recursive error resolution.
            System.err.println("Something went wrong when downloading the image for " + query + ". Recursively retrying...");
            Worker w = null;
            //Increment imgIndex with same query..
            if(category == null) {
                w = new Worker(query, directoryOrig, imgIndex + 1);
            } else {
                w = new Worker(query, category, directoryOrig, imgIndex + 1);
            }
            //Launch thread.
            Thread t = new Thread(w);
            t.start();
        }

    }

    //Update source to current url's page source.
    private void updateSource() {
        try {
            source = downloadSource(url);
        } catch (IOException e) {
            System.err.println("Something went wrong when connecting to the URL.");
            e.printStackTrace();
        }
    }

    //Save image located at URL i at file at directory d.
    private void saveImage(String i, String d) throws IOException {

        //Instantiate new connection to image.
        URLConnection connection = new URL(i).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.connect();

        //Open streams.
        InputStream is = new BufferedInputStream(connection.getInputStream());

        BufferedImage buff = ImageIO.read(is);
        //Resize to fit within 1280x720
        //buff = resize(buff);
        File out = new File(d);
        ImageIO.write(buff, "jpg", out);
        //Check if 2M is exceeded
        long len = out.length();
        if(len > 2000000L) {
            System.out.println(d + " is too large (" + out.length() + " bytes)");
            throw new IOException("File too large");
        }
        //Clean up.
        is.close();
    }

    //Check if the downloaded jpg is corrupt.
    private boolean checkIntegrity(String path) {
        try {
            JPEGImageDecoder decoder = new JPEGImageDecoder(new FileImageSource(path), new FileInputStream(path));
            decoder.produceImage();
        } catch (ImageFormatException ife) {
            return false;
        } catch (FileNotFoundException e) {
            System.err.println("Something broke in the integrity checker.");
        } catch (IOException e) {
            System.err.println("Something broke in the integrity checker.");
        }
        return true;
    }

    //Return source for URL specified by u.
    private String downloadSource(String u) throws IOException {

        //Set up URL connection and fake header to avoid 403
        URLConnection connection = new URL(u).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.connect();

        BufferedReader br = null;
        String ret = null;
        try {
            //Save stream to string
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            ret = br.lines().collect(Collectors.joining("\n"));

        } catch (IOException ioe) {
            if (ioe.toString().contains("503")) {
                System.err.println("Google detected our bot and is refusing connections.");
                ioe.printStackTrace();
                System.exit(0);
            }
        } finally {
            br.close();
        }

        return ret;
    }

}
