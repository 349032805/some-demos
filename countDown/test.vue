<template>
  <div class="example">
      <input type="test" placeholder="输入手机号" v-model="phone">
       <countDownBtn :second="10" v-on:send="getPhone" ref="countDownBtn"></countDownBtn>
  </div>
</template>

<script type="text/ecmascript-6">

    import countDownBtn from './countDownBtn';

    export default {
      data() {
          return {
            phone:""
          }
        },
        methods: {
          getPhone(){
            if(this.phone){
              this.$refs.countDownBtn.start = true;
              this.$emit('send');
            }else{
              this.$toast("请输入手机号");
            }
          }
        	
        },
       components: {
        countDownBtn
       }
      
    };
</script>

<style scoped>
 .example{
    text-align: center;
    padding-top:30px;
    position: relative;
 }

</style>
