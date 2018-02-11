<template>
  <div class="vux-marquee" :style="{height: height + 'px'}">
    <ul class="vux-marquee-box" ref="box" :style="{transform: `translate3d(0,${currenTranslateY}px,0)`, transition: `transform ${noAnimate ? 0 : duration}ms`}">
      <slot></slot>
    </ul>
  </div>
</template>

<script>
export default {
  props: {
    interval: {
      type: Number,
      default: 2000
    },
    duration: {
      type: Number,
      default: 300
    },
    direction: {
      type: String,
      default: 'up'
    }
  },
  beforeDestroy () {
    this.destroy()
  },
  data () {
    return {
      currenTranslateY: 0,
      height: '',
      length: 0,
      currentIndex: 0,
      noAnimate: false
    }
  },
  methods: {
    destroy () {
      this.timer && clearInterval(this.timer)
    },
    init () {
      this.destroy()

      if (this.cloneNode) {
        this.$refs.box.removeChild(this.cloneNode)
      }

      this.cloneNode = null
      let firstItem = this.$refs.box.firstElementChild //获取box节点下的第一个元素
      if (!firstItem) {
        return false
      }    //如果没有li元素则退出
      this.length = this.$refs.box.children.length //获得ul下li节点的长度
      this.height = firstItem.offsetHeight
      if (this.direction === 'up') {
        this.cloneNode = firstItem.cloneNode(true)
        this.$refs.box.appendChild(this.cloneNode)
      } else {
        this.cloneNode = this.$refs.box.lastElementChild.cloneNode(true)
        this.$refs.box.insertBefore(this.cloneNode, firstItem)
      }
    },
    start () {
      if (this.direction === 'down') this.go(false) //对初始样式方向
      this.timer = setInterval(() => {
        if (this.direction === 'up') {
          this.currentIndex += 1
          this.currenTranslateY = -this.currentIndex * this.height
        } else {
          this.currentIndex -= 1
          this.currenTranslateY = -(this.currentIndex + 1) * this.height
        }
        if (this.currentIndex === this.length) {
          setTimeout(() => {
            this.go(true)
          }, this.duration)
        } else if (this.currentIndex === -1) {
          setTimeout(() => {
            this.go(false)
          }, this.duration)
        } else {
          this.noAnimate = false
        }
      }, this.interval + this.duration)
    },
    go (toFirst) {
      this.noAnimate = true
      if (toFirst) {
        this.currentIndex = 0
        this.currenTranslateY = 0
      } else {
        this.currentIndex = this.length - 1  //当我们向下marquee的时候，此时最后一个下标为总长度-1
        this.currenTranslateY = -(this.currentIndex + 1) * this.height
        //因为如果向下的话，我们在li的最项部插入了最后一dom此时我们要+1
      }
    }
  }
}
</script>
<style lang="less">
.vux-marquee {
  width:70% ;
  overflow:hidden;
}
.vux-marquee-box {
  padding: 0;
  margin: 0;
  width: 100%;
  height: auto;

  li {
    margin: 0;
    width: 100%;
    padding:10px 0;
    box-sizing:border-box;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
  }
}
</style>