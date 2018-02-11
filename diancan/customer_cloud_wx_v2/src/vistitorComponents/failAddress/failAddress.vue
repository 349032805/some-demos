<template>
  <div class="failAddress">
    <div class="box">
      <img src="../../assets/newlocation.png" alt="">
      <p>无法获取地址，请打开定位权限</p>
      <router-link tag ='button' to="/changeAddress" replace>点击尝试搜索位置</router-link>

    </div>
  </div>
</template>

<script>
  export default {
      name :　"failAddress"
  }
</script>
<style lang="less">
  @import './failAddress.less';
</style>
