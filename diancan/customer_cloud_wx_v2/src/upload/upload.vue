<!-- http://vanthink-ued.github.io/vue-core-image-upload/index.html#/cn/home -->
<template>
   <div class="upload-test">
    <div class="test-view">
        <img src="../assets/default_head.png" class="upload-img" v-if="!imgUrl">
        <img :src="imgUrl" class="upload-img" v-if="imgUrl">
        <div class="center">
            <vue-core-image-upload
              class="upload-btn"
              text="测试上传"
              :crop="false"
              @imageuploaded="imageuploaded"
              inputAccept="image/*"
              url="http://101.198.151.190/api/upload.php" >
            </vue-core-image-upload>
        </div>
      </div>
   </div>
</template>

<script>
  import VueCoreImageUpload from 'vue-core-image-upload'
  export default {
    components: {
      'vue-core-image-upload': VueCoreImageUpload,
    },
    data() {
      return {
        imgUrl: '',
      }
    },
    methods: {
      imageuploaded(res) {
        if (res.errcode == 0) {
          this.imgUrl = res.data.src;
        }
      }
    }
  }
</script>

<style lang="less">
  .upload-test {
    position: relative;

    .test-view {
      position: fixed;
      top: 50px;
      left: 0;
      right: 0;
      margin: auto;
      text-align: center;
      width: 200px;
      height: 160px;
      .upload-img {
        height: 100px;
        width: 100px;
      }

      .upload-btn {
        width: 100px;
        height: 40px;
        line-height: 40px;
        text-align: center;
        border: none;
        border-radius: 4px;
        background: #2ecc71;
        margin: 10px auto;
        color: #fff;
      }
    }
  }
 
</style>
