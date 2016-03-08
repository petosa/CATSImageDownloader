import java.util.HashSet;

public class Main {

    public static void main(String[] args) {
        String[] animals = {"cat", "dog", "deer", "pig", "cow", "chicken", "rooster", "frog", "fish", "bird"};
        for (String s : animals) {
            Worker w = new Worker(s, "animal", "D:\\Code\\Dev\\CATS");
            Thread t = new Thread(w);
            t.start();
        }
    }

}
