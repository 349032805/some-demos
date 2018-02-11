
let totalArray = []
let tommorrow, today
var newArray = [];
function splitArray(array) {
  //置空数组，防止重复加载数据
  totalArray = [];
  newArray = [];
  for (let i = 0; i < array.length; i++) {
    let transArray = array[i].split('-')
    reduceArray(transArray)
  }
  // let index = newArray.findIndex((value) => {
  //   alert(value)
  //   return value == "24:00"
  // })
  let index = -1;
  for(var i=0;i<newArray.length;i++){
    if(newArray[i] == '24:00'){
      index == 0;
      break;
    }
  }
    console.log(newArray)
  if (index != -1) {
    newArray.splice(index, 1, "00:00")
    tommorrow = newArray.splice(index)
    
    today = newArray.filter(item => {
      return item > isbigNowTime()

    })
     for(let i = 0;i<today.length;i++){
       totalArray.push({time:today[i],date:'today'})
    }
    for(let i = 0;i<tommorrow.length;i++){
       totalArray.push({time:tommorrow[i],date:'tommorrow'})
    }
   
  }else{
    today = newArray.filter(item => {
      return item > isbigNowTime()

    })
    for(let i = 0;i<today.length;i++){
       totalArray.push({time:today[i],date:'today'})
    }
  }

  return totalArray
}


function reduceArray(transArray) {
  let start, end
  start = transArray[0].substring(0, 5)
  end = transArray[1].substring(0, 5)
  end = isTrueTime(end)
  pushfirstTime(start, end)
}

function isTrueTime(end) {
  let flag = end.split(":")
  if (flag[1] == "59") {
    if (flag[0] == "23") {
      flag[0] = '24'
      flag[1] = "00"
    } else {
      flag[0] = parseInt(flag[0]) + 1
      if (flag[0] < 10) {
        flag[0] = "0" + flag[0]
      }
      flag[1] = "00"
    }
  } else if (flag[1] == "29") {
    flag[1] = "30"
  }
  flag = flag.join(":")
  return flag
}
function pushfirstTime(start, end) {
  var start = start
  var end = end
  newArray.push(start)
  pushnextTime(start, end)
}

function pushnextTime(start, end) {
  var start = start
  var next = ""
  var pre = ""
  var nextPre = ""
  var newStart = start.split(":")
  if (newStart[1] == "30") {
    next = "00"
    pre = parseInt(newStart[0]) + 1
    pre = addzero(pre)
    nextPre = pre + ":" + next
    newArray.push(nextPre)
  }
  if (newStart[1] == "00") {
    next = "30"
    pre = newStart[0]
    nextPre = pre + ":" + next
    newArray.push(nextPre)
  }
  equare(nextPre, end)

}

function addzero(pre) {
  if (pre < 10 && pre > 0) {
    return '0' + pre
  } else {
    return pre
  }
}


function equare(nextPre, end) {
  let pre = nextPre, ends = end

  if (pre == "24:00" && ends == "24:00") {
    return
  }
  if (pre == "24:00") {
    pre = "00:00"
  }
  if (pre != ends) {
    pushnextTime(pre, ends)
  }
}

function isbigNowTime() {
  var now = new Date()
  var hour = now.getHours()
  var minutes = now.getMinutes();
  if (hour < 10) {
    hour = "0" + hour
  }
  var nowTime = hour + ':' + minutes
  return nowTime

}


export { splitArray }
