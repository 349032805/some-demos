<template>
  <div class="update-info">
    <div style="padding-left: .2rem;background: #fff;">
      <div class="header bottom-line">
        <p class="float-left">头像</p>
        <div class="float-right">
          <img src="../../assets/default_head.png" class="header-logo" v-if="!imgUrl">
          <img :src="imgUrl" class="header-logo" v-if="imgUrl">
          <img src="../../assets/arrow.png" class="arrow">
        </div>

        <vue-core-image-upload class="upload-block" text="" :crop="false" @imageuploaded="imageuploaded" inputAccept="image/*" :url="url" :data="auth" inputOfFile="headImg">
        </vue-core-image-upload>

      </div>
    </div>

    <div class="nickname-sex">
      <router-link to="/updateNickname">
        <div class="row bottom-line">
          <p class="float-left">昵称</p>
          <div class="float-right">
            <span class="col999">{{user.nickname?user.nickname:'游客'}}</span>
            <img src="../../assets/arrow.png" class="arrow">
          </div>
        </div>
      </router-link>

      <router-link to="/updateSex">
        <div class="row">
          <p class="float-left">性别</p>
          <div class="float-right">
            <span class="col999" v-if="user.sex == 0">未知</span>
            <span class="col999" v-if="user.sex == 1">男</span>
            <span class="col999" v-if="user.sex == 2">女</span>
            <img src="../../assets/arrow.png" class="arrow">
          </div>
        </div>
      </router-link>
    </div>

    <router-link to="/bindPhone">
      <div class="phone">
        <div class="row">
          <p class="float-left">手机号</p>
          <div class="float-right" style="letter-spacing:1px">
            <span class="col999" v-if="user.phone">{{user.phone}}</span>
            <span class="col999" v-if="!user.phone">未绑定</span>
            <img src="../../assets/arrow.png" class="arrow">
          </div>
        </div>
      </div>
    </router-link>

  </div>
</template>

<script>
import VueCoreImageUpload from 'vue-core-image-upload';
import { forbidWXshare } from "@/utils/forbidWXshare.js";
const SUCCESS = 200;
export default {
  components: {
    'vue-core-image-upload': VueCoreImageUpload,
  },
  data() {
    return {
      imgUrl: '',
      user: {},
      url: this.baseUrl + '/updateHeadImg',
      auth: { token: localStorage.token }
    }
  },
  created() {
    this.$store.commit('SAVE_TITLE', '修改资料');
    forbidWXshare();
    this.$http("get", this.baseUrl + '/getUserInfo').then((response) => {
      if (response.code == SUCCESS) {
        this.user = response.model;
        this.imgUrl = this.user.headImgUrl;
        window.localStorage.setItem('user', JSON.stringify(this.user));
      }
    });
  },
  methods: {
    imageuploaded(res) {
      if (res.code == SUCCESS) {
        this.imgUrl = res.model;
      }
    }
  }
}
</script>

<style lang="less">
@import './updateInfo.less';
</style>
