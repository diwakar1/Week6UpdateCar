package com.example.week6challenge;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    CarRepository carRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listCars(Model model){

        model.addAttribute("cars",carRepository.findAll());
        return "list";
    }

    @GetMapping("/addcar")
    public String addCar(Model model){
        model.addAttribute("car",new Car());
        model.addAttribute("categories",categoryRepository.findAll());
        return "addcar";

    }

    @GetMapping("/addcategory")
    public String addCategory(Model model) {
        model.addAttribute("category", new Category());
        return "addcategory";



    }

    @PostMapping("/addcar")
    public String processCar(@Valid @ModelAttribute Car car,BindingResult result,
                               @RequestParam("file")MultipartFile file,Model model){

        if(result.hasErrors()){
            model.addAttribute("categories",categoryRepository.findAll());
            return "addcar";
        }

        try{
            Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            car.setImage(uploadResult.get("url").toString());
            carRepository.save(car);

        }
        catch (IOException e){
            e.printStackTrace();

        }
        return "redirect:/";

    }


    @PostMapping("/addcategory")
    public String processForm(@Valid Category category, BindingResult result){

        if(result.hasErrors()){
            return  "addcategory";

        }
        categoryRepository.save(category);
        return "redirect:/";

    }

    @RequestMapping("/detail/{id}")
    public String details(@PathVariable("id") long id, Model model){

        model.addAttribute("car", carRepository.findById(id).get());
        return "showdetail";

    }

    @RequestMapping("/update/{id}")
    public String updateCar(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("car", carRepository.findById(id));
        model.addAttribute("categories", categoryRepository.findAll());
        return "addcar";
    }
    @RequestMapping("/delete/{id}")
    public String deleteCar(@PathVariable("id") long id){

        carRepository.deleteById(id);
        return "redirect:/";
    }


}
