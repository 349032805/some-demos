<template>
   <div class="service-record">
      <div class="no-record">
        <img src="../../assets/no_service.png">
        <p>您还没有服务记录</p>
      </div>
   </div>
</template>

<script>
  import { forbidWXshare } from "@/utils/forbidWXshare.js";
  export default {
    created () {
      this.$store.commit('SAVE_TITLE','服务记录');
      forbidWXshare();
    }
  }
</script>

<style lang="less">
 @import '../../style/mixin.less';
  .service-record {
    .no-record {
      width: 150px;
      height: 90px;
      margin: auto;
      padding-top: 5rem;
      text-align: center;
      font-size: 14px;
      color: #888;

      img {
        width: 50px;
        height: 50px;
        margin-bottom: 10px;
      }
    }
  }
 
</style>
