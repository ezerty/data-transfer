package rubiconproject;

import rubiconproject.services.impl.DataManipulationServiceImpl;

public class Main {

    public static void main(String[] args) {
//        System.out.println("SA");
        try {


            DataManipulationServiceImpl.getInstance().manipulate(args[0], args[1]);
//            Thread.sleep(5000);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
