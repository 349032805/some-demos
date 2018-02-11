import http from './axios.js'
import mapAxios from './mapAxios.js'
//获取当前地址位置
function getAddress(location) {

  return mapAxios('get','https://restapi.amap.com/v3/geocode/regeo',{output:"json",location : location,key : "a064595ce2a07c934f505a79e6cf5272",radius:"1000",extensions:"all"})

}
//获取位置列表接口
function addressList (value,city) {

  return mapAxios('get','https://restapi.amap.com/v3/place/text',{keywords : value,city:city, output : "json", key : "a064595ce2a07c934f505a79e6cf5272"})
}
export { getAddress ,addressList}
