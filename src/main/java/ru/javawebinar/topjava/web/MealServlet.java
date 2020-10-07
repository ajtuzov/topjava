package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealInMemoryStorage;
import ru.javawebinar.topjava.dao.Storage;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.MealsUtil.filteredByStreams;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private Storage<Meal> storage;

    @Override
    public void init() {
        storage = MealInMemoryStorage.newInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = Optional.ofNullable(request.getParameter("action"))
                .map(String::trim)
                .map(String::toLowerCase)
                .orElse("");

        switch (action) {
            case "add":
                request.getRequestDispatcher("/meal.jsp").forward(request, response);
                break;
            case "edit": {
                int id = parseId(request);
                Meal meal = storage.getById(id);
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/meal.jsp").forward(request, response);
                break;
            }
            case "delete": {
                int id = parseId(request);
                storage.delete(id);
                response.sendRedirect("meals");
                break;
            }
            default:
                List<MealTo> mealTo = filteredByStreams(storage.getAll(), LocalTime.MIN, LocalTime.MAX, 2000);
                request.setAttribute("mealTo", mealTo);
                log.debug("redirect to meals");
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String date = request.getParameter("date");
        LocalDateTime dateTime = LocalDateTime.parse(date);
        String description = request.getParameter("description");
        String cal = request.getParameter("calories");
        int calories = Integer.parseInt(cal);
        String id = request.getParameter("id");
        int userId = id == null || id.isEmpty() ? MealInMemoryStorage.getNextId() : Integer.parseInt(id);
        Meal meal = new Meal(userId, dateTime, description, calories);
        storage.put(meal);
        response.sendRedirect("meals");
    }

    private int parseId(final HttpServletRequest request) {
        return Integer.parseInt(request.getParameter("id"));
    }
}
