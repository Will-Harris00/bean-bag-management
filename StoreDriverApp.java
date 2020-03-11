// Driver application to test whether the backend code is working as intended.

package beanbags;

public class StoreDriverApp {
    public static void main(String[] args) throws IllegalIDException, BeanBagMismatchException, InvalidMonthException,
            IllegalNumberOfBeanBagsAddedException, InvalidPriceException, BeanBagIDNotRecognisedException,
            PriceNotSetException, BeanBagNotInStockException, InsufficientStockException,
            IllegalNumberOfBeanBagsSoldException, IllegalNumberOfBeanBagsReservedException, ReservationNumberNotRecognisedException {
        Store driver = new Store();
        // Tests the various methods in the backend code.
        driver.addBeanBags(2, "Argos", "Beanie", "0000ffff", (short) 2019, (byte) 11);
        driver.addBeanBags(1, "Argos", "Beanie", "0000AAAA", (short) 2019, (byte) 12, "Test");
        driver.addBeanBags(1, "Argos", "Beanie", "0000afff", (short) 2019, (byte) 11);
        driver.addBeanBags(1, "Argos", "Beanie", "0000afff", (short) 2019, (byte) 11);
        driver.addBeanBags(3, "Argos", "Beanie", "0000bbbb", (short) 2019, (byte) 11);
        driver.setBeanBagPrice("0000afff", 9999);
        driver.setBeanBagPrice("0000ffff", 7500);
        driver.setBeanBagPrice("0000bbbb", 6666);

        driver.array("s");

        // System.out.println(driver.beanBagsInStock());
        // System.out.println(driver.getBeanBagDetails("0000afff"));
        // System.out.println(driver.getBeanBagDetails("0000abff"));
        // System.out.println(driver.getBeanBagDetails("0000afhf"));
        // System.out.println(driver.beanBagsInStock("0000ffff"));
        // driver.sellBeanBags(-1, "0000ffff");
        // driver.sellBeanBags(3, "0000ffff");
        // driver.sellBeanBags(4, "0000feff");
        // driver.sellBeanBags(0, "0000ffff");

        driver.sellBeanBags(1, "0000ffff");
        driver.array("s");
        driver.array("r");
        System.out.println("marker");
        driver.reserveBeanBags(1, "0000bbbb");
        driver.array("r");
        System.out.println("Reservation reference number: " + (driver.reserveBeanBags(1, "0000bbbb")));
        System.out.println("Reservation reference number: " + (driver.reserveBeanBags(1, "0000bbbb")));
        // System.out.println("Reservation reference number: " + (driver.reserveBeanBags(1, "0000bbbb")));
        // driver.reserveBeanBags(3, "0000afff");
        // driver.array("r");
        System.out.println("\n\n");

        // driver.unreserveBeanBags(2);
        driver.array("s");
        System.out.println("\n\n");
        driver.array("r");
        System.out.println("\n\n");
        driver.array("s");
        System.out.println("\n\n");
        // driver.unreserveBeanBags(5);


        System.out.println("\n\n");
        driver.array("s");
        driver.empty();
        System.out.println("The store has been emptied\n\n");
        driver.addBeanBags(1, "Argos", "Beanie", "0000bbbb", (short) 2019, (byte) 11);
        driver.addBeanBags(1, "Argos", "Beanie", "0000CCCC", (short) 2019, (byte) 11);
        driver.setBeanBagPrice("0000bbbb", 5555);
        driver.setBeanBagPrice("0000cccc", 7777);
        driver.reserveBeanBags(1, "0000bbbb");
        driver.reserveBeanBags(1, "0000cccc");
        driver.array("s");

        driver.replace("0000CCCC","0000eeee");
        driver.array("s");

        driver.replace("0000bbbb","0000efef");
        driver.array("r");

        driver.sellBeanBags(0);
        driver.array("r");

        System.out.println(driver.getNumberOfSoldBeanBags("0000ffff"));
        driver.array("ss");

        /*
        driver.addBeanBags(4, "Argos", "Beanie", "0000dddd", (short) 2019, (byte) 11, "This is a bean bag");
        driver.setBeanBagPrice("0000dddd", 8888);
        driver.array("s");
        System.out.println("\n");
        driver.reserveBeanBags(2, "0000dddd");
        driver.array("r");
        System.out.println("\n");
        driver.sellBeanBags(2, "0000dddd");
        driver.array("ss");
        driver.sellBeanBags(0);
        driver.sellBeanBags(1);
        driver.array("ss");

         */


    }
}
