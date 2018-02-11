<template>
   <div class="activity"  >
        <ul class="row clearfix" v-for='(item,index) in orderActivityOutVoList'>
          <li style='width:80%'>
            <div class="outer">
              <div class="box" :class="whichColor(item.logoName)">{{item.logoName}}></div>
            </div>
            <p class="activity-message overflow">{{item.fsBargainName}}</p>
          </li>
          <li style='width:20%' class="discount-price"><span v-if="item.money!=0">-</span>¥{{item.money}}</li>
        </ul>
       
    </div>

</template>

<script>
  export default  {
    name : 'sendGoods',
    props : {
      orderActivityOutVoList : Array
    },
    methods:{
      whichColor(word){
        if(word == '新'){
          return 'green'
        }

        if(word == '减' || word == '折' || word == '赠'){
          return 'orange'
        }

        if(word == '送' || word == '惠'){
          return 'red'
        }
        
      }
    }
  }
</script>

<style lang="less" scoped>
  .activity {
    padding-top: 10px;
    padding-right:.5rem;
    .row {
      li {
        color: #666;
        line-height: 22px;
        float: left;

        * {
          display: inline-block;
          vertical-align: middle;
        }
        .outer {
          width: 7%;
          float: left;
          position: relative;
          top: -2px;

          .box {
            width: 14px;
            height: 14px;
            line-height: 14px;
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

      .discount-price {
        color: #FA952F;
        text-align: right;
      }
    }
  }
</style>
