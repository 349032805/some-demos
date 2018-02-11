<template>
  <div class="activity">
    <div class="row clearfix">
      <div class="outer">
        <div class="box" :class="whichColor">{{activity.logoName}}</div>
      </div>
      <p class="activity-message">{{activity.activityName}}</p>
    </div>
  </div>

</template>

<script>
  export default  {
    name : 'activity',
    props : {
      activity : Object
    },
    computed:{
      whichColor(){
        let code = this.activity.logoNameCode;
        console.log("code:"+code);
        if(code == '1'){
          return "green";
        }

        if(code == '2' || code == '3' || code == '4'){
          return "orange";
        }

        if(code == '5' || code == '6'){
          return "red";
        }
      }
    }
  }
</script>

<style lang="less" scoped>
  .activity{
    .row {
      font-size: 12px;
      color: #666;
      line-height: 22px;
      * {
        display: inline-block;
        vertical-align: text-bottom;
      }

      .outer {
        width: 7%;
        float: left;

        .box {
          width: 16px;
          height: 16px;
          line-height: 16px;
          text-align: center;
          border-radius: 2px;
          color: #fff;
          font-size: 12px;
        }

        .orange {
          background: #FA952F;
        }
        .green {
          background: #70BC46;
        }

        .red {
          background: #FF4D4D;
        }
      }

      .activity-message {
        font-size: 14px;
        color: #666;
        width: 93%;
        float: left;
      }
    }
  }
</style>
