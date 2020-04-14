package com.cibt.hms.controller;

import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.cibt.hms.core.CRUDController;
import com.cibt.hms.entity.RoomTask;
import com.cibt.hms.entity.Staff;
import com.cibt.hms.entity.Task;
import com.cibt.hms.repository.RoomTaskRepository;
import com.cibt.hms.repository.StaffRepository;
import com.cibt.hms.repository.StatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/tasks")
public class TaskController extends CRUDController<Task> {
    public TaskController() {
        viewPath = "tasks";
        pageTitle = "Tasks";
        uri = "/tasks";
        activeMenu = "task";
    }

    @Autowired
    private RoomTaskRepository roomTaskRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private HttpSession session;

    @GetMapping(params = "hId")
    public String hotelTasksIndex(Model model, @RequestParam("hId") Integer hotelId) {
        model.addAttribute("hId", hotelId);// for sidebar
        session.setAttribute("currentHotelId", hotelId);
        return super.index(model);
    }

    @Override
    public String table(Model model) {
        if (hasRole("manager")) {
            Integer hotelId = (Integer) session.getAttribute("currentHotelId");
            model.addAttribute("records", roomTaskRepository.findByRoomHotelId(hotelId));
            return viewPath + "/table";
        }
        if (hasRole("staff")) {
            Integer userId = getLoggedInUser().getId();
            Staff loggedInStaff = staffRepository.findByUserId(userId);
            model.addAttribute("records", loggedInStaff.getRoomTasks());
            return viewPath + "/table";
        }
        return super.table(model);
    }

    @GetMapping(value="/room/{roomId}/staff/{staffId}")
    @ResponseBody
    public List<RoomTask> getRoomTasksByRoomIdAndStaffId(@PathVariable("roomId") Integer roomId, @PathVariable("staffId") Integer staffId){
        List<RoomTask> roomTasks = roomTaskRepository.findByRoomIdAndStaffId(roomId, staffId);
        roomTasks.sort(Comparator.comparing(RoomTask::getPriority));
        return roomTasks;
    }

    @Override
    public Task saveJson(Task task) {
        Task taskNew = repository.save(task);
        return taskNew;
    }
    
    @PostMapping(value = "/room/save")
    @Transactional
    public String createRoomTask(RoomTask roomTask) {
        roomTask.setStatus(statusRepository.findById(4).get());
        roomTaskRepository.save(roomTask);
        return "redirect:/rooms?hId=" + roomTask.getRoom().getHotel().getId();
    }

    @PostMapping(value = "/check")
    @Transactional
    @ResponseBody
    public boolean checkRoomTask(@RequestParam("isChecked") boolean isChecked, @RequestParam("id") Integer id) {
        RoomTask roomTask = roomTaskRepository.findById(id).get();
        if (isChecked) {
            roomTask.setStatus(statusRepository.findById(5).get());
        } else {
            roomTask.setStatus(statusRepository.findById(4).get());
        }
        roomTaskRepository.save(roomTask);
        return roomTask.getId() > 0;
    }

}