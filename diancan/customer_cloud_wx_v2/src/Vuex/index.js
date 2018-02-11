Vue.use(Vuex)
import Vue from 'vue'
import Vuex from 'vuex'
import state from './state.js'
import getters from './getters'
import mutations from './mutations.js'
import actions from './actions.js'
export default new Vuex.Store({
  state,
  getters,
  mutations,
  actions
})
