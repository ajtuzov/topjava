package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Predicate;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.DateTimeUtil.isBetweenHalfOpen;
import static ru.javawebinar.topjava.util.MealsUtil.getFilteredTos;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
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
        return getFilteredTos(service.getAll(authUserId()), authUserCaloriesPerDay(), LocalTime.MIN, LocalTime.MAX);
    }

    public List<MealTo> getFiltered(String startDateParam, String endDateParam, String startTimeParam, String endTimeParam) {
        log.info("get by filter");

        LocalDate startDate = getStartDate(startDateParam);
        LocalDate endDate = getEndDate(endDateParam);
        Predicate<Meal> filterByDate = meal -> isBetweenHalfOpen(meal.getDate(), startDate, endDate);

        LocalTime startTime = getTime(startTimeParam, LocalTime.MIN);
        LocalTime endTime = getTime(endTimeParam, LocalTime.MAX);
        Predicate<Meal> filterByTime = meal -> isBetweenHalfOpen(meal.getTime(), startTime, endTime);

        List<Meal> resultList = service.getFiltered(authUserId(), filterByDate.and(filterByTime));

        return getFilteredTos(resultList, authUserCaloriesPerDay(), LocalTime.MIN, LocalTime.MAX);
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(id, authUserId());
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
        return service.create(meal, authUserId());
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(id, authUserId());
    }

    public void update(Meal meal, int id) {
        log.info("update {} with id={}", meal, id);
        assureIdConsistent(meal, id);
        service.update(meal, authUserId());
    }

    private LocalTime getTime(String time, LocalTime defaultValue) {
        return time.isEmpty() ? defaultValue : LocalTime.parse(time);
    }

    private LocalDate getStartDate(String date) {
        return date.isEmpty() ? LocalDate.MIN : LocalDate.parse(date);
    }

    private LocalDate getEndDate(String date) {
        return date.isEmpty() ? LocalDate.MAX : LocalDate.parse(date).plusDays(1);
    }
}