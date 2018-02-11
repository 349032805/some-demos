<template>
  <div class="done-store">
    <p v-show='collect == true'>
      <i>收藏的餐厅</i>
    </p>
    <div class='done-store_content'>
      <store-door v-for="(item2,index) in favoriteShopList" :info="item2" :key='index'></store-door>
    </div>
    <p v-show='done == true'>
      <i>点过的餐厅</i>
    </p>
    <div class='done-store_content'>
      <store-door v-for="(item2,index) in everShopList" class='store-padding' :info="item2" :key='index'></store-door>
    </div>
  </div>
</template>

<script>
import StoreDoor from '@/vistitorComponents/storeDoor/storeDoor.vue';
export default {

  components: {
    StoreDoor
  },
  props: {

    favoriteShopList: Array,
    everShopList: Array
  },
  data() {
    return {
      collect: false,
      done: false,
    }
  },
  watch: {
    favoriteShopList(val) {
      if (val) {
        this.collect = true
      } else {
        this.collect = false
      }
    },
    everShopList(val) {
      if (val) {
        this.done = true
      } else {
        this.done = false
      }
    }
  }
}
</script>

<style lang="less">
@import './doneStore.less';
</style>
