import java.util.*;

class CabBookingSystem {
    private String cabType;
    private double farePerKm;

    public CabBookingSystem(String cabType, double farePerKm) {
        this.cabType = cabType;
        this.farePerKm = farePerKm;
    }

    public double calculateFare(double distance) {
        return distance * farePerKm;
    }

    public String getCabType() {
        return cabType;
    }
}

class Customer {
    private String name;
    private String phoneNumber;

    public Customer(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}

class Driver {
    private String name;
    private String licenseNumber;

    public Driver(String name, String licenseNumber) {
        this.name = name;
        this.licenseNumber = licenseNumber;
    }

    public String getName() {
        return name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }
}

interface Bookable {
    void bookRide(double distance);
}

class Sedan extends CabBookingSystem implements Bookable {
    public Sedan() {
        super("Sedan", 15.0);
    }

    public void bookRide(double distance) {
        System.out.println("Ride booked in " + getCabType() + ". Total fare: " + calculateFare(distance));
    }
}

class SUV extends CabBookingSystem implements Bookable {
    public SUV() {
        super("SUV", 20.0);
    }

    public void bookRide(double distance) {
        System.out.println("Ride booked in " + getCabType() + ". Total fare: " + calculateFare(distance));
    }
}

class BookingThread extends Thread {
    private Bookable cab;
    private double distance;

    public BookingThread(Bookable cab, double distance) {
        this.cab = cab;
        this.distance = distance;
    }

    public void run() {
        cab.bookRide(distance);
    }
}

class CabBookingService {
    private List<Customer> customers = new ArrayList<>();
    private List<Driver> drivers = new ArrayList<>();

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void addDriver(Driver driver) {
        drivers.add(driver);
    }

    public void listCustomers() {
        System.out.println("Customer List:");
        for (Customer customer : customers) {
            System.out.println("Customer Name: " + customer.getName() + ", Phone: " + customer.getPhoneNumber());
        }
    }

    public void listDrivers() {
        System.out.println("Driver List:");
        for (Driver driver : drivers) {
            System.out.println("Driver Name: " + driver.getName() + ", License: " + driver.getLicenseNumber());
        }
    }
}

public class MainBookingSystem {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            CabBookingService bookingService = new CabBookingService();

            System.out.println("Enter the number of customers:");
            int customerCount = Integer.parseInt(scanner.nextLine());

            for (int i = 0; i < customerCount; i++) {
                System.out.println("Enter details for Customer " + (i + 1) + ":");
                System.out.print("Name: ");
                String customerName = scanner.nextLine();
                System.out.print("Phone Number: ");
                String customerPhone = scanner.nextLine();

                Customer customer = new Customer(customerName, customerPhone);
                bookingService.addCustomer(customer);
            }

            System.out.println("Enter the number of drivers:");
            int driverCount = Integer.parseInt(scanner.nextLine());

            for (int i = 0; i < driverCount; i++) {
                System.out.println("Enter details for Driver " + (i + 1) + ":");
                System.out.print("Name: ");
                String driverName = scanner.nextLine();
                System.out.print("License Number: ");
                String licenseNumber = scanner.nextLine();

                Driver driver = new Driver(driverName, licenseNumber);
                bookingService.addDriver(driver);
            }

            bookingService.listCustomers();
            bookingService.listDrivers();

            System.out.println("Choose a cab type for booking (1 for Sedan, 2 for SUV):");
            int cabChoice = Integer.parseInt(scanner.nextLine());
            Bookable cab = (cabChoice == 1) ? new Sedan() : new SUV();

            System.out.print("Enter distance for the ride (in km): ");
            double distance = Double.parseDouble(scanner.nextLine());

            BookingThread bookingThread = new BookingThread(cab, distance);
            bookingThread.start();
            bookingThread.join();

            scanner.close();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
