<template>
  <div class="bind-phone">
    <div class="outer">
      <div class="row bottom-line">
        <input type="tel" placeholder="输入手机号" maxlength="11" v-model="telephone">
        <div class="send">
          <img src="../../assets/close_icon.png" class="clear-icon" v-show="showClearPhone" @click="clear('phone')">
          <countDownBtn v-on:send="getPhone" ref="countDownBtn"></countDownBtn>
        </div>
      </div>
      <div class="row">
        <input type="tel" placeholder="填写验证码" maxlength="4" v-model="verifyCode" style="width: 80%;">
        <div class="clear-input float-right" v-show="showClearVerifyCode" @click="clear('code')">
          <img src="../../assets/close_icon.png" class="clear-icon">
        </div>
      </div>
    </div>

    <div class="agree">
      <div class="agree-box" @click="optAgree">
        <img src="../../assets/agree_deal.png" v-show="agree">
      </div>
      <span class="agree-deal">同意百味云
        <span style="color: #336CAE" @click="gotoAgreement">《用户协议》</span>
      </span>
    </div>

    <button type="button" class="sure-btn" :class="{'active-btn': isDisabled==false}" @click="bindPhone()" :disabled="isDisabled">
      <img src="../../assets/loading.gif" class="loading-icon" v-if="showLoading"> 确 认
    </button>

    <div class="bg-layer" v-show="showMessageBox">
      <div class="message-box">
        <img src="../../assets/success_icon.png">
        <span style="vertical-align: middle;">绑定成功</span>
      </div>
    </div>

  </div>
</template>

<script>    
import countDownBtn from '@/components/countDownBtn/countDownBtn';
import { forbidWXshare } from "@/utils/forbidWXshare.js";

const SUCCESS = 200;
export default {
  data() {
    return {
      telephone: "",
      verifyCode: "",
      isDisabled: true,
      showLoading: false,
      showMessageBox: false,
      agree: true
    }
  },
  created() {
    this.$store.commit('SAVE_TITLE','绑定手机');
    forbidWXshare();
  },
  methods: {
    getPhone() {
      let flag = this._verifyPhone();
      if (flag) {
        //按钮倒计时
        this.$refs.countDownBtn.start = true;
        this.$emit('send');

        //发送短信
        this._sendMessage();
      }
    },
    clear(obj) {
      if (obj == "phone") {
        this.telephone = "";
      } else {
        this.verifyCode = "";
      }

    },
    bindPhone() {
      let flag = this._verifyPhone();
      if (!flag) {
        return;
      }
      if (!this.verifyCode) {
        this.$toast("请输入验证码");
        return;
      }
      //绑定手机号
      this._commit();
    },
    _verifyPhone() {
      let telephone = this.telephone;
      let flag = true;
      if (telephone) {
        if (!/^1\d{10}$/gi.test(telephone)) {
          this.$toast("手机号不合法");
          flag = false;
        }
      } else {
        this.$toast("请输入手机号");
        flag = false;
      }

      return flag;
    },
    _sendMessage() {
      console.log("发送短信:" + this.telephone);
      this.$http("get", this.baseUrl + '/phoneCodes', { phone: this.telephone });
    },
    _commit() {
      console.log("绑定手机:" + this.telephone);
      this.showLoading = true;
      this.$http("post", this.baseUrl + '/bindUserPhones', { phone: this.telephone, code: this.verifyCode }).then((response) => {
        this.showLoading = false;
        if (response.code == SUCCESS) {
          this.$toast("绑定手机成功");
          this.$router.go(-1);
        } else {
          this.$toast(response.message);
        }
      });

    },
    optAgree() {
      this.agree = !this.agree;
      if (!this.agree) {
        this.isDisabled = true;
      } else {
        if (this.telephone.length > 0) {
          this.isDisabled = false;
        }

      }
    },
    gotoAgreement() {
      this.$router.push({ name: 'userAgreement2' })
    }
  },
  computed: {
    showClearPhone() {
      // if(this.telephone.length > 0){
      //   return true;
      // }else{
      //   return false;
      // }

      if (this.telephone.length > 0 && this.telephone.length < 11) {
        return true;
      } else {
        return false;
      }

    },
    showClearVerifyCode() {
      // if(this.verifyCode.length > 0){
      //   return true;
      // }else{
      //   return false;
      // }

      if (this.verifyCode.length > 0 && this.verifyCode.length < 4) {
        return true;
      } else {
        return false;
      }
    }

  },
  watch: {
    telephone(val) {
      if (val.length > 0) {
        if (this.agree) {
          this.isDisabled = false;
        }

      } else {
        this.isDisabled = true;
      }
    }
  },
  components: {
    countDownBtn
  }
}
</script>

<style lang="less">
@import './bindPhone.less';
</style>
