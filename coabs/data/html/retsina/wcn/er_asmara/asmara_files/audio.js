   var audioWin;

function servAudio(url,alt_url,new_window_property){
   var browsername = navigator.appName;
   var browserversion = parseInt(navigator.appVersion);
   
   if(new_window_property){
      window_property = new_window_property;
  
   }
   else {
      window_property = 'toolbar=no,location=no,directoreis=no,status=no,menubar=no,scrollbars=no,resizable=no,height=388,width=197,top=10,left=10';
   }

   if(((browsername == "Netscape") && (browserversion >= 3)) || ((browsername == "Microsoft Internet Explorer") && (browserversion >= 3))) {
        audioWin = window.open (url ,"audio", window_property);
   }
   else
   {
      location.href= alt_url;
   }

}

function closeAudio(){
   this.window.close();
   } 


function updateParent(newURL){
    opener.document.location.href = newURL
	}