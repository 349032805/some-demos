<template>
  <div id="container"></div>
</template>

<script>
  export default {
    data () {
      return {
        // location: [121.76,31.05]
        location: []
      }
    },
    mounted () {
      this.$nextTick(()=>{
        this.$store.commit('SAVE_TITLE','餐厅坐标');
        let longitude = this.$route.params.longitude;
        let latitude = this.$route.params.latitude;
        console.log("经度:"+longitude);
        console.log("纬度:"+latitude);
        this.location.push(longitude);
        this.location.push(latitude);
      
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
  #container {
    width: 100%;
    height: 100%;
  }
</style>
