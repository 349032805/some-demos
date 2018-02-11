<template>
   <div class="service-record">
      <div class="no-record">
        <img src="../../assets/no_service.png">
        <p>您还没有服务记录</p>
      </div>
   </div>
</template>

<script>
  export default {
    created () {
      this.$store.commit('SAVE_TITLE','服务记录');
    }
  }
</script>

<style lang="less">
  .service-record {
    .no-record {
      width: 150px;
      height: 90px;
      position: fixed;
      left: 0;
      top: 0;
      right: 0;
      bottom: 0;
      margin: auto;
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
