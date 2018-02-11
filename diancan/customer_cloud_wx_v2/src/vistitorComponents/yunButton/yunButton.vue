<template>
  <div class="food_appraise">
    <button>{{text}}</button>
  </div>
</template>
<script>
  export default {
      name : 'yunButton',
      props : {
          text : String
      }
  }
</script>

<style lang="less" scoped>
@import 'yunButton.less';
</style>
