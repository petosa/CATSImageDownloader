import java.util.HashSet;

public class Main {

    public static void main(String[] args) {
        String[] animals = {"Panda",
                "Cat",
                "Whale",
                "Apple",
                "Chicken",
                "Bed",
                "Tiger",
                "Rabbit",
                "Sofa",
                "Dolphin",
                "Dog",
                "Fork",
                "Bottle",
                "Cow",
                "Lion",
                "Turtle",
                "Carrots",
                "Rat",
                "Zebra",
                "Spoon",
                "Crow",
                "Dove",
                "Tomato",
                "Orange",
                "Giraffe",
                "Phone",
                "Fish",
                "Beef",
                "Computer",
                "Bread",
                "Spectacles",
                "Fridge",
                "Plate",
                "Rhino",
                "Brocolli",
                "Bananas",
                "Drawer",
                "Sheep",
                "Shark",
                "Milk",
                "Lamp",
                "Squirrel",
                "Hamster",
                "Egg",
                "Chocolate",
                "Knife"};
        for (String s : animals) {
            Worker w = new Worker(s, "D:\\Code\\Dev\\CATS");
            Thread t = new Thread(w);
            t.start();
        }
    }

}
