<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<head>
<title>Pronostici</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
	<div class="container-fluid">
		<div class="col col-md-5">
			<table class="table table-striped table-bordered">
				<tr>
					<th>Squadra</th>
					<th>Risultato Fisso</th>
					<th class="text-center">1</th>
					<th class="text-center">X</th>
					<th class="text-center">2</th>
					<th>Over 2,5</th>
					<th>Under 2,5</th>
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
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</body>