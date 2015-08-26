<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
	<title>Login Page</title>
	<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <link rel="stylesheet" href="css/nv.d3.css"/>
    <link rel="stylesheet" href="css/bootstrap.min.css"/>
    <!--
    <link rel="stylesheet" href="css/bootstrap-theme.min.css"/>
    -->
    <link rel="stylesheet" href="css/datepicker.css"/>

    <link rel="stylesheet" href="css/main.css"/>
	<style>
		.errorblock {
			color: #ff0000;
			background-color: #ffEEEE;
			border: 1px solid #ff0000;
			padding: 8px;
			margin: 16px;
		}
		.success { 
			color: #008B45; 
			background-color: #EEfEEE;
			border: 1px solid #008B45;
			padding: 8px;
			margin: 16px;
		}
	</style>
</head>
<body onload='document.f.j_username.focus();'>

	<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
	    <div class="navbar-brand" style="position:absolute;  padding-left: 50%; margin-left:-100px;text-align:center;">VirtualPowerPlants</div>
	</nav>
	
	<div class="row" style="margin-top: 70px;margin-bottom: 70px;">
	    <div class="col-md-9"  id="content-main-page">
	        <!-- Start right settings panel -->
	        <div class="panel panel-default vpp-panel-login" id="loginPanel">
	            <div class="panel-heading" style="text-align:center;">
	                <h3 class="panel-title" >Login</h3>
	            </div>
	            
	            <c:if test="${not empty error}">
					<div class="errorblock">
						Your login attempt was not successful, try again.<br /> Caused :
						${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
					</div>
				</c:if>
				
				<c:if test="${not empty messages.success}">
					<div class="success">
						${messages.success}
					</div>
				</c:if>
				<form id="newUser" action="register" method="GET"></form>
	            <div class="panel-body">
	                <div class="container">
	                    <form id="loginData" class="form-horizontal" role="form" action=
	                    		"<c:url value='j_spring_security_check' />" method='POST'>
	                        <div class="form-group">
	                            <label for="setUser" class="col-lg-3 control-label">User:</label>
	                            <div class="col-lg-9">
	                                <input type="text" class="form-control" name='j_username' id="setUsername" placeholder="User">
	                            </div>
	                        </div>
	                        <div class="form-group">
	                            <label for="setUser" class="col-lg-3 control-label">Password:</label>
	                            <div class="col-lg-9">
	                                <input id="setPassword" type="password" name='j_password' class="form-control"  placeholder="Password">
	                            </div>
	                        </div>
	                        <div class="form-group">
	                            <div class="col-lg-offset-3 col-lg-9" style="margin-top:5px">
	                                <button type="submit" form="loginData" class="btn btn-default" id="setBtnDone">Login</button>
	                                <button type="reset" form="loginData" class="btn btn-default" id="setBtnReset">Reset</button>
	                                <button type="submit" form="newUser" class="btn btn-default" style="margin-left:25%" id="setBtnNewUser">New User</button>
	                            </div>
	                        </div>
	               		</form>
	                    
	                </div>
	
	            </div>
	        </div>
	    </div>
	</div>
</body>
</html>