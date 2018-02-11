var newArray=[];
function splitArray(array) {
  for(let i = 0; i < array.length; i++){
    let transArray = array[i].split('-')
    reduceArray(transArray)
  }
  console.log(newArray)
  return newArray= newArray.filter(item=>{
    return  item > isbigNowTime() 
  })
}


function reduceArray (transArray) {
  let start,end
  start = transArray[0].substring(0,5)
  end = transArray[1].substring(0,5)
  pushfirstTime(start,end)
}


function pushfirstTime (start,end) {
 var start = start
 var end = end
 newArray.push(start)
 pushnextTime(start,end)
}

function pushnextTime (start,end) {
 var start =start
 var next = ""
 var pre = ""
 var nextPre = ""
 var newStart = start.split(":")
 if(newStart[1] == "30"){
   next = "00"
   pre = parseInt(newStart[0]) + 1
   pre = addzero(pre)
   nextPre = pre+":"+next
   newArray.push(nextPre)
 }
 if(newStart[1] == "00"){
   next = "30"
   pre = newStart[0]
   nextPre = pre+":"+next
   newArray.push(nextPre)
 }
 equare(nextPre,end)

}

function addzero (pre) {
    if(pre<10 && pre>0){
      return '0'+pre
    }else{
      return pre
    }
}


function equare (nextPre,end) {
 if(nextPre !=  end){
   pushnextTime(nextPre,end)
 }
}

function isbigNowTime() {
 var now = new Date()
 var hour = now.getHours()
 var minutes = now.getMinutes();
 if(hour<10){
    hour = "0"+hour
 }
 var nowTime = hour+':'+minutes
 return  nowTime

}

export {splitArray}
