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
  export default {
    data () {
      return {
        sex: 1
      }
    },
    created () {
      this.$store.commit('SAVE_TITLE','修改性别');
      let user = window.localStorage.getItem('user');
      user = JSON.parse(user);
      // this.sex = user.sex;
      if(user.sex ==0){
        this.sex = 1;
      }else{
        this.sex = user.sex;
      }
    },
    methods : {
      changeSex(sex){
        if(this.sex != sex){
          this.sex = sex;
          this._commit();
        }
      },
      _commit(){
        let _this = this;
        this.$http("post",this.baseUrl + '/updateUser',{sex: this.sex}).then((response) => {
          if(response.code==SUCCESS){
            _this.$toast("修改性别成功");
            _this.$router.go(-1);
          }
        });
      }
    }
  }
</script>

<style lang="less">
  @import '../../style/mixin.less';
  .update-sex {
    padding-top: 10px;
    .sex {
      background: #fff;
      padding-left: 10px;

      .row {
        height: 44px;
        line-height: 44px;
        font-size: 16px;
        clear: both;
        background: #fff;
        padding-right: 15px;

        .tick {
          float: right;
          img {
            width: 13px;
            height: 9px;
            vertical-align: middle;
          }
        }
      }
    }
  }
 
</style>
