import Vue from 'vue'
import Vuex from 'vuex'
import { titleModel } from './titleModel'
import { foodAttrModel } from './foodAttrModel.js'
import { menusModel } from './menusModel.js'
import { personCenterModel } from './personCenterModel.js'
import { activityModel } from './activityModel.js'
import {homeModel} from './homeModel.js'
import {shareModel} from './shareModel.js'
Vue.use(Vuex)
export default new Vuex.Store({
    modules: {
        titleModel,
        foodAttrModel,
        menusModel,
        personCenterModel,
        activityModel,
        homeModel,
        shareModel
    }
})