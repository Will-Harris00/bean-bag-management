package beanbags;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * BeanBagStore implementation class. Represents the back end of a stock
 * management system help a user manage reservations, sales and restocking of
 * items more efficiently.
 *
 * @author 680033128
 * @author 690065435
 * @version 1.1
 *
 *
 */

public class Store implements BeanBagStore {
    // Initialises the three lists for storing available, reserved, and sold bean
    // bags.
    private ObjectArrayList available = new ObjectArrayList();
    private ObjectArrayList reserved = new ObjectArrayList();
    private ObjectArrayList sold = new ObjectArrayList();

    /**
     * If no information parameter mentioned, adds a bean bag with no additional
     * information (empty string).
     */
    public void addBeanBags(int num, String manufacturer, String name, String id, short year, byte month)
            throws IllegalNumberOfBeanBagsAddedException, BeanBagMismatchException, IllegalIDException,
            InvalidMonthException {
        addBeanBags(num, manufacturer, name, id, year, month, "");
    }

    // Adds a bean bag to stock list.
    public void addBeanBags(int num, String manufacturer, String name, String id, short year, byte month,
            String information) throws IllegalNumberOfBeanBagsAddedException, BeanBagMismatchException,
            IllegalIDException, InvalidMonthException {

        // Throws an exception if the user tries to add a negative number of bean bags.
        if (num <= 0) {
            throw new IllegalNumberOfBeanBagsAddedException(
                    "The number of bean bags '" + num + "' added must be positive.");
        }

        // Throws an exception if the user tries to add a month of manufacture which
        // doesn't exist.
        if (month < 0 | month > 12) {
            throw new InvalidMonthException("The month of manufacturer '" + month + "' must be between 1 and 12.");
        }

        // Checks if IDs and attributes both match for added bean bags.
        Checks.validId(id);

        // Checks the available stock for any matching bean bags and gets the price of
        // item to use for the new one.
        int existingPrice = getExistingPrice(id);
        for (int i = 0; i < num; i++) {
            BeanBag newBeanBag = new BeanBag(manufacturer, name, id, year, month, information, existingPrice);
            Checks.existingMismatch(newBeanBag, available, reserved, sold);
            available.add(newBeanBag);
        }

        // Throws an AssertionError if the information value is null.
        assert (information != null) : "The bean bag incorrectly has information set to null.";
    }

    // Test method for printing the bean bags in a given list.
    public void array(String type) {
        switch (type.toLowerCase()) {
            case "available":
            case "a":
                arrayPrint(available, "available");
                break;
            case "reserved":
            case "r":
                arrayPrint(reserved, "reserved");
                break;
            case "sold":
            case "s":
                arrayPrint(sold, "sold");
                break;
        }
    }

    // Iterates through the given list (stock/reserved/available/sold) and prints
    // the bean bags in that list.
    public void arrayPrint(ObjectArrayList obj, String type) {
        BeanBag item;
        Reservation held;

        // If the object size is empty, let the user know.
        if (obj.size() == 0) {
            System.out.println("The '" + type + "' object is empty.");
        } else {
            System.out.println("The '" + type + "' object contains '" + obj.size() + "' items:");
            // Prints a list of the beans bags which are available or sold.
            if (type.equals("available") || type.equals("sold")) {
                for (int j = 0; j < obj.size(); j++) {
                    item = (BeanBag) obj.get(j);
                    System.out.println("[id=" + item.getIdentifier() + ",name=" + item.getName() + ",manufacturer="
                            + item.getManufacturer() + ",year=" + item.getYear() + ",month=" + item.getMonth()
                            + ",information=" + item.getInformation() + ",priceInPence=" + item.getPriceInPence()
                            + "]");
                }
                // Prints a list of the bean bags which are reserved.
            } else if (type.equals("reserved")) {
                for (int j = 0; j < obj.size(); j++) {
                    held = (Reservation) obj.get(j);
                    item = held.getAttributes();
                    System.out.println("[id=" + item.getIdentifier() + ",name=" + item.getName() + ",manufacturer="
                            + item.getManufacturer() + ",year=" + item.getYear() + ",month=" + item.getMonth()
                            + ",information=" + item.getInformation() + ",priceInPence=" + item.getPriceInPence()
                            + ",reservationNumber=" + held.getReservation() + "]");
                }
            }
        }
    }

    // Returns the total number of bean bags in stock.
    public int beanBagsInStock() {
        return available.size() + reserved.size();
    }

    // Returns the number of a type of bean bag in stock.
    public int beanBagsInStock(String id) throws BeanBagIDNotRecognisedException, IllegalIDException {
        // Returns the number of bean bags with a matching ID which are either reserved
        // or available.
        return countBeanBags(new ObjectArrayList[] { available, reserved }, id);
    }

    // Counts the number of matching IDs for any objects which are in the given
    // list(s).
    public int countBeanBags(ObjectArrayList[] objects, String id)
            throws BeanBagIDNotRecognisedException, IllegalIDException {
        // Starts the count at 0.
        int count = 0;
        boolean recognised = false;

        Checks.validId(id);

        // Accesses each element of array.
        for (ObjectArrayList object : objects) {
            // Iterates over the stock object and increments the count for each bean
            // bag with a matching ID.
            for (int j = 0; j < object.size(); j++) {
                BeanBag item;
                if (object == reserved) {
                    Reservation held = (Reservation) object.get(j);
                    item = held.getAttributes();
                } else {
                    item = (BeanBag) object.get(j);
                }
                if (item.getIdentifier().equalsIgnoreCase(id)) {
                    count += 1;
                    recognised = true;
                }
            }
        }
        // Throws an exception if no bean bag with the matching ID was found.
        if (!recognised) {
            throw new BeanBagIDNotRecognisedException("This bean bag ID '" + id + "' could not be found.");
        }

        // Returns the free text component of the first matching bean bag in the list.
        return count;
    }

    // Empties the stock of its contents.
    public void empty() {
        // Sets lists to be null for garbage collection before creating a new empty
        // list, which enables more efficient use of memory.
        available = null;
        available = new ObjectArrayList();
        reserved = null;
        reserved = new ObjectArrayList();
        resetSaleAndCostTracking();

        // Throws an AssertionError if the bean bag list wasn't emptied correctly.
        assert (available.size() == 0) : "Available list was not emptied correctly.";
        // Throws an AssertionError if the reserved list wasn't emptied correctly.
        assert (reserved.size() == 0) : "Reservation list was not emptied correctly.";
    }

    // Gets the details of a particular bean bag based on ID.
    public String getBeanBagDetails(String id) throws BeanBagIDNotRecognisedException, IllegalIDException {
        BeanBag item = null;
        boolean recognised = false;

        Checks.validId(id);

        // Iterates over the available stock list and increments the count for each bean
        // bag with a matching ID.
        for (int j = 0; j < available.size(); j++) {
            item = (BeanBag) available.get(j);
            // Breaks out of the loop if a matching bean bag was found (other matching bean
            // bags should have the same details).
            if (item.getIdentifier().equalsIgnoreCase(id)) {
                recognised = true;
                break;
            }
        }

        // Prevents iteration through the next for loop if bean bag details were found
        // from stock list.
        if (recognised) {
            return item.getInformation();

        }
        // Iterates over the reserved stock list and increments the count for each bean
        // bag with a matching ID.
        for (int j = 0; j < reserved.size(); j++) {
            item = (BeanBag) reserved.get(j);
            // Breaks out of the loop if a matching bean bag was found (other matching bean
            // bags should have the same details).
            if (item.getIdentifier().equalsIgnoreCase(id)) {
                recognised = true;
                break;
            }
        }
        // Returns the free text component of the first matching bean bag in the list.
        if (!recognised) {
            throw new BeanBagIDNotRecognisedException("This bean bag ID '" + id + "' could not be found.");
        }
        return item.getInformation();
    }

    // Gets the price of a given item based on its ID.
    public int getExistingPrice(String id) {

        // Iterates over the available stock list and get the price from the first
        // instance of a matching bean bag ID.
        for (int j = 0; j < available.size(); j++) {
            BeanBag item = (BeanBag) available.get(j);
            if (item.getIdentifier().equalsIgnoreCase(id)) {
                return item.getPriceInPence();
            }
        }

        // Returns 0 as a default price to represent the price not being set.
        return 0;
    }

    // Gets the number of unique bean bags (different types) in the stock list.
    public int getNumberOfDifferentBeanBagsInStock() {
        ObjectArrayList uniqueId = new ObjectArrayList();

        // Replaces the object IDs in each of these lists.
        ObjectArrayList[] objects = { available, reserved };
        BeanBag item;
        ObjectArrayList obj;

        // Iterates over the lists one by one.
        for (ObjectArrayList object : objects) {
            // Accesses each element of array.
            obj = object;
            // Updates the IDs in the available stock, reserved and sold list.
            for (int j = 0; j < obj.size(); j++) {
                if (obj == reserved) {
                    Reservation held = (Reservation) obj.get(j);
                    item = held.getAttributes();
                } else {
                    item = (BeanBag) obj.get(j);
                }

                String beanBagID = item.getIdentifier();
                boolean inList = false;

                // Iterates over the list of unique IDs and checks against duplicates.
                for (int i = 0; i < uniqueId.size(); i++) {
                    String id = (String) uniqueId.get(i);
                    if (id.equalsIgnoreCase(beanBagID)) {
                        inList = true;
                        break;
                    }
                }

                // Adds bean bag ID to list of unique IDs if no duplicates found.
                if (!inList) {
                    uniqueId.add(beanBagID);
                }
            }
        }

        return uniqueId.size();
    }

    // Gets the total number of sold bean bags using the sold list.
    public int getNumberOfSoldBeanBags() {
        return sold.size();
    }

    // Gets the total number of sold beans bags of a particular type (using ID)
    // using the sold list.
    public int getNumberOfSoldBeanBags(String id) throws BeanBagIDNotRecognisedException, IllegalIDException {
        // Returns the number of bean bags with a matching ID which have been sold.
        return countBeanBags(new ObjectArrayList[] { sold }, id);
    }

    // Gets the total price of bean bags in the reserved list.
    public int getTotalPriceOfReservedBeanBags() {
        int count = 0;

        // Iterates through the reserved list and adds each type of
        for (int j = 0; j < reserved.size(); j++) {
            Reservation held = (Reservation) reserved.get(j);
            count += held.getAttributes().getPriceInPence();
        }

        return count;
    }

    // Gets the total price of sold bean bags using the sold list.
    public int getTotalPriceOfSoldBeanBags() {
        int count = 0;

        // Iterates over the sold bean bags list to sum the prices.
        for (int j = 0; j < sold.size(); j++) {
            BeanBag item = (BeanBag) sold.get(j);
            count += item.getPriceInPence();
        }

        return count;
    }

    // Gets the total price of sold bean bags of a particular type (using ID) using
    // the sold list.
    public int getTotalPriceOfSoldBeanBags(String id) throws BeanBagIDNotRecognisedException, IllegalIDException {
        boolean recognised = false;
        int count = 0;

        Checks.validId(id);

        // Iterates over the sold list and counts bean bags which have a matching ID.
        for (int j = 0; j < sold.size(); j++) {
            BeanBag item = (BeanBag) sold.get(j);
            if (item.getIdentifier().equalsIgnoreCase(id)) {
                count += item.getPriceInPence();
                recognised = true;
            }
        }

        // Throws an exception for unrecognised bean bags.
        if (!recognised) {
            throw new BeanBagIDNotRecognisedException("This bean bag ID '" + id + "' could not be found.");
        }

        return count;
    }

    // Loads the store contents from a previously saved store.
    public void loadStoreContents(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            // Accesses each element of array.
            available = (ObjectArrayList) in.readObject();
            reserved = (ObjectArrayList) in.readObject();
            sold = (ObjectArrayList) in.readObject();
        }
    }

    // Responsible for the selling and cancellation of reservations in the reserved
    // bean bag stock.
    public Reservation manageReservations(int reservationNumber) throws ReservationNumberNotRecognisedException {
        boolean recognised = false;
        Reservation held = null;

        // Iterates over reservations stock to find matching reservation number.
        for (int j = 0; j < reserved.size(); j++) {
            held = (Reservation) reserved.get(j);
            if (held.getReservation() == reservationNumber) {
                recognised = true;
                break;
            }
        }

        // Throws an exception for unrecognised bean bags.
        if (!recognised) {
            throw new ReservationNumberNotRecognisedException(
                    "This reservation number '" + reservationNumber + "' is not recognised.");
        }

        return held;
    }

    // Replaces the ID of a bean bag with another given ID.
    public void replace(String oldId, String replacementId) throws BeanBagIDNotRecognisedException, IllegalIDException {
        boolean recognised = false;

        // Checks that the old ID exists.
        Checks.validId(oldId);
        // Checks that the new ID is of a correct format.
        Checks.validId(replacementId);

        // Replaces the object IDs in each of these lists.
        ObjectArrayList[] objects = { available, reserved, sold };
        BeanBag item;
        int i;
        ObjectArrayList obj;

        // Iterates over the lists one by one.
        for (i = 0; i < objects.length; i++) {
            // Accesses each element of array.
            obj = objects[i];

            // Updates the IDs in the available stock, reserved and sold list.
            for (int j = 0; j < obj.size(); j++) {

                if (obj == reserved) {
                    Reservation held = (Reservation) obj.get(j);
                    item = held.getAttributes();
                } else {
                    item = (BeanBag) obj.get(j);
                }
                if (item.getIdentifier().equalsIgnoreCase(oldId)) {
                    item.setIdentifier(replacementId);
                    recognised = true;
                }
            }
        }
        // Throws an exception if the old ID doesn't match any ID in stock or previously
        // in stock.
        if (!recognised) {
            throw new BeanBagIDNotRecognisedException("This bean bag ID '" + oldId + "' could not be found.");
        }
    }

    // Reserves a given quantity of bean bags based on ID.
    public int reserveBeanBags(int num, String id)
            throws BeanBagNotInStockException, InsufficientStockException, IllegalNumberOfBeanBagsReservedException,
            PriceNotSetException, BeanBagIDNotRecognisedException, IllegalIDException {
        boolean recognised = false;
        int reservationNumber = 0;

        // Checks if there are enough bean bags with the given ID in stock to reserve.
        Checks.validReservation(num, id, countBeanBags(new ObjectArrayList[] { available }, id));

        // For the number of bean bean bags the customer wishes to reserve, iterates
        // over the list of available bean bags and checks for matching bean
        // bags.
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < available.size(); j++) {
                BeanBag item = (BeanBag) available.get(j);
                if (item.getIdentifier().equalsIgnoreCase(id)) {
                    recognised = true;
                    // Throws an exception if no price set for this item.
                    if (item.getPriceInPence() == 0) {
                        throw new PriceNotSetException("No price has been set for this item '" + id + "'");
                    }

                    // Assigns a unique reservation number by iterating over the reserved list and
                    // checking there isn't a match.
                    for (int k = 0; k < reserved.size(); k++) {
                        Reservation held = (Reservation) reserved.get(k);
                        if (reservationNumber != held.getReservation()) {
                            break;
                        }
                        if (reservationNumber == held.getReservation()) {
                            reservationNumber += 1;
                        }

                    }

                    Reservation r = new Reservation(item, reservationNumber);
                    reserved.add(r);
                    available.remove(item);
                    // Prevents too many items from being reserved.
                    break;
                }
            }
        }

        // Throws an exception for unrecognised bean bags.
        if (!recognised) {
            throw new BeanBagIDNotRecognisedException("This bean bag ID '" + id + "' could not be found.");
        }

        return reservationNumber;
    }

    // Returns the total number of reserved bean bags in stock.
    public int reservedBeanBagsInStock() {
        return reserved.size();
    }

    // Resets the tracking of sales and costs.
    public void resetSaleAndCostTracking() {
        // Sets sold list to be null for garbage collection before creating a new empty
        // list, which enables more efficient use of memory.
        sold = null;
        sold = new ObjectArrayList();

        // Throws an AssertionError if the sales haven't been reset correctly.
        assert (this.getNumberOfSoldBeanBags() == 0) : "Sales have not been reset correctly.";
    }

    // Permanently saves the store contents so it can be used in other programs.
    public void saveStoreContents(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(available);
            out.writeObject(reserved);
            out.writeObject(sold);
            System.out.printf("Saved in %s%n", filename);
        }
    }

    // Sells a given quantity of a bean bag based on ID.
    public void sellBeanBags(int num, String id)
            throws BeanBagNotInStockException, InsufficientStockException, IllegalNumberOfBeanBagsSoldException,
            PriceNotSetException, BeanBagIDNotRecognisedException, IllegalIDException {
        boolean recognised = false;

        // Checks if there is enough available stock of that item to sell.
        Checks.validSale(num, id, countBeanBags(new ObjectArrayList[] { available }, id));

        // For the number of bean bags the customer wishes to sell, iterates over the
        // list of available stock and checks for matching bean bags.
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < available.size(); j++) {
                BeanBag item = (BeanBag) available.get(j);
                // Searches for matching IDs to find which bean bags to remove from stock list.
                if (item.getIdentifier().equalsIgnoreCase(id)) {
                    // Throws an exception if no price has been set for the bean bag.
                    if (item.getPriceInPence() == 0) {
                        throw new PriceNotSetException("No price has been set for this item '" + id + "'");
                    }

                    sold.add(item);
                    available.remove(item);
                    recognised = true;
                    // Prevents too many items from being sold.
                    break;
                }
            }
        }

        // Throws an exception for unrecognised bean bags.
        if (!recognised) {
            throw new BeanBagIDNotRecognisedException("This bean bag ID '" + id + "' could not be found.");
        }
    }

    // Sells a number of beans bags according to a reservation number.
    public void sellBeanBags(int reservationNumber) throws ReservationNumberNotRecognisedException {
        // Iterates over the reserved items for a bean bag with a matching reservation
        // number and adds it back to the stock list, removing it from the reserved
        // list.
        sold.add(manageReservations(reservationNumber).getAttributes());
        reserved.remove(manageReservations(reservationNumber));
    }

    // Sets the price of a bean bag in stock.
    public void setBeanBagPrice(String id, int priceInPence)
            throws InvalidPriceException, BeanBagIDNotRecognisedException, IllegalIDException {
        // Assumes bean bag is unrecognised until it finds the bean bag with the
        // mentioned ID.
        boolean recognised = false;
        ObjectArrayList[] objects = { available, reserved };

        // Checks the given ID, and sets the price to that ID if valid.
        Checks.validId(id);

        // Throws an exception if the user attempts to set a price of a negative value.
        if (priceInPence < 0) {
            throw new InvalidPriceException("The price '" + priceInPence + "' cannot be below zero pence.");
        }

        // Accesses each element of array.
        for (ObjectArrayList object : objects) {
            // Iterates over the stock object and increments the count for each bean
            // bag with a matching ID.
            for (int j = 0; j < object.size(); j++) {
                if (object == reserved) {
                    // checks whether the price of a reserved bean bag was reduced whilst
                    // waiting for final sale and offer them the lower of the two prices.
                    Reservation held = (Reservation) object.get(j);
                    BeanBag item = held.getAttributes();
                    if ((item.getIdentifier().equalsIgnoreCase(id)) && (priceInPence < item.getPriceInPence())) {
                        item.setPriceInPence(priceInPence);
                        recognised = true;
                    }
                } else {
                    // Iterates over the list of available bean bags and sets the given price to
                    // bean bags with matching IDs.
                    BeanBag item = (BeanBag) object.get(j);
                    if (item.getIdentifier().equalsIgnoreCase(id)) {
                        item.setPriceInPence(priceInPence);
                        recognised = true;
                    }
                }
            }
        }

        // Throws an exception for unrecognised bean bags.
        if (!recognised) {
            throw new BeanBagIDNotRecognisedException("This bean bag ID '" + id + "' could not be found.");
        }
    }

    // Cancels a reservation of bean bags based on reservation number.
    public void unreserveBeanBags(int reservationNumber) throws ReservationNumberNotRecognisedException {
        // Iterates over the reserved items for a bean bag with a matching reservation
        // number and adds it back to the stock list, removing it from the reserved
        // list.
        int existingPrice = getExistingPrice(manageReservations(reservationNumber).getAttributes().getIdentifier());

        // Updates bean bags to have the same price as the current price when moving
        // them back to the available bean bags list.
        if (existingPrice != 0) {
            manageReservations(reservationNumber).getAttributes().setPriceInPence(existingPrice);
        }
        available.add(manageReservations(reservationNumber).getAttributes());
        reserved.remove(manageReservations(reservationNumber));
    }
}
