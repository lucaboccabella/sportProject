<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:forEach var="squadra" items="${stats}">
	Squadra: ${squadra.key} 
	Stats: <c:forEach var="stat" items="${squadra.value}">
				${stat}
		   </c:forEach>
</c:forEach>