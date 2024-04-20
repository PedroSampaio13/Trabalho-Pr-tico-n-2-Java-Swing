import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RoomBookingSystem {

    public List<Room> searchAvailableRooms(List<Room> rooms, List<Booking> bookings, int numberOfAdults, int numberOfChildren, Date checkInDate, Date checkOutDate, int canceledStatus) {
        return rooms.stream()
            .filter(room -> room.getAdultsCapacity() >= numberOfAdults && (room.getAdultsCapacity() + room.getChildrenCapacity()) >= (numberOfAdults + numberOfChildren))
            .filter(room -> bookings.stream().noneMatch(booking -> booking.getRoomId() == room.getId() &&
                                                          (checkInDate.compareTo(booking.getCheckInDate()) >= 0 &&
                                                           checkInDate.compareTo(booking.getCheckOutDate()) <= 0) ||
                                                          (checkOutDate.compareTo(booking.getCheckInDate()) >= 1 &&
                                                           checkOutDate.compareTo(booking.getCheckOutDate()) <= 0) &&
                                                          booking.getStatusId() != canceledStatus))

            .sorted(Comparator.comparing(Room::getPrice))
            .collect(Collectors.toList());
    }
}
