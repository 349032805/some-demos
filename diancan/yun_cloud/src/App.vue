<template>
  <div id="app">
      <transition name='router-fade' mode='out-in'>
         <router-view></router-view>
      </transition>
  </div>
</template>

<script>
import Vue from 'vue'
export default {
  name: 'app'
}
</script>

<style lang="less">
@import 'style/common';
@import 'style/rest';
</style>
