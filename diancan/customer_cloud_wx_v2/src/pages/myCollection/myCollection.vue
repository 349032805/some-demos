<template>
   <div class="my-collection" v-infinite-scroll="_loadList"
  infinite-scroll-disabled="loading" infinite-scroll-distance="10">
      <!-- <div class="item">
        <div class="store-logo">  
          <img src="../../assets/1.jpg">
        </div>
        <div class="store">
          <p class="store-name overflow">沙县国际(长泰广场店)</p>
          <p class="business-distance"><span style="margin-right: 10px;">火锅</span><span>5.5km</span></p>
        </div>

        <div class="pre-order">
          <div class="box">预点</div>
        </div>
      </div> -->

      <!-- <swipeout>
          <swipeout-item>
            <div slot='right-menu'>
                <swipeout-button class="cancel-collect">取消收藏</swipeout-button>
            </div>
            <div slot='content'>
                <div class="item">
                  <div class="store-logo">
                    <img src="../../assets/1.jpg">
                  </div>
                  <div class="store">
                    <p class="store-name overflow">沙县国际县国际(长泰广场店)</p>
                    <p class="business-distance"><span style="margin-right: 10px;">火锅</span><span>5.5km</span></p>
                  </div>

                  <div class="pre-order">
                    <div class="box">预点</div>
                  </div>
                </div>
            </div>
          </swipeout-item>
      </swipeout> -->

      <div v-for="(item,index) in collectionList">
          <swipeout @click.native='goDetail(item.fsShopGUID)'>
              <swipeout-item>
                <div slot='right-menu'>
                  <swipeout-button class="cancel-collect"  @click.stop.native='cancelCollect(index,item.fsShopGUID)'>取消收藏</swipeout-button>
                </div>
                <div slot='content'>
                    <div class="item">
                      <div class="store-logo">
                        <img :src="item.fsLogo" v-if="item.fsLogo">
                        <img src="../../assets/default_store.png" v-if="!item.fsLogo">
                      </div>
                      <div class="store">
                        <p class="store-name overflow">{{item.fsShopName}}</p>
                        <p class="business-distance">
                          <span style="margin-right: 10px;">{{item.fsCuisineSeries}}</span>
                          <span v-if="item.distance">{{item.distance}}km</span>
                          <span v-if="!item.distance">--km</span>
                        </p>
                      </div>

                      <div class="pre-order" v-if="item.orderType == 2">
                        <div class="box">预点</div>
                      </div>

                    </div>
                </div>
              </swipeout-item>
          </swipeout>
      </div>
      <load-more type='flower' :spinnerTipstyles="spinnerTipstyles" v-show='loadingTip'></load-more>
      <!-- 提示没有更多了 -->
      <div class="no-more" v-if="showNoMore && collectionList.length>0">没有更多收藏了 ~</div>
      
        <!-- 无记录提示 -->
      <div class="no-record" v-if="req == true && collectionList.length == 0">
        <img src="../../assets/no_collection.png">
        <p>您还没有收藏的餐厅</p>
      </div>

   </div>
</template>

<script>
  import {Swipeout,SwipeoutItem,SwipeoutButton} from '@/components/swipeout';
  import {LoadMore} from '@/crib-zk';
  const SUCCESS = 200;
  export default {
    components : {
      Swipeout,
      SwipeoutItem,
      SwipeoutButton,
      LoadMore
    },
    data () {
      return {
        longitude: '0', //经度
        latitude: '0', //维度
        collectionList: [],
        req: false, //是否请求过了
        pageNum: 0, // 当前第几页
        pageSize: 10, //一页的长度
        total: 0, //数据总数
        showNoMore: false,
        loading: true,
        loadingTip : false,
        spinnerTipstyles : {
          fontSize:'14px'
        }
      }
    },
    created () {
      // location : {
      //   lng : '124',
      //   lat : '123423'
      // }
      this.$store.commit('SAVE_TITLE','我的收藏');
      let myLocation = window.localStorage.getItem('location');
      if(myLocation){
        myLocation = JSON.parse(myLocation);
        this.longitude = myLocation.lng;
        this.latitude = myLocation.lat;
      }

      // 请求收藏列表
      this._loadList();
      
    },
    methods : {
      cancelCollect(index,storeId){
        this.collectionList.splice(index,1);
        this._cancelCollect(storeId);
      },
      goDetail(storeId){
         // this.$router.push({name:'storeDetail',params:{storeId:storeId}});
         this.$router.push({name:'menus',params:{shopId:storeId}});
      },
      _loadList(){
        let _this = this;
        this.loading = true;
        this.$http("get",this.baseUrl + '/getUserShopCollection',
          {
            longitude: _this.longitude,
            latitude: _this.latitude,
            pageNum: _this.pageNum,
            pageSize: _this.pageSize
          }).then((response) => {
          if(response.code==SUCCESS){
            _this.collectionList = _this.collectionList.concat(response.model);
            _this.req = true;

            // _this.total = response.extra.total;
            if(response.model.length >= _this.pageSize){
              _this.pageNum += 1;
              _this.loading= false;
              _this.loadingTip = true
            }else{
              _this.showNoMore = true;
              _this.loading = true;
              _this.loadingTip = false
            }
            
          }
        });
      },
      _cancelCollect(storeId){
        console.log("取消收藏的门店id:"+storeId);
        this.$http("post",this.baseUrl + '/removeShopCollection',{shopId: storeId}).then((response) => {
          if(response.code==SUCCESS){
            console.log("取消收藏成功");
          }
        });
      }
    }
  }
</script>

<style lang="less">
  @import './myCollection.less';
</style>
