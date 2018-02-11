<template>
  <div class="store_table" ref="store_table">
    <sticky>
      <div class="table_bar">
        <span @click="table1" :class="[type==1?left_bar : '']">附近餐厅</span>
        <span @click="table2" :class="[type==2?right_bar : '']">餐厅足迹</span>
      </div>
    </sticky>
    <div class="table_content" v-if="type==1" v-infinite-scroll="loadMore" infinite-scroll-disabled="loading" infinite-scroll-distance="1">
      <div>
        <store-door v-for='(item,index) in storeDoor' :info="item" :key='index' style='margin-bottom:8px;' ></store-door>
        <div class="addMore" v-show="addMore">
          <load-more></load-more>
        </div>
        <p class="no-more" v-if="noMore">没有更多的商家了</p>
      </div>
      <no-store content='您所在城市还未开通点餐服务，敬请期待' v-show='nearStore == true'></no-store>
    </div>
    <div class="table_content" v-show="type == 2">
      <done-store :favoriteShopList='favoriteShopList'  :everShopList="everShopList"></done-store>
      <no-store content='您还没有去过的餐厅'  v-show='favStores == true'></no-store>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import {LoadMore} from '@/crib-zk'
import Sticky from '@/components/sticky/index.vue'
import StoreDoor from '@/vistitorComponents/storeDoor/storeDoor.vue'
import DoneStore from '@/vistitorComponents/doneStore/doneStore.vue'
import NoStore from '@/vistitorComponents/noStore/noStore.vue'
export default {
  name: 'storeTable',
  created() {
    //从搜索入口进来的从新给vux
    if (sessionStorage.LOCATION) {
      let location = JSON.parse(sessionStorage.LOCATION)
      console.log(location)
      this.$store.commit('SAVE_LOCATION', location)
    }
  },
   components: {
    NoStore,
    StoreDoor,
    DoneStore,
    Sticky,
    LoadMore
  },
  props: {
    type : null,
    list: {
      type: Array
    }
  },
  data() {
    return {
      nearStore : false,
      favStores : false,
      location: "",
      left_bar: 'left_bar',
      right_bar: 'right_bar',
      loading: true,
      storeDoor: [],
      pageNum: 0,
      pageSize: 10,
      addMore: false,
      noMore: false,
      favoriteShopList: [],
      everShopList: []

    }
  },
  methods: {
    table1() {
      this.$emit('update:type',1)
      this.scroll.call(this)
    },
    table2() {
      this.$emit('update:type',2)
      this.scroll.call(this)
    },
    favStore() {
      this.$http('get', `${this.baseUrl}/getShopFootprint`, {
        longitude: this.LOCATION.lng,
        latitude: this.LOCATION.lat,
        pageNum: 0,
        pageSize: 20
      }).then(res => {
        if (res.code == '200') {
          if(!res.model.favoriteShopList && !res.model.everShopList){
            this.favStores = true
            return 
          }
          let value = res.model
          this.favoriteShopList = value.favoriteShopList
          this.everShopList = value.everShopList
        }
      })
    },
    nearshop() {
      this.pageNum = 0;
      this.$http('get', `${this.baseUrl}/getNearShop`, {
        longitude: this.LOCATION.lng,
        latitude: this.LOCATION.lat,
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }).then(res => {
        if (res.code === 200) {
          if(res.model.length == 0){
             this.nearStore = true
             return   
          }
          let value = this.storeDoor = res.model
          if (value.length >= 10) {
            this.loading = false
            this.addMore = true
            this.pageNum++
          } else {
            this.loading = true
            this.noMore = true
          }
        }
      })
    },
    loadMore() {
      this.loading = true
      this.$http('get', `${this.baseUrl}/getNearShop`, {
        longitude: this.LOCATION.lng,
        latitude: this.LOCATION.lat,
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }).then(res => {
        if (res.code === 200) {
          let value = res.model
          this.storeDoor = [...this.storeDoor, ...res.model]
          if (value.length >= 10) {
            this.pageNum++
            this.loading = false
            this.addMore = true
          } else {
            this.loading = true
            this.addMore = false
            this.noMore = true
          }
        }
      })
    },
    scroll() {
      let windowScroll = document.body.scrollTop
      let setTop = this.$refs.store_table.offsetTop
      if (windowScroll >= setTop) {
        document.body.scrollTop = setTop - 1
      }
    }
  },
  computed: {
    ...mapState([
      'LOCATION'
    ])
  },
  watch: {
    LOCATION: {
      handler: function (val, oldval) {
        if (val != null) {
          this.nearshop();
          this.favStore()
        }
      }
    }
  }
 
}
</script>

<style lang="less">
@import "./storeTable.less";
</style>
