<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:forEach var="squadra" items="${stats}">
	Squadra: ${squadra.key} 
	Stats:${squadra.value.media}
</c:forEach>