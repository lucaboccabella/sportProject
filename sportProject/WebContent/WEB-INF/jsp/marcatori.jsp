<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE HTML>
<html>
<%@include file="head.jsp"%>
<body>
	<%@include file="header.jsp"%>
	<div id="content" class="container notBarre">
		<div class="col col-md-3" id="classifica">
			<table class="table table-bordered">
				<tr>
					<th>Nome</th>
					<th>Squadra</th>
					<th>Reti</th>
					<th>Rigori</th>
				</tr>
				<c:forEach var="marcatore" items="${marcatori}">
					<tr>
						<td>${marcatore.nomeMarcatore}</td>
						<td>${marcatore.nomeSquadra}</td>
						<td class="text-center">${marcatore.goal}</td>
						<td class="text-center">${marcatore.rigori}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</body>
</html>