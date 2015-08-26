<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Servlet Hello World</title>
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
			.error {
				color: #ff0000;
			}
		</style>
    </head>
    <body>
    
    <nav class="navbar navbar-default navbar-fixed-top" role="navigation">
	    <div class="navbar-brand" style="position:absolute;  padding-left: 50%; margin-left:-100px;text-align:center;">VirtualPowerPlants</div>
	</nav>
    	
	<div class="row" style="margin-top: 70px;margin-bottom: 70px;">
	    <div class="col-md-9"  id="content-main-page">
	        <!-- Start right settings panel -->
	        <div class="panel panel-default vpp-panel-login" id="registerPanel">
	            <div class="panel-heading" style="text-align:center;">
	                <h3 class="panel-title" >Register New User</h3>
	            </div>
	            <div class="panel-body">
	                <div class="container">
	                    <form id="registerData" class="form-horizontal" role="form" action="register" method='POST'>
	                        <div class="form-group">
	                            <label for="setUser" class="col-lg-3 control-label" style="text-align:left">Desired name:</label>
	                            <div class="col-lg-9">
	                                <input type="text" class="form-control" name='username' id="setUsername" placeholder="User">
	                                <span class="error">${messages.username}</span>
	                            </div>
	                        </div>
	                        <div class="form-group">
	                            <label for="setUser" class="col-lg-3 control-label" style="text-align:left">Desired password:</label>
	                            <div class="col-lg-9">
	                                <input id="setPassword" type="password" name='password' class="form-control"  placeholder="Password">
	                                <span class="error">${messages.password}</span>
	                            </div>
	                        </div>
	                        <div class="form-group">
	                            <label for="setUser" class="col-lg-3 control-label" style="text-align:left">Repeat password:</label>
	                            <div class="col-lg-9">
	                                <input id="setPassword2" type="password" name='password2' class="form-control"  placeholder="Password">
	                                <span class="error">${messages.password2}</span>
	                            </div>
	                        </div>
	                        <div class="form-group">
	                            <div class="col-lg-offset-3 col-lg-9" style="margin-top:5px">
	                                <button type="submit" class="btn btn-default" id="setBtnDone">Register</button>
	                                <button type="reset" class="btn btn-default" id="setBtnReset">Reset</button>
	                            </div>
	                        </div>
	               		</form>
	                    
	                </div>
	
	            </div>
	        </div>
	    </div>
	</div>
	
<!--         <form action="register" method="post"> -->
	        
<!--             <h3>Register New User</h3> -->
            
<!--             <table> -->
<!-- 				<tr> -->
<!-- 					<td>Introduce desired user name:</td> -->
<%-- 					<td><input id="username" name="username" value="${fn:escapeXml(param.username)}"> --%>
<%-- 					<span class="error">${messages.username}</span> --%>
<!-- 					</td> -->
<!-- 				</tr> -->
<!-- 				<tr> -->
<!-- 					<td>Introduce desired password:</td> -->
<%-- 					<td><input type="password" id="password" name="password" value="${fn:escapeXml(param.password)}"> --%>
<%-- 					<span class="error">${messages.password}</span> --%>
<!-- 					</td> -->
<!-- 				</tr> -->
<!-- 				<tr> -->
<!-- 					<td>Introduce again the desired password:</td> -->
<%-- 					<td><input type="password" id="password2" name="password2" value="${fn:escapeXml(param.password2)}"> --%>
<%-- 					<span class="error">${messages.password2}</span> --%>
<!-- 					</td> -->
<!-- 				</tr> -->
<!-- 				<tr> -->
<!-- 					<td colspan='2'><input type="submit" value="Register"> -->
<!-- 					</td> -->
<!-- 				</tr> -->
<!-- 			</table> -->
        </form>
    </body>
</html>