<template>
   <div class="update-nickname">
      <div class="row">
        <input type="text" placeholder="请输入昵称" maxlength="10" v-model="nickname">
        <div class="clear-input" @click="clear()">
          <img src="../../assets/close_icon.png" v-show="showClear">
        </div>
      </div>

       <button type="button" class="sure-btn" @click="updateNickname()">确 认</button>
   </div>
</template>

<script>
 import axios from 'axios'
 const SUCCESS = 200;
  export default {  
    data () {
      return {
        nickname: ""
      }
    },
    created () {
      this.$store.commit('SAVE_TITLE','修改昵称')
    },
    methods : {
      clear(){
        this.nickname = "";
      },
      updateNickname(){
        if(!this.nickname){
          this.$toast("请输入昵称");
          return;
        }

        //提交
        console.log("昵称:"+this.nickname);
        let _this = this;
        this.$http("post",this.baseUrl + '/updateUser',{nickname: this.nickname}).then((response) => {
          if(response.code==SUCCESS){
            _this.$toast("修改昵称成功");
            _this.$router.go(-1);
          }
        });

      }
    },
    computed: {
      showClear(){
        if(this.nickname.length > 0){
          return true;
        }else{
          return false;
        }
      }
    }
  }
</script>

<style lang="less">
  @import '../../style/mixin.less';
  .update-nickname {
    padding-top: 10px;
   
    .row {
      height: 44px;
      line-height: 44px;
      font-size: 16px;
      background: #fff;
      padding-left: 15px;
      padding-right: 15px;

      input {
        height: 100%;
        width: 80%;
        font-size: 16px;
      }

      .clear-input {
        float: right;
        img {
          width: 13px;
          height: 13px;
          vertical-align: middle;
        }
      }
    }

    .sure-btn {
      width: 92%;
      height: 47px;
      background: #06C1AE;
      border-radius: 4px;
      font-size: 18px;
      color: #fff;
      display: block;
      margin: 14px auto;
    }
  }
 
</style>
