<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="javatime" uri="http://sargue.net/jsptags/time" %>
<html lang="ru">
<head>
    <title>Meals</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>

<h4><a href="meals?action=add">Add meal</a></h4>

<table>
    <th>Date</th>
    <th>Description</th>
    <th>Calories</th>
    <th colspan="2">Action</th>
    <c:forEach var="meals" items="${mealTo}">
        <jsp:useBean id="meals" scope="page" type="ru.javawebinar.topjava.model.MealTo"/>
        <tr style="color: ${meals.excess ? 'red' : 'green'}">
            <td><javatime:format value="${meals.dateTime}" pattern="yyyy-MM-dd HH:mm"/></td>
            <td><c:out value="${meals.description}"/></td>
            <td><c:out value="${meals.calories}"/></td>
            <td><a href="meals?action=edit&id=<c:out value="${meals.id}"/>">Update</a></td>
            <td><a href="meals?action=delete&id=<c:out value="${meals.id}"/>">Delete</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
