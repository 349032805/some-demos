<template>
  <div class="map-container" id="container"></div>
</template>

<script>
  export default {
    data () {
      return {
        location: [121.76,31.05]
      }
    },
    mounted () {
      this.$nextTick(()=>{
        this.$store.commit('SAVE_TITLE','测试高得地图');
        let _this = this;
        var map = new AMap.Map('container');
        map.setZoom(11);  //详细到第几层级
        map.setCenter(this.location);
        var marker = new AMap.Marker({
          position: _this.location,//marker所在的位置
          map: map//创建时直接赋予map属性
        });
        marker.setMap(map);
      })
    }
  }
</script>

<style lang="less">
  .map-container {
    width: 100%;
    height: 100%;
  }
</style>
