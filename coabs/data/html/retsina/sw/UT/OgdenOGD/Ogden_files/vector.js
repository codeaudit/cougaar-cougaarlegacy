conStant = "/WEATHER/cities/";
conStant1 = "/WEATHER/uv.report/regions/";
conStant2 = "/WEATHER/uv.report/regions/";
conStant3 = "/WEATHER/uv.report/regions/";
conStant4 = "/WEATHER/uv.report/regions/";
conStant5 = "/WEATHER/uv.report/regions/";
fileType = ".html";
windowFeatures = 'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,width=440,height=120';

function goVector (vectorChoice) {
	pickIt = document.go.vector.options[document.go.vector.selectedIndex].value;
	if (pickIt == "us.district") {
		location.href = "/WEATHER/html/WashingtonDC.html";
		}
		else {
			location.href = conStant + pickIt + fileType;
			}
	}

function goVector1 (vectorChoice) {
	pickIt1 = document.go1.vector1.options[document.go1.vector1.selectedIndex].value;
	if (pickIt1 == "us.district") {
		location.href = "/WEATHER/html/WashingtonDC.html";
		}
		else {
			location.href = conStant1 + pickIt1 + fileType;
			}
	}

function goVector2 (vectorChoice) {
	pickIt2 = document.go2.vector2.options[document.go2.vector2.selectedIndex].value;
	if (pickIt2 == "us.district") {
		location.href = "/WEATHER/html/WashingtonDC.html";
		}
		else {
			location.href = conStant2 + pickIt2 + fileType;
			}
	}

function goVector3 (vectorChoice) {
	pickIt3 = document.go3.vector3.options[document.go3.vector3.selectedIndex].value;
	if (pickIt3 == "us.district") {
		location.href = "/WEATHER/html/WashingtonDC.html";
		}
		else {
			location.href = conStant3 + pickIt3 + fileType;
			}
	}

function goVector4 (vectorChoice) {
	pickIt4 = document.go4.vector4.options[document.go4.vector4.selectedIndex].value;
	if (pickIt4 == "us.district") {
		location.href = "/WEATHER/html/WashingtonDC.html";
		}
		else {
			location.href = conStant4 + pickIt4 + fileType;
			}
	}

function goVector5 (vectorChoice) {
	pickIt5 = document.go5.vector5.options[document.go5.vector5.selectedIndex].value;
	if (pickIt5 == "us.district") {
		location.href = "/WEATHER/html/WashingtonDC.html";
		}
		else {
			location.href = conStant5 + pickIt5 + fileType;
			}
	}

function makeWindow (url) {
	var newWin = window.open (url, 'Cityfinder', windowFeatures);
    newWin.document.close();
    }
