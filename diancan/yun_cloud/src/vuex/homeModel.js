import {
    getAddress
} from '@/config/api.js';
const homeModel = {
    state: {
        CITY: "",
        ADDRESS: '',
        LOCATION: null,
        CITY_CODE: "",
    },
    mutations: {
        SAVE_CITY(state, city) {
            state.CITY = city
        },
        SAVE_ADDRESS(state, address) {
            state.ADDRESS = address
        },
        SAVE_LOCATION(state, params) {
            state.LOCATION = params
        },
        SAVE_CITY_CODE(state, cityCode) {
            state.CITY_CODE = cityCode
        }
    },
    actions: {
        async GET_ADDRESS({ commit }, location) {
            let { lng, lat } = location
            let subLoction = `${lng},${lat}`;
            let res = await getAddress(subLoction)
            let result = res.regeocode
            let stringlocation = JSON.stringify(location)
            sessionStorage.LOCATION = stringlocation
            sessionStorage.CITY_CODE = result.addressComponent.citycode
            sessionStorage.ADDRESS = result.formatted_address
            sessionStorage.CITY = result.addressComponent.province
            sessionStorage.firstLoad = 'false'
            commit('SAVE_LOCATION', location) //把径尾度给存起来
            commit('SAVE_CITY_CODE', result.addressComponent.citycode) //把城市码给存起来
            commit('SAVE_ADDRESS', result.formatted_address) //把城市的定位地址给存起来
            commit('SAVE_CITY', result.addressComponent.province) //把城市的定位地址给存起来

        }
    }
}


export { homeModel }