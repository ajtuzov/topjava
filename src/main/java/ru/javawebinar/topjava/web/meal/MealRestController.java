package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;

import java.util.List;

import static java.time.LocalTime.MAX;
import static java.time.LocalTime.MIN;
import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.MealsUtil.getFilteredTos;
import static ru.javawebinar.topjava.util.ValidationUtil.*;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    protected final Logger log = getLogger(getClass());

    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }


    public List<MealTo> getAll() {
        log.info("getAll");
        return getFilteredTos(service.getAll(authUserId()), authUserCaloriesPerDay(), MIN, MAX);
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(id, authUserId());
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        meal.setUserId(authUserId());
        checkNew(meal);
        return service.create(meal);
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(id, authUserId());
    }

    public void update(Meal meal, int id) {
        log.info("update {} with id={}", meal, id);
        checkNotFound(meal.getUserId() == authUserId(), "Forbidden");
        assureIdConsistent(meal, id);
        service.update(meal);
    }
}