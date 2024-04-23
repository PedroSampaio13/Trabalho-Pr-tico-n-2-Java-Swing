import java.util.Date;

public class Booking {

    private int id;
    private String guestFirstName;
    private String guestLastName;
    private Date checkInDate;
    private Date checkOutDate;
    private int numberOfAdults;
    private int numberOfChildren;
    private int roomId;
    private int statusId;
    
    public Booking(String guestFirstName, String guestLastName, Date checkInDate, Date checkOutDate, int roomId, int statusId) {
        this.guestFirstName = guestFirstName;
        this.guestLastName = guestLastName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfAdults = 0;
        this.numberOfChildren = 0;
        this.roomId = roomId;
        this.statusId = statusId;
    }

    public Booking(int id, String guestFirstName, String guestLastName, Date checkInDate, Date checkOutDate, int numberOfAdults, int numberOfChildren, int roomId, int statusId) {
        this.id = id;
        this.guestFirstName = guestFirstName;
        this.guestLastName = guestLastName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
        this.roomId = roomId;
        this.statusId = statusId;
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
    }
}
