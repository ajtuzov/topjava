package ru.javawebinar.topjava.web.meal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/ui/meals")
public class MealUIController extends AbstractMealController {

    @PostMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Meal create(
            @RequestParam LocalDateTime dateTime,
            @RequestParam String description,
            @RequestParam int calories) {
        return super.create(new Meal(dateTime, description, calories));
    }

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MealTo> getAll() {
        return super.getAll();
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }
}
