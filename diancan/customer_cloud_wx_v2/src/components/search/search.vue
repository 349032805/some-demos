<template>
  <div class="search">
    <div class="inputBox">
      <input type="text" :placeholder="palceholder" v-model="value" @keyup="keyup">
    </div>
    <p class="cancel" @click="cancel">取消</p>
  </div>
</template>

<script>
  export default {
      name : "search",
      props : {
          palceholder : {
              type : String,
              default : '请输入地址'
          }
      },
      data () {
        return {
            value : ""
        }
      },
      methods : {
        keyup () {
            this.$emit('keyup',this.value)
        },
        cancel () {
            this.value=""
            this.$emit('cancel')
        }
      }
  }
</script>

<style lang="less" scoped>
  @import './search.less';
</style>
