package com.cibt.hms.controller;

import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.cibt.hms.core.CRUDController;
import com.cibt.hms.entity.BookedRooms;
import com.cibt.hms.entity.Booking;
import com.cibt.hms.entity.Checkin;
import com.cibt.hms.entity.Room;
import com.cibt.hms.repository.BookedRoomsRepository;
import com.cibt.hms.repository.BookingMatchingRoomsRepository;
import com.cibt.hms.repository.BookingRepository;
import com.cibt.hms.repository.CheckinRepository;
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
@RequestMapping(value = "/checkins")
public class CheckinController extends CRUDController<Checkin> {

    public CheckinController() {
        viewPath = "checkin";
        pageTitle = "Check Ins";
        uri = "/checkins";
        activeMenu = "checkin";
    }

    @Autowired
    private CheckinRepository checkinRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private BookedRoomsRepository bookedRoomsRepository;

    @Autowired
    private BookingMatchingRoomsRepository bookingMatchingRoomsRepository;

    @Autowired
    private HttpSession session;

    @GetMapping(params = "hId")
    public String checkInIndex(Model model, @RequestParam("hId") Integer hotelId) {
        model.addAttribute("hId", hotelId);// for sidebar
        session.setAttribute("currentHotelId", hotelId);
        return super.index(model);
    }

    @Override
    public String table(Model model) {
        Integer hotelId = (Integer) session.getAttribute("currentHotelId");
        if(hasRole("receptionist")){
            model.addAttribute("records", checkinRepository.findByRoomHotelIdAndCheckedTrue(hotelId));
        }

        if(hasRole("manager")){
            Sort sort = new Sort(Sort.Direction.DESC, "checked");
            model.addAttribute("records", checkinRepository.findByRoomHotelId(hotelId, sort));
        }

        session.removeAttribute("currentHotelId");
        return viewPath + "/table";
    }

    @PostMapping(value = "/booking/save")
    @Transactional
    @ResponseBody
    public boolean saveCheckinByBooking(Checkin model) {
        Booking checkedBooking = bookingRepository.findById(model.getBooking().getId()).get();
        Room checkedRoom = roomRepository.findById(model.getRoom().getId()).get();

        model.setChecked(true);
        model.setCheckinDate(new Date());
        model.setCustomer(checkedBooking.getCustomer());
        model.setNoOfPeople(checkedBooking.getNoOfPeople());
        repository.save(model);

        checkedBooking.setStatus(statusRepository.findById(9).get());
        bookingRepository.save(checkedBooking);

        checkedRoom.setChecked(true);
        roomRepository.save(checkedRoom);

        BookedRooms bookedRoom = bookedRoomsRepository.findByBookingIdAndRoomId(checkedBooking.getId(),
                checkedRoom.getId());
        if (bookedRoom == null) {
            bookedRoom = new BookedRooms();
            bookedRoom.setBooking(checkedBooking);
            bookedRoom.setRoom(checkedRoom);
            bookedRoom.setStartDate(checkedBooking.getStartDate());
            bookedRoom.setEndDate(checkedBooking.getEndDate());
        }
        bookedRoom.setChecked(true);
        bookedRoomsRepository.save(bookedRoom);
        return model.getId() > 0;
    }

    @PostMapping(value = "/direct/save")
    @Transactional
    public String saveCheckinDirect(Checkin model) {
        Room checkedRoom = roomRepository.findById(model.getRoom().getId()).get();
        model.setChecked(true);
        model.setCheckinDate(new Date());
        repository.save(model);

        checkedRoom.setChecked(true);
        roomRepository.save(checkedRoom);

        return "redirect:/rooms?hId=" + checkedRoom.getHotel().getId() + "&roomIds";
    }

    @PostMapping(value = "/check-out")
    @ResponseBody
    @Transactional
    public boolean checkOut(Checkin model) {
        Checkin checkin = checkinRepository.findById(model.getId()).get();
        checkin.setCheckouDate(new Date());
        checkin.setChecked(false);
        repository.save(checkin);

        Room checkedOutRoom = roomRepository.findById(checkin.getRoom().getId()).get();
        checkedOutRoom.setChecked(false);
        roomRepository.save(checkedOutRoom);

        if(checkin.getBooking() !=null){
            Integer bookingId = checkin.getBooking().getId();
            bookingRepository.findById(bookingId).get().setStatus(statusRepository.findById(10).get());
            bookedRoomsRepository.deleteByBookingId(bookingId);
            bookingMatchingRoomsRepository.deleteByBookingId(bookingId);
        }

        return checkin.getId()>0;
    }

}