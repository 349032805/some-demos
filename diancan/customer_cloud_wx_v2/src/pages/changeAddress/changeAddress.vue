<template>
  <div class="changeAddress">
    <div class="choose-country">
      <div class="choose-country_box">
        <span class="choose-country_item" @click='cityShow=!cityShow'>
          <span class='current-city'>{{currentCity}}</span>
          <img class='updown' :class='[cityShow?"":"rotate"]' src="../../assets/Triangle.png" alt="">
        </span>
        
        <span @click='enterSearchAddress' class="choose-country_item2">点击输入地址</span>
      </div>
    </div>
    <keep-alive>
      <components :is='currentView'></components>
    </keep-alive>
    <transition name="router-slid" mode="out-in">
      <router-view></router-view>
    </transition>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import SearchCountry from '@/vistitorComponents/searchCountry/searchCountry.vue'
import CurrentCountry from '@/vistitorComponents/currentCountry/currentCountry.vue'
export default {
  // beforeRouteLeave(to, from, next) {
  //   this.$store.commit('SAVE_TITLE', '搜索')
  //   next()
  // },
  created() {
    this.$store.commit('SAVE_TITLE', '切换地址')
    // if(sessionStorage.CITY){
    //    this.$store.commit('SAVE_CITY',sessionStorage.CITY)
    // }
    // this.$store.commit('SAVE_TITLE', '切换地址')
  },
  components: {
    SearchCountry,
    CurrentCountry
  },
  data() {
    return {
      currentView: SearchCountry,
      cityShow: true,
      addressList: [],

    }
  },
  computed: {
    ...mapState([
      'CITY', 'LOCATION'
    ]),
    currentCity() {
      if (this.CITY == "") {
        return '上海市'
      } else {
        return this.CITY
      }
    }
  },
  methods : {
     enterSearchAddress () {
       this.$router.push({path:'/searchAddress'})
    }
  },
  watch: {
    '$route' () {
      this.$store.commit('SAVE_TITLE', '切换地址')
    },
   
    cityShow(val) {
      if (val == true) {
        this.currentView = SearchCountry
      } else {
        this.currentView = CurrentCountry
      }
    }
  }
}
</script>
<style lang="less" scoped>
@import './changeAddress.less';
</style>
