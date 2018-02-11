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

/*
  获取店铺的祥情
  ------------------------------------
  params 参数解释 
  -> shopId  店铺id
*/
function getShopInfo(baseUrl,shopId) {
    return http('get', baseUrl + '/getShopInfo', { shopId: shopId })
}

//1：左右排版，2：上下排版
function getMenusTypeSetting(baseUrl,shopId) {
   return http('get',baseUrl + '/getMenusTypeSetting',{shopId:shopId})
}

/*
  获取店铺所有菜单明细
  -------------------------------------
  params 参数解释 
  -> shopId  店铺id
  -> tableId 桌号id
*/
function getShopMenu(baseUrl,shopId, tableId) {
    return http('get', baseUrl + '/getShopMenu', { shopId: shopId, tableId: tableId })
}



/*
  获取店铺活动明细
  --------------------------------------
  params 参数解释 
  -> shopId  店铺id
*/

function getShopActivityList(baseUrl,shopId) {
    return http('get', baseUrl + '/getShopActivityList', { shopId: shopId })
}


function getUserShopOrder(baseUrl,shopId) {
  return http('get',baseUrl +'/getUserShopOrder',{shopId:shopId, pageNum: 0,pageSize: 100})
} 


function getMenusItemChingList(baseUrl,shopId,chingItemInVoList) {
  return http('post',baseUrl+'/getMenusItemChingList',{shopId:shopId,chingItemInVoList:chingItemInVoList},'json')
}
export {getAddress,addressList, getShopInfo, getShopMenu,getMenusTypeSetting, getShopActivityList,getUserShopOrder,getMenusItemChingList }
