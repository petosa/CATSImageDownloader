import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

public class Worker implements Runnable {

    private String url;
    private String query;
    private String encodedQuery;
    private String category;
    private boolean isAmb;
    private String source;
    private String imgURL;
    private String format;
    private String directory;

    public Worker(String q, String d) {
        query = q;
        encodedQuery = q.replace(" ", "%20");
        directory = d;
        url = "https://www.google.com/search?q=" + encodedQuery +"&tbm=isch";
    }

    public Worker(String q, String c, String d) {
        this(q, d);
        category = c;
    }

    @Override
    public void run() {

        //Download source
        updateSource();

        //Ambiguity check, only run if we have a category.
        if (category != null) {
            isAmb = source.contains("class=\"_OQi\"");
            if (isAmb) {
                //Add category to query for greater specificity
                url = "https://www.google.com/search?q=" + encodedQuery + "%20" + category + "&tbs=itp:photo,isz:lt,islt:xga&tbm=isch";
                updateSource();
            }
        }

        //Get direct link to first Google Image result
        int index = source.indexOf("imgurl=") + 7;
        imgURL = "";
        while(source.charAt(index) != '&') {
            imgURL += source.charAt(index);
            index++;
        }

        //Get file format of picture (png, jpg, ...)
        format = "";
        index--;
        while(source.charAt(index) != '.') {
            format = source.charAt(index) + format;
            index--;
        }

        //Set to uncategorized if no category
        if(category == null) {
            category = "uncategorized";
        }

        //Change directory
        directory += "\\" + category;

        //Make this directory
        File f = new File(directory);
        f.mkdir();

        //Append file name to directory
        directory += "\\" + query + "." + format;

        //Download image from url to file
        try {
            saveImage(imgURL, directory);
        } catch (IOException e) {
            System.err.println("Something went wrong when downloading the image for " + query + ".");
            e.printStackTrace();
        }

    }

    private void updateSource() {
        try {
            source = downloadSource(url);
        } catch (IOException e) {
            System.err.println("Something went wrong when connecting to the URL.");
            e.printStackTrace();
        }
    }

    private void saveImage(String i, String d) throws IOException {

        URLConnection connection = new URL(i).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.connect();
        InputStream is = connection.getInputStream();
        OutputStream os = new FileOutputStream(d);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }

    private String downloadSource(String u) throws IOException {

        //Set up URL connection and fake header to avoid 403
        URLConnection connection = new URL(u).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.connect();

        //Save stream to string
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String ret = br.lines().collect(Collectors.joining("\n"));
        br.close();

        return ret;
    }

}
