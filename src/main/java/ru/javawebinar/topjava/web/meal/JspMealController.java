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
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;
import static org.springframework.format.annotation.DateTimeFormat.ISO.TIME;
import static ru.javawebinar.topjava.util.MealsUtil.getFilteredTos;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
@RequestMapping(value = "/meals")
public class JspMealController extends AbstractMealController {

    public JspMealController(MealService service) {
        super(service);
    }

    @GetMapping
    public String all(Model model) {
        int userId = authUserId();
        log.info("getAll for user {}", userId);
        List<MealTo> mealList = MealsUtil.getTos(service.getAll(userId), authUserCaloriesPerDay());
        model.addAttribute("meals", mealList);
        return "meals";
    }

    @GetMapping("/create")
    public String create(Model model) {
        var meal = new Meal(now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        model.addAttribute("meal", meal);
        return "mealForm";
    }

    @GetMapping("/update")
    public String update(@RequestParam("id") int id,
                         Model model) {
        int userId = authUserId();
        log.info("get meal {} for user {}", id, userId);
        model.addAttribute("meal", service.get(id, userId));
        return "mealForm";
    }

    @PostMapping
    public String save(
            @RequestParam(value = "id", required = false, defaultValue = "-1") int id,
            @RequestParam("dateTime") @DateTimeFormat(iso = DATE_TIME) LocalDateTime dateTime,
            @RequestParam("description") String description,
            @RequestParam("calories") int calories) {

        var meal = new Meal(id == -1 ? null : id, dateTime, description, calories);
        int userId = authUserId();
        if (meal.isNew()) {
            checkNew(meal);
            log.info("create {} for user {}", meal, userId);
            service.create(meal, userId);
        } else {
            assureIdConsistent(meal, id);
            log.info("update {} for user {}", meal, userId);
            service.update(meal, userId);
        }
        return "redirect:/meals";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        int userId = authUserId();
        log.info("delete meal {} for user {}", id, userId);
        service.delete(id, userId);
        return "redirect:/meals";
    }

    @GetMapping("/filter")
    public String filter(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DATE) LocalDate endDate,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(iso = TIME) LocalTime startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(iso = TIME) LocalTime endTime,
            Model model) {

        int userId = authUserId();
        List<Meal> mealsBetweenDates = service.getBetweenInclusive(startDate, endDate, userId);
        log.info("getBetween dates({} - {}) time({} - {}) for user {}", startDate, endDate, startTime, endTime, userId);
        List<MealTo> mealList = getFilteredTos(mealsBetweenDates, authUserCaloriesPerDay(), startTime, endTime);
        model.addAttribute("meals", mealList);
        return "meals";
    }
}
