import {
  SAVE_LOCATION,
  SAVE_CITY_CODE,
  SAVE_ADDRESS,
  SAVE_CITY,
  SAVE_TITLE,
  CHANGE_EVALUATESTATUS,
  SAVE_SHOP_STATUS,
  CHANGE_SHARE_MODEL
} from './mutation-types.js'

export default {
  //存径尾度
  [SAVE_LOCATION](state, location) {
    state.LOCATION = location
  },
  //存城市码
  [SAVE_CITY_CODE](state,citycode){
    state.CITY_CODE = citycode
  },
  //存当前地址
  [SAVE_ADDRESS](state,address){
    state.ADDRESS = address
  },
  //存城市地址
  [SAVE_CITY] (state,city){
    state.CITY = city
  },
  [SAVE_TITLE] (state,title){
    state.TITLE = title
  },
  [CHANGE_EVALUATESTATUS] (state,number){
    state.EVALUATE_STATUS = number
  },
  [SAVE_SHOP_STATUS] (state,shopstatus){
    state.SHOP_STATUS = shopstatus
  },
  [CHANGE_SHARE_MODEL] (state,param) {
    state.SHARE_MODEL = param
  }
}
