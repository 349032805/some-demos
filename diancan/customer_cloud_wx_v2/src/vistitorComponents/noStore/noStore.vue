<template>
  <div class="no-store">
      <img src="../../assets/01.png" alt="">
      <p>{{content}}</p>
  </div>
</template>

<script>
export default {
   name : 'noStore',
   props : {
       content : String
   }
}
</script>
<style lang='less'>
@import './noStore.less';
</style>


