var browser = "";
var browsername = navigator.appName;
var browserversion = parseInt(navigator.appVersion);
if (browsername == "Netscape") 
{
	browser = "ns" + browserversion;
}
else
{
	if (browsername == "Microsoft Internet Explorer") 
	{
		if (browserversion >= 4) 
		{
			browser = "ie" + browserversion;
		}
		else 
		{
			browser = "ie3";
		}
	}
}
bName = navigator.appName;
bVer = parseInt(navigator.appVersion);

function Popup()
{
	if (bName == "Microsoft Internet Explorer")
	{ 
		window.open('http://cnn.com/ads/advertiser/pagenet/9908/pop.html','Ad','toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=0,resizable=0,width=253,height=253');}else{window.open('http://cnn.com/ads/advertiser/pagenet/9908/pop.html','Ad','toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=0,resizable=0,width=267,height=267');
	}
}
// this handles the homepage picture of the day
function popNav(url,name,features) 
{
	if ((browser == "ns3","ns4") || (browser == "ie4")) 
	{
		popBox = window.open(url,name,features);
		popBox.focus();
	}
	else
	{
		if (browser == "ie3") 
		{
			popBox = window.open(url,name,features);
		}  
	}
}

// this function is used to redirect the search on the main page
// to looksmart if its an internet search  -- SG
function validate( tform ) {
	var site;
	
	if ( tform.sites.options ) {		//	"sites" should be a select
		site = tform.sites.options[tform.sites.selectedIndex].value;
	} else {
	if (tform.sites) {site =tform.sites.value;}
	else {return true;}						//	error, but don't show the user
	}
	
	switch ( site.toLowerCase() ) {
		case "internet":
			tform.action = "http://cnn.looksmart.com/r_search"
			tform.key.value = tform.qt.value;
			tform.isp.value = 'zcb';
			return true;
		
		case "cnnsi":
			tform.action = "http://search.cnnsi.com/query.html";
			tform.qp.value = "url:http://www.cnnsi.com/";
			tform.col.value = 'cnnsi';
			return true;

		case "cnnfn":
			tform.action = "http://search.cnnfn.com/query.html";
			tform.col.value = 'cnnfn';
			return true;
		
		case "cnn":
			tform.action = "http://search.cnn.com:80/query.html";
			tform.col.value = 'cnni';
			return true;
		
		
		case "cnnfyi":
			tform.action = "http://search.cnn.com:80/query.html";
			tform.col.value = 'cnni';
			tform.qp.value = 'url:/fyi/';
			//tform.rq.value = '2';
			return true;
		
		case "cnnlaw":
			tform.action = "http://search.cnn.com:80/query.html";
			tform.col.value = 'cnni';
			tform.qp.value = 'url:/LAW/';
			//tform.rq.value = '2';
			return true;
		
		case "time":
			tform.keyword.value = tform.qt.value;
			tform.action = "http://www.pathfinder.com/time/daily/searchresults/1,2645,,00.html";
			tform.col.value = 'time';
			//tform.rq.value = '2';
			return true;
			
		case "cnneurope":
			tform.action = "http://cnn.looksmart.com/r_search"
			tform.key.value = tform.qt.value;
			tform.isp.value = 'zcn';
			return true;

		case "cnneuropeweb":
			tform.action = "http://cnn.looksmart.com/r_search"
			tform.key.value = tform.qt.value;
			tform.isp.value = 'zcp';
			return true;

		case "cnneuropeir":
			tform.action = "http://cnn.looksmart.com/r_search"
			tform.key.value = tform.qt.value;
			tform.isp.value = 'zcu';
			return true;

		case "cnneuropenl":
			tform.action = "http://cnn.looksmart.com/r_search"
			tform.key.value = tform.qt.value;
			tform.isp.value = 'zcw';
			return true;

		case "cnneuropeit":
			tform.action = "http://cnn.looksmart.com/r_search"
			tform.key.value = tform.qt.value;
			tform.isp.value = 'zcv';
			return true;

		case "cnneuropeswz":
			tform.action = "http://cnn.looksmart.com/r_search"
			tform.key.value = tform.qt.value;
			tform.isp.value = 'zda';
			return true;

		case "cnneuropeuk":
			tform.action = "http://cnn.looksmart.com/r_search"
			tform.key.value = tform.qt.value;
			tform.isp.value = 'zdb';
			return true;

		
		default:
			return true;						//	unsupported site
	}
}



// this will open a new window, submit the poll form, and send the results to the popup window
function pollSubPop (earl, name, widgets, specialsURL) 
{
	host = location.hostname;
	if (host.indexOf('customnews') != -1) 
	{
		var url = 'http://customnews.cnn.com' + earl;
	}
	else
	{
		var url = earl;
	}
	popupWin = window.open(url, name, widgets);
	popupWin.opener.top.name = "opener";
	popupWin.focus();
}

// this is for opening pop-up windows
function openWindow (earl,name,widgets) 
{
	host = location.hostname;
	if (host.indexOf('customnews') != -1) 
	{
		var url = 'http://customnews.cnn.com' + earl;
	}
	else
	{
		var url = earl;
	}
	popupWin = window.open (url,name,widgets);
	popupWin.opener.top.name="opener";
	popupWin.focus();
}

// sk
// This allows you to redirect the main browser window to a new URL when launching a popup
function jumpLink( earl, name, widgets, specialsURL ) 
{
	host = location.hostname;
	if ( host.indexOf( 'customnews' ) != -1 ) 
	{
		var url = 'http://customnews.cnn.com' + earl;
	}
	else
	{
		var url = earl;
	}
	popupWin = window.open( url, name, widgets );
	if (specialsURL)
	{
		popupWin.opener.location = specialsURL;
	}
	
	
	popupWin.opener.top.name = "opener";
	popupWin.focus();
}

function closeWindow () 
{
	parent.close ();
}

function goTW()
{
	var URL = document.pathfinder.site.options[document.pathfinder.site.selectedIndex].value;
	window.location.href = URL;
}
// tg	

function email()
{
	address=document.emailform.emailinput.value;
	location='http://cnn.com/EMAIL/index.html?'+address;
}
function splitWindow(s) 
{
	window.name="_mainWindow";
	var w=127;h=153;
	var v=navigator.appVersion.substring(0,1);
	if (navigator.appName=="Netscape")
	{
		if ((v==3)||(v==4)) {w=127,h=168;} 
		else {w=132,h=179;}
	} 
	else {w=112,h=137;}
	if (!s) s="*itn/ord";
	else if (s.indexOf("*,")!=-1) 
	s = s.substring(0,s.indexOf("*"))+"*itn/ord"+s.substring(s.indexOf(","));
	var f="http://cnn.com/event.ng/Type=click&RunID=17006&ProfileID=749&AdID=11567&GroupID=313&FamilyID=2433&TagValues=4.8.435.487.1098&Redirect=http:%2F%2Fwww.itn.net%2Fcgi%2Fget%3Fjava%2FFlightTicker%2FsplitWindow.html&Stamp="+s;
	var win=window.open(f,"ticker","status=0,scrollbars=0,resizable=0,width="+w+",height="+h);
}

///////////////////////////////////////////////////////////////////////////////////
function vod (url,streamtitle,customfeatures) 
{
	windowFeatures = 'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,width=210,height=480';

	//Browser Detection
	var browser = "";
	var browsername = navigator.appName;
	var browserversion = parseInt(navigator.appVersion);
	var browserplatform = navigator.userAgent;
	if (browsername == "Netscape") 
	{
		browser = "ns" + browserversion;
	}
	else
	{
		if (browsername == "Microsoft Internet Explorer") 
		{
			if (browserversion >= 4) 
			{
				browser = "ie" + browserversion;
			}
			else
			{
				browser = "ie3";
			}
		}
	}

	if ((customfeatures) && customfeatures != '') 
	{
		windowFeatures = customfeatures;
	}

	if (url.indexOf(".rm",(url.length-10)) != -1) 
	{
		if (browser != 'ie3') 
		{
			hasplugin = 'false';
			if ( (browserplatform.indexOf('Mac') !=-1) && ( browsername != "Netscape") )  
			{
				numPlugins = 0;
				for (i = 0; i < numPlugins; i++) 
				{
					plugin = navigator.embeds[i];
					if (plugin.name.substring(0,10)=="RealPlayer") 
					{
						hasplugin = 'true';
					}
				}
			}
			else
			{
				numPlugins = navigator.plugins.length;
				for (i = 0; i < numPlugins; i++) 
				{
					plugin = navigator.plugins[i];
					if (plugin.name.substring(0,10)=="RealPlayer") 
					{
						hasplugin = 'true';
					}
				}
			}
			if (browser.substring(0,2) == 'ie') 
			{
				hasplugin = 'true';
			}
			if (hasplugin == 'true') 
			{
				videoWin = window.open (url , 'video', windowFeatures);
				if (streamtitle != '') 
				{
					videoWin.streamtitle=streamtitle;
				}
				videoWin.document.close();		
			}
			else
			{
				stream = url.substring(0,(url.length-5));
				location.href=stream + '.ram';
			}
		}
		else
		{
			videoWin = window.open (url , 'video', windowFeatures);
			if (document.images) 
			{
				if (streamtitle != '') 
				{
					videoWin.streamtitle=streamtitle;
				}
			}
			videoWin.document.close();			
		}		
	}
	else
	{
		videoWin = window.open (url , 'video', windowFeatures);
		if (document.images) 
		{
			if (streamtitle != '') 
			{
				videoWin.streamtitle=streamtitle;
			}
		}
		videoWin.document.close();
	}
}

///////////////////////////////////////////////////////////////////////////////////
// tg	
function livevideo (url,streamtitle,customfeatures)
{
windowFeatures = 'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,width=215,height=500';


var browser = "";
var browsername = navigator.appName;
var browserversion = parseInt(navigator.appVersion);
if (browsername == "Netscape") {
    browser = "ns" + browserversion;
} else {
    if (browsername == "Microsoft Internet Explorer") {
        if (browserversion >= 4) {
            browser = "ie" + browserversion;
        } else {
            browser = "ie3";
        }
    }
}

if (url.indexOf("real") != -1) {
	if (browser != 'ie3') {
		hasplugin = 'false';
		numPlugins = navigator.plugins.length;
		for (i = 0; i < numPlugins; i++) {
			plugin = navigator.plugins[i];
			if (plugin.name.substring(0,10)=="RealPlayer") {
				hasplugin = 'true';
			}
		}
		if (browser.substring(0,2) == 'ie') {
			hasplugin = 'true';
		}
		if (hasplugin == 'true') {
			videoWin = window.open (url , 'video', windowFeatures);
			if (streamtitle != '') {
   		 		videoWin.streamtitle=streamtitle;
   		 	}
   		 	videoWin.document.close();		
		} else {
		stream = url.charAt((url.length-6))
		location.href='/video/live/live' + stream + '.rm28.ram';
		}
	} else {
		videoWin = window.open (url , 'video', windowFeatures);
		if (document.images) {
			if (streamtitle != '') {
   				videoWin.streamtitle=streamtitle;
   			 }
   		}
   		videoWin.document.close();			
	}		
} else {
	videoWin = window.open (url , 'video', windowFeatures);
	if (document.images) {
		if (streamtitle != '') {
	    	videoWin.streamtitle=streamtitle;
	    }
	}
    videoWin.document.close();
}
}

//this is for the dropdowns on the pages like TRAVEL


function napVector (vectorChoice) {
        location.href = document.nap.vector.options[document.nap.vector.selectedIndex].value;
        }
function ipVector (vectorChoice) {
        location.href = document.ip.vector.options[document.ip.vector.selectedIndex].value;
        }

///////////////////////////////////////////////////////////////////////////////////
// The pulldown on the Euro Edition Nav bars and the Showbiz main page for Horoscope/Comics/Games

function makeSelOpt (obj) {
	if (obj.length > 1) {
		var last = 2;
 		if (document.all){
 			var last = obj.length;
 			for (var count = 2; count < last; count++) {
 			obj.remove(2)
 			}	
 		}
	} else {
		var last = obj.length;
	}
	for (var count = 0; count < URLs.length; count++) {
		if (navigator.appName == "Netscape") {
			obj[last + count] = new Option(NAMEs[count],URLs[count],false);
		} else {
			var newElem 	= document.createElement("OPTION");
			newElem.text 	= NAMEs[count];
			if (URLs[count] != "") {
				newElem.value = URLs[count];
				if (navigator.platform != "MacPPC") {newElem.style.backgroundColor = "#ffffff";}
			} else if (navigator.platform != "MacPPC") {
				 newElem.style.backgroundColor = "#ffffff";
			}
			obj.add(newElem);
		}
	}
return;
}

//this is for the edition cookie popup, Jamie Randell author

EditionCookieName = 'EditionPopUp';
EditionCookieExpire = 7;  
EditionValue = 'seen';
EditionLimit = 1;
EditionURL = '/virtual/editions/europe/2000/roof/change.pop/frameset.exclude.html';  // URL for editions popup HTML goes here!
EdPopHeight = 400;     // Height for popup window goes here
EdPopWidth = 510;      // Width of popup window goes here

EditionDomain = ".cnn.com";

with (navigator) {
	var aN = appName;
	uA = userAgent;
	aV = parseInt(appVersion);
}
ie = uA.indexOf("MSIE") > 0 || aN.indexOf("Microsoft") == 0;
mac = uA.indexOf("Mac") > 0;

if (!mac && ie && aV<4) {
	var msieStart = navigator.appVersion.indexOf("MSIE");
	var msieVer = navigator.appVersion.substring(msieStart+5,navigator.appVersion.length);
	aV = parseInt(msieVer);
}

function setCNNedCookieDate() {
	var cookieDate = new Date();
	var year;
	var month;
	var day;
		month = cookieDate.getMonth();
		year = cookieDate.getYear();
		day = cookieDate.getDate();
		if (year<2000) year += 1900;
	day += EditionCookieExpire;
	if (day > 28) {
		day = 1;
		month++;
		if (month > 11) {
			month = 0;
			year++;
		}
	}
	cookieDate.setYear(year);
	cookieDate.setMonth(month);
	cookieDate.setDate(day);

	var CNNcookieDate = cookieDate.toGMTString();
	return CNNcookieDate;
}

function CNNedCookie() {
	this.sh = 0;
	this.id = 0;
}

function decodeCNNedCookie(cookieValue) {
	var searchString = EditionValue + "(";
	var start = cookieValue.indexOf(searchString);
	var cnnCookie = new CNNedCookie();
	if (start == -1)
		cnnCookie.restString = (cookieValue);
	else {
		var extractStart = start + searchString.length;
		var end = cookieValue.indexOf(")",extractStart);
		if (end == -1)
			end = cookieValue.length;
		var cookieExtract = cookieValue.substring(extractStart,end);
		cnnCookie.restString = cookieValue.substring(0,start);
		cnnCookie.restString += cookieValue.substring(end+1, cookieValue.length);

		start = 0;
		while (start < cookieExtract.length) {
			 end = cookieExtract.indexOf(':',start);
			 if (end == -1) break;
			 var attributeName = cookieExtract.substring(start,end);
			 start = end+1;
			 end = cookieExtract.indexOf('&',start);
			 if (end == -1) end = cookieExtract.length;
			 var attributeVal = unescape(cookieExtract.substring(start,end));
			 start = end + 1;
			 cnnCookie[attributeName] = attributeVal;
		} 
	}
	return cnnCookie;
}

function getCNNedCookie() {
	var cnnCookie = null;
	if (document.cookie.length > 0) {
		var search = EditionCookieName + "=";
		var offset = document.cookie.indexOf(search);
		if (offset != -1) {
			offset += search.length;
			end = document.cookie.indexOf(";", offset);
			if (end == -1) end = document.cookie.length;
			cnnCookie = decodeCNNedCookie(document.cookie.substring(offset, end));			 
		}
	}
	return cnnCookie;
}

function storeCNNedCookie(cnnCookie) {
	if (cnnCookie == null) return;
	var cookieVal = "";
	for (var prop in cnnCookie) {
		if (prop != "restString") {
			if (cookieVal != "") cookieVal += '&';
			cookieVal += prop + ':' + escape(cnnCookie[prop]);
		}
	}

	var cookieString = EditionCookieName + "=";
	cookieString += EditionValue+"("+cookieVal+")";
	if (cnnCookie.restString != null) cookieString += cnnCookie.restString;
	cookieString += '; expires=' + setCNNedCookieDate();
	if (EditionDomain != "") cookieString += '; domain=' + EditionDomain;
	cookieString += '; path=/';
	document.cookie = cookieString;
}

function setCNNedCookie() {
	var newcnnCookie = new CNNedCookie();
	storeCNNedCookie(newcnnCookie);
}

// PopExclude works with the Greenfield surveys, prevents 2 popups on same page

function PopExclude() {
	var PopDate = new Date();
	var time = PopDate.getTime();
	time += 600000;
	PopDate.setTime(time);
	document.cookie = 'PopX=set; path=/; expires=' + PopDate.toGMTString() + '; domain=' + EditionDomain;
	return;
}

function getEdMinFromStamp(stamp) {
	var year = stamp.substring(0,4);
	var dotpos = stamp.indexOf(".",5);
	var month = stamp.substring(5,dotpos);
	var dotpos2 = stamp.indexOf(".",dotpos+1);
	var day = stamp.substring(dotpos+1,dotpos2);
	dotpos = stamp.indexOf(".",dotpos2+1);
	var hour = stamp.substring(dotpos2+1,dotpos);
	dotpos2 = stamp.indexOf(".",dotpos+1);
	var minute = stamp.substring(dotpos+1,dotpos2);
	var stampDate = new Date(year, parseInt(month)-1, day, hour, minute, 0);
	var minutes = stampDate.getTime()/60000;
	return minutes;
}

function validateCNNedCookie(cnnCookie) {
	return cnnCookie.sh < EditionLimit  && document.cookie.indexOf('CNNid') > -1 && document.cookie.indexOf('SelectedEdition') < 0;
}

function updateCNNedCookie(cnnCookie) {
	cnnCookie.sh++;
	storeCNNedCookie(cnnCookie);
}

function cnnEditionPopUp() {
	if (aV >= 3) {
		var cnnCookie = getCNNedCookie();
		if (cnnCookie == null) {
			setCNNedCookie();
			cnnCookie = getCNNedCookie();
			if (cnnCookie == null) return; 
		}
		if (cnnCookie != null && validateCNNedCookie(cnnCookie)) {
			PopExclude();
			open(EditionURL,"AdInterstitial", "height="+EdPopHeight+",width="+EdPopWidth);
			updateCNNedCookie(cnnCookie);
		}
	}
}

if (ie && aV<4  ||  !ie && mac && aV<4) {
	cnnEditionPopUp();
}
if (aV>2 || (!ie && mac && aV >= 4)) {
//window.onload = cnnEditionPopUp;
	cnnEditionPopUp();
}

// _________________________________________________________________________
// The following code was added to launch the 'change editions' popup.
// It checks first if it's able to set a cookie before launching the window.
// M.E. - 2000.09.12
function launchEditionPopup() {
	var WM_acceptsCookies = false;
	if ( document.cookie == '' ) {
		document.cookie = 'WM_acceptsCookies=yes'; // Try to set a cookie.
	    if ( document.cookie.indexOf( 'WM_acceptsCookies=yes' ) != -1 ) {
			WM_acceptsCookies = true;
	    } // If it succeeds, set variable
	} else { // there was already a cookie
	  WM_acceptsCookies = true;
	}

	if ( !WM_acceptsCookies ) {
		alert( "In order to set your default edition you must accept cookies." );
	} else {
		pollSubPop('/virtual/editions/europe/2000/roof/change.pop/frameset.exclude.html','defaultpopup','scrollbars=yes,width=510,height=400');
	}
}

