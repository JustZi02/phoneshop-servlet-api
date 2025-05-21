<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Advanced Search">
    <h1>
        Advanced Search
    </h1>
    <form>
        <p>
            Description:
            <label>
                <input name="description">
            </label>
            <select name="searchType">
                <option>All words</option>
                <option>Any word</option>
            </select>
        </p>
        <p>
            Min Price:
            <label>
                <input name="minPrice">
            </label>
        </p>
        <p>
            Max Price:
            <label>
                <input name="maxPrice">
            </label>
        </p>
        <button>Search</button>
    </form>
        <table>

        </table>

</tags:master>

