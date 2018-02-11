export function getSys() {
  let sys = "";
  let u = navigator.userAgent;
  let isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
  let isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
  if(isAndroid){
    sys = "android";
  }else{
    sys = "ios";
  }
  console.log("当前手机"+sys+"系统");
  return sys;
};