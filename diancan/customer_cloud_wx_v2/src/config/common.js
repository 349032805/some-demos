function getLocation(cb1,cb2) {
  if (navigator.geolocation){
    navigator.geolocation.getCurrentPosition(position=>{
      
      let location = position.coords
      let [lng,lat] = [location.longitude,location.latitude]
      cb1(lng,lat)
    },error=>{
      console.log(error)
      cb2()
    },{timeout:5000});
  }else{
    this.$store.commit('SAVE_ADDRESS','')
    alert("浏览器不支持地理定位。");
   }
}



function GetQueryString(name)
{
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null)return  unescape(r[2]); return null;
}

export {getLocation ,GetQueryString}
