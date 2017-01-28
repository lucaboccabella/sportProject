<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE HTML>
<html>
<head>
<title>Home Page</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<style>
	.stemmi{
		width: 30px;
		margin-right: 2%;
		margin-left: 2%;
	}
	#footer{
		background-color: #333;
		color: #fff;
	}
	#copyright{
		padding: 1%;
    	margin-bottom: 0;
	}
</style>
</head>
<body>
	<nav class="navbar navbar-inverse navbar-static-top">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target="#myNavbar">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">Nome sito</a>
			</div>
			<div class="collapse navbar-collapse" id="myNavbar">
				<ul class="nav navbar-nav">
					<li class="active"><a href="#">Home</a></li>
					<li class="dropdown"><a class="dropdown-toggle"
						data-toggle="dropdown" href="#">Page 1 <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="#">Page 1-1</a></li>
							<li><a href="#">Page 1-2</a></li>
							<li><a href="#">Page 1-3</a></li>
						</ul></li>
					<li><a href="pronostici">Pronostici</a></li>
					<li><a href="#">Page 3</a></li>
				</ul>
			</div>
		</div>
	</nav>
	<div class="container-fluid">
		<div class="col col-md-2" id="classifica">
			<table class="table table-bordered">
				<tr>
					<th>Nome</th>
					<th class="text-center">Punti</th>
				</tr>
				<c:forEach var="squadra" items="${classifica}">
					<tr class="${squadra.situazionePunti}">
						<td>${squadra.nome}</td>
						<td class="text-center">${squadra.punti}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div class="col col-md-7">
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
	<div id="footer">
		<h4 id="copyright">Copyright 2017</h4>
	</div>
</body>
</html>