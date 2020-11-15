package ru.javawebinar.topjava.web.meal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.springframework.format.annotation.DateTimeFormat.ISO.*;

@Controller
@RequestMapping(value = "/meals")
public class JspMealController extends AbstractMealController {

    public JspMealController(MealService service) {
        super(service);
    }

    @GetMapping
    public String all(Model model) {
        model.addAttribute("meals", getAll());
        return "meals";
    }

    @GetMapping("/create")
    public String createMeal(Model model) {
        var meal = new Meal(now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        model.addAttribute("meal", meal);
        return "mealForm";
    }

    @GetMapping("/update")
    public String updateMeal(@RequestParam("id") int id, Model model) {
        model.addAttribute("meal", get(id));
        return "mealForm";
    }

    @PostMapping
    public String save(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("dateTime") @DateTimeFormat(iso = DATE_TIME) LocalDateTime dateTime,
            @RequestParam("description") String description,
            @RequestParam("calories") int calories) {

        var meal = new Meal(id, dateTime, description, calories);
        if (meal.isNew()) {
            create(meal);
        } else {
            update(meal, id);
        }
        return "redirect:/meals";
    }

    @GetMapping("/delete")
    public String deleteMeal(@RequestParam("id") int id) {
        delete(id);
        return "redirect:/meals";
    }

    @GetMapping("/filter")
    public String filter(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DATE) LocalDate endDate,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(iso = TIME) LocalTime startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(iso = TIME) LocalTime endTime,
            Model model) {

        List<MealTo> mealList = getBetween(startDate, startTime, endDate, endTime);
        model.addAttribute("meals", mealList);
        return "meals";
    }
}
