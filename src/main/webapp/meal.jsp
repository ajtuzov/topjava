<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <title>Title</title>
</head>
<body>
<form action="meals" method="post">
    <input type="hidden" name="id" value="<c:out value="${meal.id}"/>">
    <label>DateTime:<input type="datetime-local" name="date" value="<c:out value="${meal.dateTime}"/>"></label>
    <br>
    <label>Description:<input type="text" name="description" value="<c:out value="${meal.description}"/>"></label>
    <br>
    <label>Calories:<input type="number" name="calories" value="<c:out value="${meal.calories}"/>"></label>
    <br>
    <p>
        <button type="submit">Save</button>
        <a href="meals"><button type="button">Cancel</button></a>
    </p>
</form>
</body>
</html>
