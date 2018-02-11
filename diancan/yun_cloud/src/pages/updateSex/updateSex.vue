<template>
  <div class="update-sex">
    <div class="sex">
      <div class="row bottom-line" @click="changeSex(1)">
        <p class="float-left">男</p>
        <div class="tick" v-show="sex==1">
          <img src="../../assets/tick_icon.png">
        </div>
      </div>
      <div class="row" @click="changeSex(2)">
        <p class="float-left">女</p>
        <div class="tick" v-show="sex==2">
          <img src="../../assets/tick_icon.png">
        </div>
      </div>
    </div>
  </div>
</template>

<script>
const SUCCESS = 200;
import { forbidWXshare } from "@/utils/forbidWXshare.js";
export default {
  data() {
    return {
      sex: 1
    }
  },
  created() {
    this.$store.commit('SAVE_TITLE', '修改性别');
    forbidWXshare();
    let user = window.localStorage.getItem('user');
    user = JSON.parse(user);
    // this.sex = user.sex;
    if (user.sex == 0) {
      this.sex = 1;
    } else {
      this.sex = user.sex;
    }
  },
  methods: {
    changeSex(sex) {
      if (this.sex != sex) {
        this.sex = sex;
        this._commit();
      }
    },
    _commit() {
      this.$http("post", this.baseUrl + '/updateUser', { sex: this.sex }).then((response) => {
        if (response.code == SUCCESS) {
          this.$toast("修改性别成功");
          this.$router.go(-1);
        }
      });
    }
  }
}
</script>

<style lang="less">
@import '../../style/mixin.less';
.update-sex {
  padding-top: .2rem;
  .sex {
    background: #fff;
    padding-left: .2rem;

    .row {
      height: .88rem;
      line-height: .88rem;
      font-size: .32rem;
      clear: both;
      background: #fff;
      padding-right: .3rem;

      .tick {
        float: right;
        img {
          width: .26rem;
          height: .18rem;
          vertical-align: middle;
        }
      }
    }
  }
}
</style>
