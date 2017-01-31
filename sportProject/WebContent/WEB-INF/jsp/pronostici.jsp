<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<%@include file="head.jsp"%>
<body>
	<%@include file="header.jsp" %>
	<div class="container-fluid">
		<div class="col col-md-2">
			<img
				src="https://placeholdit.imgix.net/~text?txtsize=38&txt=220%C3%971800&w=220&h=1800" />
		</div>
		<div class="col col-md-8">
			<h2 class="title">I Nostri Pronostici</h2>
			<br /> <img
				src="https://placeholdit.imgix.net/~text?txtsize=38&txt=940%C3%97200&w=940&h=200" />
			<br /> <br />
			<div class="col col-md-12 col-xs-12 col-sm-12">
				<table class="table table-striped table-bordered">
					<tr>
						<th>Prossima giornata</th>
						<th>Risultato Fisso</th>
						<th class="text-center">1</th>
						<th class="text-center">X</th>
						<th class="text-center">2</th>
						<th>Over 2,5</th>
						<th>Under 2,5</th>
						<th>Goal</th>
						<th>NoGoal</th>
					</tr>
					<c:forEach var="risultati" items="${stats}">
						<tr>
							<td>${risultati.key}</td>
							<td class="text-center">${risultati.value.risultatoProbabile}</td>
							<td>${risultati.value.uno}%</td>
							<td>${risultati.value.pareggio}%</td>
							<td>${risultati.value.due}%</td>
							<td>${risultati.value.over}%</td>
							<td>${risultati.value.under}%</td>
							<td>${risultati.value.goal}%</td>
							<td>${risultati.value.nogoal}%</td>
						</tr>
					</c:forEach>
				</table>
			</div>
			<br />
			<h4>Lorem ipsum dolor sit amet, consectetur adipiscing elit.
				Vivamus gravida est ut velit faucibus, in molestie orci ornare.
				Vestibulum hendrerit eros varius sem facilisis rhoncus non eget mi.</h4>
			<table class="table table-striped table-bordered">
				<tr>
					<th>Stato di forma</th>
					<th>Punteggio statistico</th>
				</tr>
				<c:forEach var="squadra" items="${classificaPrevista}">
					<tr class="${squadra.value.stato}">
						<td>${squadra.key}</td>
						<td class="text-center">${squadra.value.media}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div class="col col-md-2">
			<img
				src="https://placeholdit.imgix.net/~text?txtsize=38&txt=240%C3%971800&w=240&h=1800" />
		</div>
	</div>
	<%@include file="footer.jsp" %>
</body>
</html>