<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE HTML>
<html>
<%@include file="head.jsp" %>
<body>
	<%@include file="header.jsp" %>
	<div id="content" class="container notBarre">
		<div class="col col-md-3" id="classifica">
			<table class="table table-bordered">
				<tr>
					<th>Nome</th>
					<th class="text-center">Punti</th>
				</tr>
				<c:forEach var="squadra" items="${classifica}">
					<tr class="${squadra.situazionePunti}">
						<td><img class="stemmi" src="http://soccer.sportsopendata.net/logos/italy/<c:url value="${fn:toLowerCase(squadra.nome)}"/>.png"/>${squadra.nome}</td>
						<td class="text-center">${squadra.punti}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div class="col col-md-6">
			<h1 class="lead text-center">Ultima giornata</h1>
			<table class="table table-bordered col col-md-12">
				<c:forEach var="risultato" items="${risultati}">
					<tr class="active">
						<td><img class="stemmi" src="http://soccer.sportsopendata.net/logos/italy/<c:url value="${fn:toLowerCase(risultato.home)}"/>.png"/>${risultato.home}</td>
						<td class="text-center">${risultato.homeres}</td>
						<td class="text-center">${risultato.awayres}</td>
						<td class="text-right">${risultato.away}<img class="stemmi" src="http://soccer.sportsopendata.net/logos/italy/<c:url value="${fn:toLowerCase(risultato.away)}"/>.png"/></td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div class="col col-md-3" id="news">
			<!-- start feedwind code -->
			<script type="text/javascript"
				src="https://feed.mikle.com/js/fw-loader.js" data-fw-param="7582/"></script>
			<!-- end feedwind code -->
		</div>
	</div>
	<%@include file="footer.jsp" %>
</body>
</html>