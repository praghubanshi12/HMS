package com.cibt.hms.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public abstract class CRUDController<T> extends SecuredController {

    @Autowired
    protected JpaRepository<T, Integer> repository;

    @GetMapping()
    public String index(Model model) {
        return viewPath + "/index";
    }

    @GetMapping(value = "/create")
    public String create() {
        return viewPath + "/create";
    }

    @GetMapping(value = "/edit/{id}")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("record", repository.findById(id));
        return viewPath + "/edit";
    }

    @GetMapping(value = "/table")
    public String table(Model model) {
        model.addAttribute("records", repository.findAll());
        return viewPath + "/table";
    }

    @GetMapping(value = "/json")
    @ResponseBody
    public List<T> json() {
        return repository.findAll();
    }

    @GetMapping(value = "/{id}/json")
    @ResponseBody
    public T jsonById(@PathVariable("id") int id) {
        return repository.findById(id).get();
    }

    @PostMapping(value = "/save")
    public String save(T model) {
        repository.save(model);
        return "redirect:" + uri + "?success";
    }

    @PostMapping(value = "/save-json")
    @ResponseBody
    public T saveJson(T model) {
        repository.save(model);
        return model;
    }

}