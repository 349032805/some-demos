<template>
  <div class="vux-x-dialog" @touchmove="onTouchMove">
      <transition :name="maskTransition" >
          <div class="weui-mask" v-show="value"></div>
      </transition>
      <transition :name="dialogTransition">
          <div class="weui-dialog" v-show="value">
             <slot></slot>
          </div>
      </transition>
  </div>
</template>


<script>
export default {
    props : {
        //是否显示的值
        value : {
            type :Boolean,
            default : false
        },
        //遮罩动画
        maskTransition:{
            type : String,
            default :'vux-mask'
        },
        //对话框架的动画
        dialogTransition : {
            type : String,
            default : 'vux-dialog'
        },
        //是否阻止内层滚动
        scroll: {
          type: Boolean,
          default: false
        },
        //对话框架盒子样式
        dialogStyle: Object,
    },
    methods: {
      onTouchMove: function (event) {
        !this.scroll && event.preventDefault()
      }
    }
}

</script>

<style lang="less">
  @import '../style/transition.less';
.weui-mask {
  position: fixed;
  z-index: 1000;
  top: 0;
  right: 0;
  left: 0;
  bottom: 0;
  background: rgba(0, 0, 0, .3);
}
  .weui-dialog {
    position: fixed;
    z-index: 5000;
    width: 80%;
    max-width: 300px;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);

    background-color: #fff;
    text-align: center;
    border-radius: 3px;
    overflow: hidden;
  }
.weui-mask_transparent{
  position: fixed;
  z-index: 1000;
  top: 0;
  right: 0;
  left: 0;
  bottom: 0;
}
</style>
