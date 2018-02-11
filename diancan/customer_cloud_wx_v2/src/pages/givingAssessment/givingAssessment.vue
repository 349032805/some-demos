<template>
  <div class="givingAssessment">
    <p class="fav-food">点击图片选择您喜欢的菜品</p>
    <div ref="content">
      <ul>
        <li v-for="(item,index) in list">
          <div class="img-box" @click="choosed(index)">
            <div class="choose" v-show="item.flag">
              <img src="../../assets/choose-y.png" alt="">
            </div>
            <img v-if='item.fsimageurl' v-lazy="item.fsimageurl" alt="">
            <img v-else src="../../assets/nodish-big.png" alt="">
          </div>
          <p>
            <span style='float:left'>{{item.fsitemname}}</span>
            <span style="color:#999;font-size:.55rem;float:right">{{item.price}}/{{item.unit}}</span>
          </p>
        </li>
      </ul>
      <div class="zbutton-box">
        <z-button :styles='btnStyles' @click.native="submit">提交</z-button>
      </div>
    </div>
  </div>
</template>

<script>

import YunButton from '@/vistitorComponents/yunButton/yunButton.vue'
import { ZButton } from '@/crib-zk'
import { mapState } from 'vuex'
export default {
  created() {
    this.$store.commit('SAVE_TITLE', '菜品评价')
    let fsShopGuid = this.fsShopGuid = this.$route.params.fsShopGuid
    this.orderId = sessionStorage.orderId
    if(sessionStorage.type == 1){
       this.url = "getOrderDishInWeiXin"
       this.submitUrl = 'addDishEvaluateInWeiXin'
       this.shopId = sessionStorage.storeId
    }else{
       this.url = "getOrderDish"
       this.submitUrl = 'addDishEvaluate'
    }
    
    this.$http('get', `${this.baseUrl}/${this.url}`, {
      orderId: this.orderId,
      shopId : this.shopId
    }).then(res => {
      this.list = res.model
      this.list.forEach((item) => {
        this.$set(item, 'flag', false)
      })
    })
  },
  components: {
    ZButton
  },
  computed: {
    ...mapState([
      'ORDER_INFO'
    ])
  },
  data() {
    return {
      shopId : "",
      url : "",
      submitUrl : "",
      orderId: "",
      fsShopGuid: "",
      list: [],
      btnStyles: {
        color: '#505050',
        background: '#FFFFFF',
        border: '1px solid #DCDCDC',
        height: '44px',
        borderRadius: '4px',
        fontSize: '.68rem'
      }
    }
  },
  methods: {
    choosed(index) {
      this.list[index].flag = !this.list[index].flag
    },
    submit() {
      let chooseList = this.list.filter((item) => {
        return item.flag == true
      })
      chooseList.forEach((item) => {
        item.orderId = this.orderId
        item.fsShopGuid = this.fsShopGuid
      })
      let evaluateInVoList = chooseList
      this.$http('post', `${this.baseUrl}/${this.submitUrl}`,
        evaluateInVoList, 'json'
      ).then(res => {
        if (res.code == 200) {
          this.$router.replace({name:'assessmentDetail'})
        }
      })
    }
  }
}
</script>

<style lang="less" scoped>
@import './givingAssessment.less';
</style>
