<template>
  <button type="button" class="btn btn-info" :class="disabledStatus" @click="send" :disabled="start && time > 0">
    {{ text }}
  </button>
</template>

<script type="text/ecmascript-6">
  export default {
    props: {
      second: {
        type: Number,
        default: 60
      }
    },
    data() {
      return {
        time: 0,
        start: false
      }
    },
    methods: {
      send() {
        this.$emit('send')
        if(this.start){
            this.time = this.second
            this.timer()
        }
      },
      timer() {
        if (this.time > 0) {
          this.time = this.time - 1
          setTimeout(this.timer, 1000)
          this.$parent.canWrite = true
        }
      }
    },
    computed: {
    text() {
        if(this.start && this.time > 0){
          return ' 重新获取(' + this.time + 's)';
        }else{
          this.start = false;
          this.$parent.canWrite = false
          return '获取验证码';
        }
      },
      disabledStatus(){
        if(this.start && this.time > 0){
          return "disabled-btn";
        }else{
          return "";
        }
      }
    }
  };
</script>

<style lang="less">
  .disabled-btn {
    background: #bbb!important;
    color: #fff!important;
  }

</style>
