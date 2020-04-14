package com.cibt.hms.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.cibt.hms.core.CRUDController;
import com.cibt.hms.entity.BookedRooms;
import com.cibt.hms.entity.Booking;
import com.cibt.hms.entity.BookingMatchingRooms;
import com.cibt.hms.entity.Hotel;
import com.cibt.hms.entity.Room;
import com.cibt.hms.entity.Staff;
import com.cibt.hms.repository.BookedRoomsRepository;
import com.cibt.hms.repository.BookingMatchingRoomsRepository;
import com.cibt.hms.repository.BookingRepository;
import com.cibt.hms.repository.CustomerRepository;
import com.cibt.hms.repository.FacilityRepository;
import com.cibt.hms.repository.HotelRepository;
import com.cibt.hms.repository.RoomRepository;
import com.cibt.hms.repository.StaffRepository;
import com.cibt.hms.repository.StatusRepository;
import com.cibt.hms.repository.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rooms")
public class RoomController extends CRUDController<Room> {
    public RoomController() {
        viewPath = "rooms";
        pageTitle = "Rooms";
        uri = "/rooms";
        activeMenu = "rooms";
    }

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingMatchingRoomsRepository bookingMatchingRoomsRepository;

    @Autowired
    private BookedRoomsRepository bookedRoomsRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private HttpSession session;

    @GetMapping(params = "hId")
    public String hotelRoomIndex(Model model, @RequestParam(value = "hId") Integer hotelId,
            @RequestParam(value = "roomIds", required = false) List<Integer> roomIds,
            @RequestParam(value = "bookingId", required = false) Integer bookedId) throws ParseException {
        model.addAttribute("hId", hotelId);// for sidebar
        Hotel hotel = hotelRepository.findById(hotelId).get();
        model.addAttribute("pageTitle", hotel.getName());

        if (hasRole("manager")) {
            model.addAttribute("staffs", hotel.getStaffs());
            model.addAttribute("tasks", taskRepository.findByHotelId(hotelId));
            model.addAttribute("facilities", facilityRepository.findByHotelId(hotelId));
            session.setAttribute("currentHotelId", hotelId);
            Sort sort = new Sort(Sort.Direction.ASC, "roomNo");
            model.addAttribute("rooms", roomRepository.findByHotelId(hotelId, sort));
        }

        if (hasRole("staff")) {
            Integer userId = getLoggedInUser().getId();
            Staff loggedInStaff = staffRepository.findByUserId(userId);
            List<Room> staffRooms = loggedInStaff.getRooms();
            staffRooms.sort(Comparator.comparing(Room::getRoomNo));
            model.addAttribute("staffId", loggedInStaff.getId());
            model.addAttribute("rooms", staffRooms);
            model.addAttribute("hId", hotelId);
        }

        if (hasRole("receptionist")) {
            if (roomIds.size() != 0) {
                List<Room> matchingRoomsAvailable = new ArrayList<Room>();
                List<Room> matchingRoomsBooked = new ArrayList<Room>();
                List<Room> matchingCheckedRooms = new ArrayList<Room>();
                List<Room> matchingRoomsBookedByLoggedInCustomer = new ArrayList<Room>();
                Integer bookingId = bookedId;
                Booking booking = bookingRepository.findById(bookingId).get();
                for (Integer roomId : roomIds) {
                    // if booking date is already occupied for a room
                    BookedRooms bookedRoom = bookedRoomsRepository.findBookedRoomByBookingDateRange(roomId,
                            booking.getStartDate(), booking.getEndDate());

                    if (bookedRoom != null) {
                        Room matchingRoomBooked = roomRepository.findById(roomId).get();
                        if (bookedRoom.isChecked()) {
                            matchingCheckedRooms.add(matchingRoomBooked);
                        } else {
                            if (bookedRoom.getBooking().getId() == bookingId) {
                                matchingRoomsBookedByLoggedInCustomer.add(matchingRoomBooked);
                            } else {
                                matchingRoomsBooked.add(matchingRoomBooked);
                            }
                        }
                    } else {
                        Room matchingRoomAvailable = roomRepository.findById(roomId).get();
                        matchingRoomsAvailable.add(matchingRoomAvailable);
                    }
                }
                model.addAttribute("bookingId", bookedId);
                model.addAttribute("bookingStartDate", booking.getStartDate());
                model.addAttribute("availableRooms", matchingRoomsAvailable);
                model.addAttribute("bookedRooms", matchingRoomsBooked);
                model.addAttribute("myBookedRooms", matchingRoomsBookedByLoggedInCustomer);
                model.addAttribute("checkedRooms", matchingCheckedRooms);
            } else {
                List<Room> currentlyAvailableRooms = new ArrayList<Room>();
                List<Room> currentlyBookedRooms = new ArrayList<Room>();
                model.addAttribute("customers", customerRepository.findAll());
                Sort sort = new Sort(Sort.Direction.ASC, "roomNo");
                Date currentDate = getFormattedDate(new Date());
                for (Room room : roomRepository.findByHotelId(hotelId, sort)) {
                    if (bookedRoomsRepository.findByStartDateAndRoomId(currentDate, room.getId()) != null) {
                        currentlyBookedRooms.add(room);
                    } else {
                        currentlyAvailableRooms.add(room);
                    }
                }
                model.addAttribute("rooms", currentlyAvailableRooms);
                model.addAttribute("currentlyBookedRooms", currentlyBookedRooms);
            }
        }

        if (hasRole("customer")) {
            List<Room> matchingRoomsAvailable = new ArrayList<Room>();
            List<Room> matchingRoomsBooked = new ArrayList<Room>();
            List<Room> matchingRoomsChecked = new ArrayList<Room>();
            Integer bookingId = (Integer) session.getAttribute("bookingId");
            if (bookingId != null) {
                Booking booking = bookingRepository.findById(bookingId).get();
                session.removeAttribute("bookingId");
                if (roomIds.size() != 0) {
                    for (Integer roomId : roomIds) {
                        // record All matched rooms for booking
                        BookingMatchingRooms bookingMatchingRooms = new BookingMatchingRooms();
                        bookingMatchingRooms.setBooking(booking);
                        // if booking date is already occupied for a room
                        BookedRooms bookedRoom = bookedRoomsRepository.findBookedRoomByBookingDateRange(roomId,
                                booking.getStartDate(), booking.getEndDate());
                        if (bookedRoom != null) {
                            Room matchingRoomBooked = roomRepository.findById(roomId).get();
                            if (bookedRoom.isChecked()) {
                                matchingRoomsChecked.add(matchingRoomBooked);
                            } else {
                                matchingRoomsBooked.add(matchingRoomBooked);
                            }
                            bookingMatchingRooms.setRoom(matchingRoomBooked);
                        } else {
                            Room matchingRoomAvailable = roomRepository.findById(roomId).get();
                            matchingRoomsAvailable.add(matchingRoomAvailable);
                            bookingMatchingRooms.setRoom(matchingRoomAvailable);
                        }
                        bookingMatchingRoomsRepository.save(bookingMatchingRooms);
                    }
                }
            }
            model.addAttribute("rooms", matchingRoomsAvailable);
            model.addAttribute("bookedRooms", matchingRoomsBooked);
            model.addAttribute("checkedRooms", matchingRoomsChecked);

        }
        return viewPath + "/index";
    }

    @GetMapping(value = "/matching/booking/{id}")
    @ResponseBody
    public List<Room> getRoomsByBookingId(@PathVariable("id") Integer bookingId) {
        return bookingRepository.findById(bookingId).get().getMatchingRooms();
    }

    @PostMapping(value = "/create")
    @Transactional
    public String create(Room room) {
        // set status dirty by default
        room.setStatus(statusRepository.findById(1).get());
        roomRepository.save(room);
        return "redirect:" + uri + "?hId=" + room.getHotel().getId() + "&success";
    }

    @PostMapping(value = "/check-room-number")
    @ResponseBody
    public boolean create(@RequestParam("roomNo") Integer roomNo, @RequestParam("hotelId") Integer hotelId) {
        if(roomRepository.countByRoomNoAndHotelId(roomNo, hotelId) >0){
            return true;
        }
        return false;
    }

    @PostMapping(value = "/change-status")
    @Transactional
    @ResponseBody
    public boolean changeStatus(@RequestParam("roomId") Integer id, @RequestParam("status") String status) {
        Room room = roomRepository.findById(id).get();
        if (!room.getStatus().getName().equalsIgnoreCase(status)) {
            room.setStatus(statusRepository.findByName(status));
            roomRepository.save(room);
            return room.getId() > 0;
        }
        return false;
    }

    public Date getFormattedDate(Date date) throws ParseException {
        Calendar currDate = Calendar.getInstance();
        currDate.setTime(date);
        ;
        currDate.set(Calendar.HOUR_OF_DAY, 12);
        currDate.set(Calendar.MINUTE, 0);
        currDate.set(Calendar.SECOND, 0);
        currDate.set(Calendar.MILLISECOND, 0);
        return currDate.getTime();
    }
}