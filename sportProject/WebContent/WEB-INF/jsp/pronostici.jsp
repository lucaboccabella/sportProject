<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:forEach var="risultati" items="${stats}">
	<c:forEach var="risultato" items="${risulati}">
		${risultato}
	</c:forEach>
	<br />
</c:forEach>