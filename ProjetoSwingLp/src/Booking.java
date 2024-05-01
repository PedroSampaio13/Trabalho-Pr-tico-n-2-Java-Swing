import java.util.Date;

public class Booking {
    private static int nextId = 1; // Variável de classe para o próximo ID disponível
    private int id;
    private String guestFirstName;
    private String guestLastName;
    private Date checkInDate;
    private Date checkOutDate;
    private int numberOfAdults;
    private int numberOfChildren;
    private int roomId;
    private int statusId;
    Status status;

    public Booking(String guestFirstName, String guestLastName, Date checkInDate, Date checkOutDate, int numberOfAdults, int numberOfChildren, int roomId, int statusId) {
        this.id = nextId++; // Incrementa o ID e atribui o próximo ID disponível
        this.guestFirstName = guestFirstName;
        this.guestLastName = guestLastName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
        this.roomId = roomId;
        this.statusId = statusId;
        this.status = new Status(statusId);
    }

    public Booking(String guestFirstName, String guestLastName, Date checkInDate, Date checkOutDate, int roomId, int statusId) {
        this.guestFirstName = guestFirstName;
        this.guestLastName = guestLastName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfAdults = 0;
        this.numberOfChildren = 0;
        this.roomId = roomId;
        this.statusId = statusId;
        this.status = new Status(statusId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGuestFirstName() {
        return guestFirstName;
    }

    public void setGuestFirstName(String guestFirstName) {
        this.guestFirstName = guestFirstName;
    }

    public String getGuestLastName() {
        return guestLastName;
    }

    public void setGuestLastName(String guestLastName) {
        this.guestLastName = guestLastName;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    public void setNumberOfAdults(int numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
        this.status = new Status(statusId);
    }

    // Classe interna Status
    class Status {

        private String state;

        public Status(int statusId) {
            // Defina a lógica para associar o ID do status à classe Status
            switch (statusId) {
                case 1:
                    this.state = "Booked";
                    break;
                case 2:
                    this.state = "CheckedIn";
                    break;
                case 3:
                    this.state = "CheckedOut";
                    break;
                case 4:
                    this.state = "Canceled";
                    break;
                default:
                    this.state = "Unknown";
                    break;
            }
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

}
