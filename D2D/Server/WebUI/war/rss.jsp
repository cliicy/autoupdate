<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@   page   import="com.ca.arcflash.ui.server.properties.JSPMessage"%> 

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="index.css" />
<style>
  .news_title {
 	margin: 0px 0px 0px 5px;
 	text-align: left;
 	font-family: Verdana;
 	font-size: 8pt;
 	font-weight: BOLD;  
  	color: #000000;
  	cursor: pointer;
	white-space: nowrap;   
	overflow: hidden;   
	text-overflow: ellipsis;   
	-o-text-overflow: ellipsis;   
	-moz-binding: url('./ellipsis.xml#ellipsis');   
 }
 
 .news_description {
 	text-align: left;
 	font-family: Verdana;
 	font-size: 8pt;  
  	color: #11628A;
  	cursor: pointer;
  }
  
  .news_image {
  	cursor: pointer;
  }
</style>
<%
	String language = request.getParameter("language");
	String country = request.getParameter("country");
	if (language == null || language.isEmpty()) {
		language = "en";
	}
	if (country == null || country.isEmpty()) {
		country = "";
	}
  	String rssURL = JSPMessage.getRssURLResource(language,country);
%>
<script src="http://www.google.com/jsapi/?key=internal-sample"
	type="text/javascript"></script>
<script src="rss.js" type="text/javascript"></script>
<script type="text/javascript">
<!--

function addOverflowEllipsis( containerElement, maxWidth ) 
{   
	  alert(containerElement.innerHTML);
	var contents = containerElement.innerHTML;     
      var pixelWidth = containerElement.offsetWidth;     
      if(pixelWidth > maxWidth)     
       {         contents = contents + ""; // ellipsis character, not "..." but ""    
         }     
      while(pixelWidth > maxWidth)    
          {         
          contents = contents.substring(0,(contents.length - 2)) + "";
          containerElement.innerHTML = contents;         pixelWidth = containerElement.offsetWidth;     
          } 
      } 
    function showGadget() {
        try
        {
		    var options = {
				title_container : 'feed_title',
				desc_container : 'feed_desc',
				title_imageContainer : 'feed_image',
				whole_container : 'whole_div',
				title_imageHtml :'<img class="news_image" src="images/feed-icon-28x28.png" />'
			};
      		 new D2DFeedsControl('<%=rssURL%>', options);
	    }
	    catch(e)
	    {
			try{
	    		var title_div = document.getElementById('feed_title');
	    		title_div.innerHTML = "";
			}catch(e1){
			}
	
	    }
    }
    try
    {
	    google.load("feeds", "1", {nocss:1});
	    google.setOnLoadCallback(showGadget);
    }
    catch(e)
    {
		try{
    		var title_div = document.getElementById('feed_title');
    		title_div.innerHTML = "";
		}catch(e1){
		}

    }
    // rss_href is global defined in rss.js
    function open_rss(){
        if(rss_href!="")
	   		window.open(rss_href, "_blank_", "toolbar=yes,menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes" );
   		return true; 
    }
    function open_rss_source(){
        window.open("<%=rssURL%>", "_blank_", "menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes" );
        return true;
    }
  //the end for script hidding -->
  </script>
</head>

<body TOPMARGIN="0px" LEFTMARGIN="0px" MARGINHEIGHT="0px" MARGINWIDTH="0px" bgcolor="#ebeff2"  width="100%">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tbody>
		<tr width="100%">
			<td width="100%" id="<%=language+"_"+country%>" align="center" valign="top">
			<div id="whole_div" style="padding: 0px; width: 100%; opacity: 0.993844; " >
			<table width="100%" border="0" cellspacing="0" cellpadding="1" style="table-layout:fixed;">
				<tbody>
					<tr>
						<td class="x-table-layout-cell" width="16px" align="left" id="feed_image" onclick="open_rss_source();" class="rss">
						</td>
						<td width="30%" id="feed_title" class="news_title" align="left"  onclick="open_rss();">
						</td>
						<td width="69%" class="news_description"  id="feed_desc" style="text-overflow:ellipsis;overflow:hidden;white-space:nowrap;" onclick="open_rss();">
						</td>
					</tr>
				</tbody>
			</table>
			</div>
			</td>
		</tr>
	</tbody>
</table>
</body>
</html>