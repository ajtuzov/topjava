package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.parse;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
@RequestMapping(value = "/meals")
public class JspMealController {

    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);

    private final MealService mealService;

    public JspMealController(MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping
    public String all(Model model) {
        int userId = SecurityUtil.authUserId();
        log.info("getAll for user {}", userId);
        List<MealTo> mealList = MealsUtil.getTos(mealService.getAll(userId), SecurityUtil.authUserCaloriesPerDay());
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
    public String update(@RequestParam("id") Integer id,
                         Model model) {
        int userId = SecurityUtil.authUserId();
        log.info("get meal {} for user {}", id, userId);
        model.addAttribute("meal", mealService.get(id, userId));
        return "mealForm";
    }

    @PostMapping("/save")
    public String save(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("dateTime") String dateTime,
            @RequestParam("description") String description,
            @RequestParam("calories") Integer calories) {

        var meal = new Meal(parse(dateTime), description, calories);
        int userId = SecurityUtil.authUserId();
        if (id == null) {
            checkNew(meal);
            log.info("create {} for user {}", meal, userId);
            mealService.create(meal, userId);
        } else {
            assureIdConsistent(meal, id);
            log.info("update {} for user {}", meal, userId);
            mealService.update(meal, userId);
        }
        return "redirect:/meals";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") Integer id) {
        int userId = SecurityUtil.authUserId();
        log.info("delete meal {} for user {}", id, userId);
        mealService.delete(id, userId);
        return "redirect:/meals";
    }

    @GetMapping("/filter")
    public String filter(@RequestParam("startDate") String startDate,
                         @RequestParam("endDate") String endDate,
                         @RequestParam("startTime") String startTime,
                         @RequestParam("endTime") String endTime,
                         Model model) {

        var startLocalDate = parseLocalDate(startDate);
        var endLocalDate = parseLocalDate(endDate);
        int userId = SecurityUtil.authUserId();
        List<Meal> mealsBetweenDates = mealService.getBetweenInclusive(startLocalDate, endLocalDate, userId);
        var startLocalTime = parseLocalTime(startTime);
        var endLocalTime = parseLocalTime(endTime);
        log.info("getBetween dates({} - {}) time({} - {}) for user {}", startLocalDate, endLocalDate, startLocalTime, endLocalTime, userId);
        List<MealTo> mealList = MealsUtil.getFilteredTos(mealsBetweenDates, SecurityUtil.authUserCaloriesPerDay(), startLocalTime, endLocalTime);
        model.addAttribute("meals", mealList);
        return "meals";
    }
}
