package com.cibt.hms.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.cibt.hms.core.CRUDController;
import com.cibt.hms.entity.BookedRooms;
import com.cibt.hms.entity.Booking;
import com.cibt.hms.repository.BookedRoomsRepository;
import com.cibt.hms.repository.BookingMatchingRoomsRepository;
import com.cibt.hms.repository.BookingRepository;
import com.cibt.hms.repository.CustomerRepository;
import com.cibt.hms.repository.HotelRepository;
import com.cibt.hms.repository.RoomFacilityRepository;
import com.cibt.hms.repository.RoomRepository;
import com.cibt.hms.repository.StatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/bookings")
public class BookingController extends CRUDController<Booking> {

    public BookingController() {
        viewPath = "booking";
        pageTitle = "Bookings";
        uri = "/bookings";
        activeMenu = "booking";
    }

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookedRoomsRepository bookedRoomsRepository;

    @Autowired
    private BookingMatchingRoomsRepository bookingMatchingRoomsRepository;

    @Autowired
    private RoomFacilityRepository roomFacilityRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HttpSession session;

    @GetMapping(params = "hId")
    public String bookingIndex(Model model, @RequestParam(value = "hId") Integer hotelId) {
        session.setAttribute("currentHotelId", hotelId);
        model.addAttribute("hId", hotelId);
        return viewPath + "/index";
    }

    @Override
    public String table(Model model) {
        Integer hotelId = (Integer) session.getAttribute("currentHotelId");
        Sort sort = new Sort(Sort.Direction.ASC, "createdDate");
        List<Booking> validBookings = bookingRepository.findByHotelIdAndStatusIdIn(hotelId, Arrays.asList(4, 7, 9), sort);
        model.addAttribute("bookings", validBookings);
        return viewPath + "/table";
    }

    @GetMapping(value = "/form")
    public String bookingForm(Model model, @RequestParam("hId") Integer hotelId) {
        model.addAttribute("facilities", hotelRepository.findById(hotelId).get().getFacilities());
        model.addAttribute("hId", hotelId);
        model.addAttribute("cId", customerRepository.findByEmail(getLoggedInUser().getEmail()).getId());
        return viewPath + "/customer/form";
    }

    @PostMapping(value = "/booked/room")
    @ResponseBody
    @Transactional
    public boolean addBookedRoom(@RequestParam("bookingId") Integer bookingId, @RequestParam("roomId") Integer roomId) {
        Booking booking = repository.findById(bookingId).get();
        booking.setStatus(statusRepository.findById(7).get());
        BookedRooms bookedRoom = new BookedRooms();
        bookedRoom.setBooking(booking);
        bookedRoom.setRoom(roomRepository.findById(roomId).get());
        bookedRoom.setStartDate(booking.getStartDate());
        bookedRoom.setEndDate(booking.getEndDate());
        bookedRoomsRepository.save(bookedRoom);
        return bookedRoom.getId() > 0;
    }

    @PostMapping(value = "/booked/room/cancel")
    @ResponseBody
    @Transactional
    public boolean removeBookedRoom(@RequestParam("bookingId") Integer bookingId,
            @RequestParam("roomId") Integer roomId) {
        return bookedRoomsRepository.deleteByBookingIdAndRoomId(bookingId, roomId) > 0;
    }

    @PostMapping(value = "/cancel")
    @ResponseBody
    @Transactional
    public boolean cancelBooking(@RequestParam("bookingId") Integer bookingId) {
        Booking booking = repository.findById(bookingId).get();
        booking.setStatus(statusRepository.findById(8).get());
        repository.save(booking);

        bookingMatchingRoomsRepository.deleteByBookingId(bookingId);
        bookedRoomsRepository.deleteByBookingId(bookingId);
        return booking.getId() > 0;
    }

    @PostMapping(value = "/create")
    @ResponseBody
    @Transactional
    public List<Integer> create(Model model, Booking booking,
            @RequestParam(value = "facilityIds", required = false) List<Integer> facilityIds,
            @RequestParam("roomType") String roomType, @RequestParam("bedType") String bedType,
            @RequestParam("isSmoking") boolean isSmoking, @RequestParam("isBalcony") boolean isBalcony,
            @RequestParam("bookingStartDate") String startDate, @RequestParam("bookingEndDate") String endDate)
            throws Exception {
        List<Integer> matchingRooms = roomFacilityRepository.findRoomIdsByFacilities(facilityIds, facilityIds.size(),
                roomType, bedType, isSmoking, isBalcony);

        if (matchingRooms.size() > 0) {
            Date date1 = getFormattedDate(startDate);
            Date date2 = getFormattedDate(endDate);
            booking.setStartDate(date1);
            booking.setEndDate(date2);
            booking.setStatus(statusRepository.findById(4).get());
            bookingRepository.save(booking);
            session.setAttribute("bookingId", booking.getId());
            return matchingRooms;
        }
        return null;
    }

    public Date getFormattedDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar currDate = Calendar.getInstance();
        currDate.setTime(sdf.parse(date));
        currDate.set(Calendar.HOUR_OF_DAY, 12);
        currDate.set(Calendar.MINUTE, 0);
        currDate.set(Calendar.SECOND, 0);
        currDate.set(Calendar.MILLISECOND, 0);
        return currDate.getTime();
    }
}