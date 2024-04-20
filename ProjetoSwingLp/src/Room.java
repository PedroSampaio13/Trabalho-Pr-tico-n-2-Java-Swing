public class Room {

    private int id;
    private int roomNumber;
    private int adultsCapacity;
    private int childrenCapacity;
    private float price;

    public Room(int id, int roomNumber, int adultsCapacity, int childrenCapacity, float price) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.adultsCapacity = adultsCapacity;
        this.childrenCapacity = childrenCapacity;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getAdultsCapacity() {
        return adultsCapacity;
    }

    public void setAdultsCapacity(int adultsCapacity) {
        this.adultsCapacity = adultsCapacity;
    }

    public int getChildrenCapacity() {
        return childrenCapacity;
    }

    public void setChildrenCapacity(int childrenCapacity) {
        this.childrenCapacity = childrenCapacity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}


