<template>
    <div class="searchOrder">
        <search class="crib-search" text='取消' placeholder='请输入商店名称' :fixed='true' :autoFocus='true' :inputValue.sync='value' @on-submit='submit'></search>
        <history-search v-show='history==true'></history-search>
        <div class="searchOrder_box" v-infinite-scroll="loadMore" infinite-scroll-disabled="loading" infinite-scroll-distance="10">
            <store-door class="store_door" v-for="(item,index) in storeDoor" :info='item' :key='index' @click.native="enterstore(item)"></store-door>
            <div class='add-box' v-show='add'>
               <load-more type='flower' ></load-more>
            </div>
            <p class="no-more" v-show='noMore'>已无更多商家</p>
            <div class="search-box" v-show='nosearch == true'>
                <img class='search-img' src="../../assets/search.png" alt="">
                <span>没有搜索结果</span>
            </div>
        </div>
    </div>
</template>

<script>
import {LoadMore} from '@/crib-zk';
import {mapState } from 'vuex'
import {Search} from "@/crib-zk"
import StoreDoor from '@/vistitorComponents/storeDoor/storeDoor.vue'
import HistorySearch from '@/vistitorComponents/historySearch/historySearch.vue'
export default {
    name: "searchOrder",
    created () {
          let location = JSON.parse(sessionStorage.LOCATION)
          this.$store.commit('SAVE_LOCATION',location)
    },
    components: {
        Search,
        StoreDoor,
        LoadMore,
        HistorySearch
    },
    computed : {
       ...mapState([
      'LOCATION'
    ])
    },
    data() {
        return {
            value: "",
            pageNum: 0,
            pageSize: 10,
            loading: true,
            storeDoor: [],
            add : false,
            noMore : false,
            nosearch : false,
            history : true
        }
    },
    methods: {
        submit () {
           this.$router.go(-1)
        },
        loadMore() {
            this.loading = true
            this.$http('get',`${this.baseUrl}/getShopSearch`,{
                shopName : this.value,
                longitude : this.LOCATION.lng,
                latitude : this.LOCATION.lat,
                pageNum : this.pageNum,
                pageSize : this.pageSize
            }).then(res=>{
                if(res.code == '200'){
                    if(res.model.length >=10){
                         this.loading= false
                         this.storeDoor = [...this.storeDoor,...res.model]
                         this.add = true;
                         this.pageNum ++
                    }else{
                         this.storeDoor = [...this.storeDoor,...res.model]
                        this.loading = true
                        this.add = false;
                        this.noMore = true
                    }
                }
            })
        },
        enterstore(store) {
            if(localStorage.historyStore){
                let historyStore = JSON.parse(localStorage.historyStore)
                let storeItem = store
                var choosed = historyStore.some(item=>{
                    return item.fsShopGUID == store.fsShopGUID
                })
                if(!choosed){
                    historyStore.push(store)
                    historyStore = JSON.stringify(historyStore)
                    localStorage.historyStore = historyStore
                }
            }else{
                let historyStore = []
                historyStore.push(store)
                historyStore = JSON.stringify(historyStore)
                localStorage.historyStore = historyStore
            }
            //this.$router.push({ path: "/menus" })
        },
        keyup(value) {
            this.history = false
            this.value = value;
            this.noMore= false;
            this.add = false;
            this.nosearch = false
            this.pageNum = 0;
            this.$http('get',`${this.baseUrl}/getShopSearch`,{
                shopName : this.value,
                longitude : this.LOCATION.lng,
                latitude : this.LOCATION.lat,
                pageNum : this.pageNum,
                pageSize : this.pageSize
            }).then(res=>{
                if(res.code == '200'){
                    this.storeDoor = res.model
                    if(res.model.length == 0){
                        this.nosearch = true
                    }
                    else if(res.model.length >=10){
                        this.pageNum+=1
                        this.loading = false
                        this.add = true
                    }else{
                        this.loading = true
                        this.noMore = true
                    }
                }
            })
        },
        cancel() {
            this.noMore= false;
            this.add = false;
            this.value = ""
        },
    },
    watch: {
        value(val, oldval) {
            if (val == "") {
                this.history = true
                this.storeDoor = []
                return 
            }
            this.keyup(val)
        }
    },
}
</script>

<style lang="less">
@import './searchOrder.less';
.crib-search{
    z-index: 1000;
}
</style>
