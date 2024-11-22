import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CabBookingSystemDatabase {

    private static void ensureTableExists() {
    String createTableQuery = """
        CREATE TABLE IF NOT EXISTS bookings (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            customer_name TEXT NOT NULL,
            cab_type TEXT NOT NULL,
            email TEXT NOT NULL,
            kilometers REAL NOT NULL,
            fare REAL NOT NULL
        );
    """;
    try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
        stmt.execute(createTableQuery);
        System.out.println("Table ensured.");
    } catch (SQLException e) {
        System.err.println("Error creating table: " + e.getMessage());
    }
}
    private static final String URL = "jdbc:sqlite:cab.db";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    private static final Lock bookingLock = new ReentrantLock();
    private static final double RATE_PER_KM = 15.0;
    static class Booking {
        private final int id;
        private final String customerName;
        private final String cabType;
        private final String email;
        private final double kilometers;
        private final double fare;

        public Booking(String customerName, String cabType, String email, double kilometers) {
            this.id = 0;
            this.customerName = customerName;
            this.cabType = cabType;
            this.email = email;
            this.kilometers = kilometers;
            this.fare = kilometers * RATE_PER_KM;
        }

        public Booking(int id, String customerName, String cabType, String email, double kilometers, double fare) {
            this.id = id;
            this.customerName = customerName;
            this.cabType = cabType;
            this.email = email;
            this.kilometers = kilometers;
            this.fare = fare;
        }

        public int getId() {
            return id;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getCabType() {
            return cabType;
        }

        public String getEmail() {
            return email;
        }

        public double getKilometers() {
            return kilometers;
        }

        public double getFare() {
            return fare;
        }

        @Override
        public String toString() {
            return String.format(
                "ID: %d, Customer: %s, Email: %s, Cab Type: %s, Kilometers: %.2f, Fare: ₹%.2f",
                id, customerName, email, cabType, kilometers, fare
            );
        }
    }

    public static void addBooking(Booking booking) {
        bookingLock.lock();
        String query = "INSERT INTO bookings (customer_name, cab_type, email, kilometers, fare) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, booking.getCustomerName());
            stmt.setString(2, booking.getCabType());
            stmt.setString(3, booking.getEmail());
            stmt.setDouble(4, booking.getKilometers());
            stmt.setDouble(5, booking.getFare());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            bookingLock.unlock();
        }
    }
    public static List<Booking> getAllBookings() {
        List<Booking> bookingList = new ArrayList<>();
        String query = "SELECT * FROM bookings";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                bookingList.add(new Booking(
                    rs.getInt("id"),
                    rs.getString("customer_name"),
                    rs.getString("cab_type"),
                    rs.getString("email"),
                    rs.getDouble("kilometers"),
                    rs.getDouble("fare")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookingList;
    }
    public static boolean deleteBooking(int id) {
        String query = "DELETE FROM bookings WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("SQLite JDBC driver loaded successfully. Database connected.");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database. Please check the configuration.");
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        testConnection();
        ensureTableExists();
        Scanner scanner = new Scanner(System.in);
            while (true) {
                try{
                System.out.println("\nMain Booking System");
                System.out.println("1. Add Booking");
                System.out.println("2. View All Bookings");
                System.out.println("3. Delete Booking");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        System.out.print("Enter Customer Name: ");
                        String customerName = scanner.nextLine();
                        System.out.print("Enter Cab Type (Sedan/SUV): ");
                        String cabType = scanner.nextLine();
                        while (!cabType.equalsIgnoreCase("Sedan") && !cabType.equalsIgnoreCase("SUV")) {
                            System.out.print("Invalid cab type. Please enter 'Sedan' or 'SUV': ");
                            cabType = scanner.nextLine();
                        }
                        System.out.print("Enter Email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter Kilometers: ");
                        double kilometers = scanner.nextDouble();

                        Booking booking = new Booking(customerName, cabType, email, kilometers);
                        addBooking(booking);
                        System.out.println("Booking added successfully!");
                        break;
                    case 2 :
                        List<Booking> bookings = getAllBookings();
                        System.out.println("Bookings:");
                        if (bookings.isEmpty()) {
                            System.out.println("No bookings available.");
                        } else {
                            bookings.forEach(System.out::println);
                        }
                        break;
                    case 3 :
                        System.out.print("Enter Booking ID to delete: ");
                        int id = scanner.nextInt();
                        if (deleteBooking(id)) {
                            System.out.println("Booking deleted successfully!");
                        } else {
                            System.out.println("Booking ID not found.");
                        }
                        break;
                    case 4 :
                        System.out.println("Exiting...");
                        System.exit(0);
                        break;

                    default : System.out.println("Invalid choice. Please try again.");
                }
                } catch (Exception e) {
            e.printStackTrace();
        }
            }
    }
}