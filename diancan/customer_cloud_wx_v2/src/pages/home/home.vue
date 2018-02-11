<template>
  <div class="home">
    <router-link to="/personCenter">
    </router-link>
    <div class="top">
      <router-link to="/ChangeAddress" tag='div' class="left-top">
        <img class="location" src="../../assets/location.png" alt="">
        <span class="content">{{ADDRESS}}</span>
        <img class="arrow" src="../../assets/arrow.png" alt="">
      </router-link>
      <router-link tag='div' class="searchStore" to="/searchOrder">
        <img src="../../assets/search-small.png" alt="">
        <span>输入商家名称</span>
      </router-link>
    </div>
    <swiper v-if='this.swiperList.length>0' :type='1'  :swiperList='swiperList' style="height:6rem;"></swiper>
    <store-table :store-door="storeList" :type.sync='type'></store-table>
    <!--<div class="bar"></div>-->
    <fail-address v-if="failAddress"></fail-address>
  </div>
</template>

<script>
import { mapMutations, mapState } from 'vuex'
import { getLocation,GetQueryString } from '@/config/common.js'
import FailAddress from '@/vistitorComponents/failAddress/failAddress.vue'
import StoreTable from '@/vistitorComponents/storeTable/storeTable.vue'
import Swiper from '@/vistitorComponents/swiper/swiper.vue'
export default {
  name: 'home',
  created() {
    this.$store.commit('SAVE_TITLE', '百味云点餐系统')
    sessionStorage.homeType = this.$route.query.type
    this.type = sessionStorage.homeType
    localStorage.token = this.$route.query.token
    
    this.$http('get',`${this.baseUrl}/getUserShopOrder`,{
      pageNum :0,
      pageSize:100
    }).then(res=>{
      if(res.code == 200){
        this.swiperList = res.model;

      }
    })

    if(this.LOCATION != null){
      return 
    }
    this.$indicator.open('正在定位中...')
    getLocation((lng, lat) => {
      this.$indicator.close();
      let location = {
        lng: lng,
        lat: lat
      }
      this.$store.dispatch('GET_ADDRESS', location)
      
    }, () => {
      this.$indicator.close();
      this.failAddress = true
      this.$store.commit('SAVE_ADDRESS','定位失败')
    })
  },
  components: {
    FailAddress,
    StoreTable,
    Swiper
  },
  data() {
    return {
      type : "",
      swiperList : [],
      storeList: [],
      failAddress: false,
      pageNum: 0,
      pageSize: 3,
      loading: false
    }
  },
  computed: {
    ...mapState([
      'ADDRESS', 'LOCATION'
    ])
  },
  methods: {
    //      loadMore () {
    //          console.log(1)
    //      }
  }
}
</script>

<style lang="less" scoped>
@import './home.less';
</style>




























