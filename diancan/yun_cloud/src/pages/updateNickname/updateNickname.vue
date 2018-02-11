<template>
  <div class="update-nickname">
    <div class="row">
      <input type="text" placeholder="请输入昵称" maxlength="10" v-model="nickname">
      <div class="clear-input" @click="clear()">
        <img src="../../assets/close_icon.png" v-show="showClear">
      </div>
    </div>

    <button type="button" class="sure-btn" @click="updateNickname()">完 成</button>
  </div>
</template>

<script>
const SUCCESS = 200;
import { forbidWXshare } from "@/utils/forbidWXshare";
export default {
  data() {
    return {
      nickname: ""
    }
  },
  created() {
    this.$store.commit('SAVE_TITLE', '修改昵称');
    forbidWXshare();
  },
  methods: {
    clear() {
      this.nickname = "";
    },
    updateNickname() {
      if (!this.nickname) {
        this.$toast("请输入昵称");
        return;
      }

      //提交
      console.log("昵称:" + this.nickname);
      this.$http("post", this.baseUrl + '/updateUser', { nickname: this.nickname }).then((response) => {
        if (response.code == SUCCESS) {
          this.$toast("修改昵称成功");
          this.$router.go(-1);
        }
      });

    }
  },
  computed: {
    showClear() {
      if (this.nickname.length > 0) {
        return true;
      } else {
        return false;
      }
    }
  }
}
</script>

<style lang="less">
@import '../../style/mixin.less';
.update-nickname {
  width: 100%;
  height: 100%;
  padding-top: .2rem;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 600;
  background: #f2f2f2;
  .row {
    height: .88rem;
    line-height: .88rem;
    font-size: .32rem;
    background: #fff;
    padding-left: .3rem;
    padding-right: .3rem;

    input {
      height: 100%;
      width: 80%;
      font-size: .32rem;
    }

    .clear-input {
      float: right;
      img {
        width: .26rem;
        height: .26rem;
        vertical-align: middle;
      }
    }
  }

  .sure-btn {
    width: 92%;
    height: .94rem;
    background: #F1503F;
    border-radius: 4px;
    font-size: .36rem;
    color: #fff;
    display: block;
    margin: .28rem auto;
  }
}
</style>
