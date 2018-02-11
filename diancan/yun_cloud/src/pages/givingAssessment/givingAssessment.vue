<template>
  <div class="givingAssessment">
    <p class="fav-food">点击图片选择您喜欢的菜品</p>
    <div ref="content">
      <ul>
        <li v-for="(item,index) in list" :key="index">
          <div class="img-box" @click="choosed(index)">
            <div class="choose" v-show="item.flag">
              <img src="../../assets/choose-y.png">
            </div>
            <img v-if='item.fsimageurl' v-lazy="item.fsimageurl">
            <img v-else src="../../assets/nodish-big.png">
          </div>
          <p>
            <span>{{item.fsitemname}}</span>
            <span>¥{{item.price}}</span>
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
import YunButton from './yunButton/yunButton.vue'
import { ZButton } from '@/crib-zk'
import { mapState } from 'vuex'
export default {
  created() {
      this.$store.commit('SAVE_TITLE', '菜品评价')
      let fsShopGuid = this.fsShopGuid = this.$route.params.fsShopGuid
      this.orderId = sessionStorage.orderId
      this.shopId = sessionStorage.storeId
      this.$http('get', `${this.baseUrl}/getOrderDish`, {
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
      orderId: "",
      fsShopGuid: "",
      list: [],
      btnStyles: {
        color: '#505050',
        background: '#fafafa',
        border: '1px solid #DCDCDC',
        height: '44px',
        borderRadius: '4px',
        fontSize: '.32rem'
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
      this.$http('post', `${this.baseUrl}/addDishEvaluate`,
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
