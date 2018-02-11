<template>
  <div class="error">
    <img src="../../assets/error_warining.png">
    <div class="tip">
      <p>扫码失败,请重新扫码!</p>
    </div>
    <button type="button" @click="close()">重新扫描二维码</button>
  </div>
</template>

<script type="text/ecmascript-6">
  export default {
    methods: {
      close(){
        WeixinJSBridge.invoke('closeWindow',{},function(res){

        });
      }
    }
  };
</script>

<style lang="less" scoped>
  .error {
    padding-top: 160px;
    img {
      width: 75px;
      height: 75px;
      display: block;
      margin: 0 auto;
    }

    .tip {
      text-align: center;
      margin-top: 40px;
      p {
        font-size: 16px;
        color: #323232;
      }
    }
    button {
      width: 90%;
      height: 50px;
      font-size: 17px;
      color: #fff;
      background: #11bbb4;
      display: block;
      margin: 0 auto;
      margin-top: 46px;
      border-radius: 4px;
      letter-spacing: 1px;
    }
  }
  
</style>
