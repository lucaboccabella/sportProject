<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html>
<head>
<title>Home Page</title>
<link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
<style type="text/css">@import url("<c:url value="/css/main.css"/>");</style>
</head>
<body>
<div class="fluid-container">
	<h4>League</h4>
    <div class="col col-md-2" id="classifica">
        <h2 class="lead">Stagione: ${stagioneAttuale}</h2>
    	<table class="table table-bordered">
    		<tr>
    			<th>Nome</th>
    			<th>Punti</th>
    		</tr>
    		<c:forEach var="squadra" items="${classifica}">
    			<tr class="${squadra.situazionePunti}">
    				<td>${squadra.nome}</td>
    				<td>${squadra.punti}</td>
    			</tr>
    		</c:forEach>
		</table>
	</div>
</div>
</body>
</html>